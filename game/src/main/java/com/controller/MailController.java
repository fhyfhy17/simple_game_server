package com.controller;

import com.annotation.Controllor;
import com.entry.po.MailPo;
import com.exception.StatusException;
import com.net.msg.MAIL_MSG;
import com.pojo.Player;
import com.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
public class MailController extends BaseController {

    @Controllor
    //删除邮件
    public MAIL_MSG.GTC_DEL_MAIL delMail(Player player, MAIL_MSG.CTG_DEL_MAIL req) throws StatusException {
        MAIL_MSG.GTC_DEL_MAIL.Builder builder = MAIL_MSG.GTC_DEL_MAIL.newBuilder();

        player.mailPart.delMail(req.getMailIdList());
        builder.addAllMailId(req.getMailIdList());
        return builder.build();
    }

    @Controllor
    // 邮件列表
    public MAIL_MSG.GTC_MAIL_LIST mailList(Player player, MAIL_MSG.CTG_MAIL_LIST req) {
        MAIL_MSG.GTC_MAIL_LIST.Builder builder = MAIL_MSG.GTC_MAIL_LIST.newBuilder();
        List<MailPo> mailPos = player.mailPart.mailList();
        for (MailPo mailPo : mailPos) {
            MAIL_MSG.MAIL_INFO.Builder mailInfo = MAIL_MSG.MAIL_INFO.newBuilder();
            mailInfo.setMailId(mailPo.getMailId());
            mailInfo.setMailTemplateId(mailPo.getMailTemplateId());
            mailInfo.setHasRead(mailPo.isHasRead());
            mailInfo.setHasReceived(mailPo.isHasReceived());
            mailInfo.setMailTime(mailPo.getMailTime());
            mailInfo.addAllItems(Util.transferItemInfoList(mailPo.getItemList()));
        }
        return builder.build();
    }

    @Controllor
    //领取邮件
    public MAIL_MSG.GTC_RECEIVE_ITEMS receiveItems(Player player, MAIL_MSG.CTG_RECEIVE_ITEMS req) throws StatusException {
        MAIL_MSG.GTC_RECEIVE_ITEMS.Builder builder = MAIL_MSG.GTC_RECEIVE_ITEMS.newBuilder();
        long mailId = req.getMailId();
        MailPo mailPo = player.mailPart.receiveItems(mailId);
        builder.addAllItems(Util.transferItemInfoList(mailPo.getItemList()));
        return builder.build();
    }
}
