package com.service;

import com.annotation.EventListener;
import com.annotation.EventMethod;
import com.dao.CenterMailRepository;
import com.entry.CenterMailEntry;
import com.entry.MailEntry;
import com.entry.po.ItemInfo;
import com.entry.po.MailPo;
import com.enums.EventType;
import com.pojo.Player;
import com.template.templates.MailTemplate;
import com.template.templates.MailTemplateCache;
import com.template.templates.type.CenterMailType;
import com.util.GameUtil;
import com.util.IdCreator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@EventListener
@Slf4j
public class MailService extends GameService {
    @Autowired
    private CenterMailRepository centerMailRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private OnlineService onlineService;

    public List<CenterMailEntry> findByDate() {

        Query query = new Query();

        Criteria c = Criteria.where("mailStartTime")
                .gt(Instant.now().toEpochMilli() - DateUtils.MILLIS_PER_DAY * 30);
        query.addCriteria(c);
        return mongoTemplate.find(query, CenterMailEntry.class);

    }

    @EventMethod(EventType.PlayerTest)
    public void test(int a, String b, CenterMailEntry c) {
        System.out.println(Thread.currentThread().getName() + "---- " + a);
        System.out.println(b);
        System.out.println(c);
    }

    public MailPo createMail(int mailTemplateId) {
        MailTemplate mailTemplate = MailTemplateCache.get(mailTemplateId);

        List<ItemInfo> itemInfoList = GameUtil.createItemInfoList(mailTemplate.getItems());

        MailPo mailPo = new MailPo();
        mailPo.setMailId(IdCreator.nextId(MailEntry.class));
        mailPo.setMailTemplateId(mailTemplateId);
        mailPo.setMailTime(Instant.now().toEpochMilli());
        mailPo.setItemList(itemInfoList);
        return mailPo;
    }

    /**
     * 世界邮件的接收处理
     */
    public void onCenterMail(CenterMailEntry centerMailEntry) {
        Map<Long, Player> playerMap = onlineService.getPlayerMap();
        switch (centerMailEntry.getType()) {
            case CenterMailType.Personal:
                Long playerIdPersonal = centerMailEntry.getReceiverId().iterator().next();
                if (playerMap.containsKey(playerIdPersonal)) {
                    Player player = playerMap.get(playerIdPersonal);
                    systemDis(player.getUid(), () -> {
                        player.getMailModule().onCenterMail(centerMailEntry);
                    });
                }
                break;
            case CenterMailType.Multiple:
                for (Long playerId : centerMailEntry.getReceiverId()) {
                    if (onlineService.getPlayerMap().containsKey(playerId)) {
                        Player player = onlineService.getPlayerMap().get(playerId);
                        systemDis(player.getUid(), () -> {
                            player.getMailModule().onCenterMail(centerMailEntry);
                        });
                    }
                }
                break;
            case CenterMailType.Total:
                for (Player player : playerMap.values()) {
                    systemDis(player.getUid(), () -> {
                        player.getMailModule().onCenterMail(centerMailEntry);
                    });
                }

                break;
            default:
                log.info("类型错误 世界邮件 centermail = {}", centerMailEntry);
        }
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onClose() {

    }
}
