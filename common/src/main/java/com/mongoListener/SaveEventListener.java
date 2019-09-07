package com.mongoListener;

import com.annotation.EventListener;
import com.entry.BaseEntry;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;


@Component
@EventListener

/**
 *
 * 本类为，存入MongoDB之前的监听，可以指定生成的键。 用来实现SeqID，因为缓存用了Write-Behind模式，所以新建的 Entity不是及时入存的了
 * 得不到 id， 只能是预先生成键 ，已用  Hutool里的 SnowFlake 替代，相比独立的id生成服务， 本地的SnowFlake只是一个同步线程而已，
 * 在生成规模不大的话，用这个已经够了，之后最好修改一个 服务ID，和区ID （可以认为是，服务器ID和TYPE ID）
 *
 *
 *
 * */
public class SaveEventListener extends AbstractMongoEventListener<BaseEntry> {

//    @Autowired
//    private MongoTemplate mongo;
//
//    private ConcurrentHashMap<String, Pair<Long, Long>> map = new ConcurrentHashMap<>();
//
//    private final static int EVERY_GET_ID_NUM = 5000;
//
//    @PostConstruct
//    public void init() {
//        List<String> seqClassNames = ReflectionUtil.getSeqClassNames();
//        for (String seqClassName : seqClassNames) {
//            map.put(seqClassName, new Pair<>(-1L, -1L));
//        }
//    }
//
//    @Override
//    public void onBeforeConvert(BeforeConvertEvent<BaseEntry> event) {
//        BaseEntry source = event.getSource();
//
//        if (source != null) {
//            if (0 == source.getId()) {
//                return;
//            }
//
//            ReflectionUtils.doWithFields(source.getClass(), field -> {
//                ReflectionUtils.makeAccessible(field);
//                if (field.isAnnotationPresent(IncKey.class)) {
//                    String name = source.getClass().getAnnotation(SeqClassName.class).name();
//
//                    field.set(source, getNextId(name));
//                }
//            });
//        }
//    }
//
//    private Long getNextIdFromMongo(String collName) {
//
//        Query query = new Query(Criteria.where("collName").is(collName));
//        Update update = new Update();
//        update.inc("seqId", EVERY_GET_ID_NUM);
//        FindAndModifyOptions options = new FindAndModifyOptions();
//        options.upsert(true);
//        options.returnNew(true);
//        SeqEntry seq = mongo.findAndModify(query, update, options, SeqEntry.class);
//        return seq.getSeqId();
//    }
//
//    private Long getNextId(String name) {
//
//        Pair<Long, Long> idpair = map.get(name);
//        synchronized (idpair) {
//            if (idpair.getKey() == -1 || idpair.getKey().equals(idpair.getValue())) {
//                Long nextMaxId = getNextIdFromMongo(name);
//                idpair.setKey(nextMaxId - EVERY_GET_ID_NUM + 1);
//                idpair.setValue(nextMaxId);
//            } else {
//                idpair.setKey(idpair.getKey() + 1);
//            }
//        }
//
////        Pair<Long, Long> longLongPair = map.computeIfPresent(name, (s, idPair) -> {
////
////            if (idPair.getKey() == -1) {
////
////                Long nextMaxId = getNextIdFromMongo(name);
////                return new Pair<>(nextMaxId - EVERY_GET_ID_NUM + 1, nextMaxId);
////            }
////            if (idPair.getKey().equals(idPair.getValue())) {
////                Long nextMaxId = getNextIdFromMongo(name);
////                return new Pair<>(nextMaxId - EVERY_GET_ID_NUM + 1, nextMaxId);
////            } else {
////                idPair.setKey(idPair.getKey() + 1);
////                return idPair;
////            }
////        });
//        return idpair.getKey();
//
//    }

}

