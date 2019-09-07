package com.module;

import com.dao.CenterMailRepository;
import com.dao.MailRepository;
import com.entry.CenterMailEntry;
import com.entry.MailEntry;
import com.entry.po.MailPo;
import com.exception.StatusException;
import com.service.MailService;
import com.template.TemplateManager;
import com.template.templates.type.CenterMailType;
import com.template.templates.type.TipType;
import lombok.Getter;
import lombok.Setter;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Component
@Getter
@Setter
@Order(2)
public class MailModule extends BaseModule
{

    private MailEntry mailEntry;
    @Autowired
    private TemplateManager templateManager;

    @Autowired
    private CenterMailRepository centerMailRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private MailRepository mailRepository;

    @Override
    public void onLoad() {
        player.mailPart = this;
        Cache<Long, MailEntry> cache = cacheManager.getCache(getCacheName(), Long.class, MailEntry.class);
        mailEntry = cache.get(player.getPlayerId());
        if (Objects.isNull(mailEntry)) {
            mailEntry = new MailEntry(player.getPlayerId());
            cache.put(player.getPlayerId(), mailEntry);
        }
        scanCenterMail();

    }

    private void scanCenterMail() {
        List<CenterMailEntry> centerMails = mailService.findByDate();
        for (CenterMailEntry centerMail : centerMails) {
            //已经领过的
            if (mailEntry.getHasCenterMailIds().contains(centerMail.getId())) {
                continue;
            }
            //个人邮件或群体邮件，不是发给自己的
            if ((centerMail.getType() == CenterMailType.Personal || centerMail.getType() == CenterMailType.Multiple)
                    && !centerMail.getReceiverId().contains(player.getPlayerId())) {
                continue;
            }

            // 插入个人邮件
            MailPo mail = mailService.createMail(centerMail.getMailTemplateId());
            mailEntry.getMailList().add(mail);
        }
    }

    public void addMail(int mailTemplateId) {
        MailPo mail = mailService.createMail(mailTemplateId);
        mailEntry.getMailList().add(mail);
        //TODO 单条消息推送给前端
    }

    //邮件列表
    public List<MailPo> mailList() {
        List<MailPo> mailList = mailEntry.getMailList();
        //TODO 全部推送给前端  解list放到controller，这里只返回list ，也就是说在controller里直接
        // player.mailPart.mailEntry.getMailList 也可以
        return mailList;
    }

    //读邮件
    public void readMail(long mailId) {
        for (MailPo mailPo : mailEntry.getMailList()) {
            if (mailPo.getMailId() == mailId) {
                mailPo.setHasRead(true);
            }
        }
    }

    //领取邮件物品
    public MailPo receiveItems(long mailId) throws StatusException {
        MailPo mail = null;
        for (MailPo mailPo : mailEntry.getMailList()) {
            if (mailPo.getMailId() == mailId) {
                mail = mailPo;
            }
        }
        if (!Objects.isNull(mail)) {
            mail.setHasReceived(true);
            boolean b = player.bagPart.getBag().addItemRefuse(mail.getItemList());
            if (!b) {
                throw new StatusException(TipType.BagNotEnough);
            }

        } else {
            throw new StatusException(TipType.MailNoExist);
        }
        mail.setHasReceived(true);
        return mail;
    }

    public void delMail(List<Long> mailIds) throws StatusException {
        for (Long mailId : mailIds) {
            for (MailPo mailPo : mailEntry.getMailList()) {
                if (mailId == mailPo.getMailId() && mailPo.isHasReceived()) {
                    throw new StatusException(TipType.MailHasItem);
                }
            }
        }


        Iterator<MailPo> it = mailEntry.getMailList().iterator();
        if (it.hasNext()) {
            MailPo mailPo = it.next();
            for (Long mailId : mailIds) {
                if (mailId == mailPo.getMailId()) {
                    it.remove();
                }
            }

        }
    }


    @Override
    public void onLogin() {

    }
}
