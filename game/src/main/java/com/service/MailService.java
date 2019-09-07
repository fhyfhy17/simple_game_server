package com.service;

import com.annotation.EventListener;
import com.dao.CenterMailRepository;
import com.entry.CenterMailEntry;
import com.entry.MailEntry;
import com.entry.po.ItemInfo;
import com.entry.po.MailPo;
import com.template.TemplateManager;
import com.template.templates.MailTemplate;
import com.util.IdCreator;
import com.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@EventListener
@Slf4j
public class MailService {
    @Autowired
    private CenterMailRepository centerMailRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TemplateManager templateManager;

    public List<CenterMailEntry> findByDate() {

        Query query = new Query();

        Criteria c = Criteria.where("mailStartTime")
                .gt(Instant.now().toEpochMilli() - DateUtils.MILLIS_PER_DAY * 30);
        query.addCriteria(c);
        return mongoTemplate.find(query, CenterMailEntry.class);

    }

    public MailPo createMail(int mailTemplateId) {
        MailTemplate mailTemplate = templateManager.getTemplate(MailTemplate.class, mailTemplateId);

        List<ItemInfo> itemInfoList = Util.createItemInfoList(mailTemplate.getItems());

        MailPo mailPo = new MailPo();
        mailPo.setMailId(IdCreator.nextId(MailEntry.class));
        mailPo.setMailTemplateId(mailTemplateId);
        mailPo.setMailTime(Instant.now().toEpochMilli());
        mailPo.setItemList(itemInfoList);
        return mailPo;
    }
}
