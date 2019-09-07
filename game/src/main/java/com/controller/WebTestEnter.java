package com.controller;

import com.dao.PlayerRepository;
import com.dao.UserRepository;
import com.dao.cache.PlayerDBStore;
import com.entry.PlayerEntry;
import com.enums.TypeEnum;
import com.lock.zk.ZkDistributedLock;
import com.manager.ServerInfoManager;
import com.mongoListener.SaveEventListener;
import com.net.msg.LOGIN_MSG;
import com.net.msg.Options;
import com.node.RemoteNode;
import com.pojo.Packet;
import com.rpc.RpcProxy;
import com.rpc.interfaces.gameToBus.GameToBus;
import com.service.UnionService;
import com.util.ContextUtil;
import com.util.IdCreator;
import com.util.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.WriteBehindConfigurationBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//import com.config.RedissonConfig;

@RestController
@Slf4j
public class WebTestEnter {
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SaveEventListener saveEventListener;

    @Autowired
    private UnionService unionService;

    @Autowired
    private RpcProxy rpcProxy;
//    @Autowired
//    @Qualifier("ha")
//    private HazelcastInstance hazelcastInstance;
//    @Autowired
//    RedissonConfig redissonConfig;
    
 
    
    @RequestMapping("/test/rpc")
    public void rpc() {
        //GameToBus gameToBus=rpcProxy.serviceProxy(GameToBus.class,123,TypeEnum.ServerTypeEnum.LOGIN,123);
        //String s=gameToBus.needResponse("你好哇");
        StopWatch stopWatch = new StopWatch();
    
        stopWatch.start();
        CountDownLatch countDownLatch =new CountDownLatch(10);
        for(int i=0;i<10;i++)
        {
            new Thread(()->{
                for(int j=0;j<100;j++)
                {
                    GameToBus gameToBus=rpcProxy.serviceProxy(GameToBus.class,123,TypeEnum.ServerTypeEnum.LOGIN,123);
                    gameToBus.needResponse("");
                    //log.info("发送 RPC 请求时间  "+ System.currentTimeMillis());
                }
                countDownLatch.countDown();
            }).start();
            
        }
        try
        {
            countDownLatch.await();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        stopWatch.stop();
        log.info("共用时："+stopWatch.getTime());
    }
    
    
    @RequestMapping("/test/a")
    public void test() {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    PlayerEntry playerEntry = new PlayerEntry(IdCreator.nextId(PlayerEntry.class));
                    playerEntry.setName("aaa");
                    playerRepository.save(playerEntry);

//                    UserEntry userEntry = new UserEntry();
//                    userEntry.setUserName("bbb");
//                    userEntry.setPassWord("bbb");
//                    userRepository.save(userEntry);
                }
            }).start();
        }


    }

//    @RequestMapping("/test/addUnion")
//    public void addUnion() {
//        UnionEntry u = new UnionEntry(IdCreator.nextId(UnionEntry.class));
//        IMap<Long, UnionEntry> idIndexMap = hazelcastInstance.getMap(CacheEnum.UnionEntryCache.name());
//        idIndexMap.put(u.getId(), u);
//
//
//        PlayerEntry u2 = new PlayerEntry(IdCreator.nextId(PlayerEntry.class));
//        u2.setExp(3);
//        u2.setName("王三");
//        IMap<Long, PlayerEntry> map2 = hazelcastInstance.getMap(CacheEnum.PlayerEntryCache.name());
//        map2.put(u2.getId(), u2);
//
//
//        IMap<Long, PlayerEntry> map3 = hazelcastInstance.getMap(CacheEnum.PlayerEntryCache.name());
//        System.out.println(map3.get(u2.getId()));
//    }


//    @RequestMapping("/test/addUnion2")
//    public void addUnion2() {
//
//        IMap<Long, PlayerEntry> map3 = hazelcastInstance.getMap(CacheEnum.PlayerEntryCache.name());
////        map3.loadAll(true);
//        System.out.println("成功啦" + map3.get(1L));
//    }
//
//    @RequestMapping("/test/createPlayer")
//    public void createPlayer() {
//        PlayerEntry playerEntry = new PlayerEntry(IdCreator.nextId(PlayerEntry.class));
//        IMap<Long, PlayerEntry> idIndexMap = hazelcastInstance.getMap(CacheEnum.PlayerEntryCache.name());
//
//        idIndexMap.put(playerEntry.getId(), playerEntry);
////        1077939648774410240
//    }

    AtomicInteger a = new AtomicInteger(0);
    StopWatch s = new StopWatch();
    ZkDistributedLock lock = new ZkDistributedLock(ContextUtil.zkIpPort, 1000, "textLock");

    @RequestMapping("/test/playerAddUnion")
    public void playerAddUnion() {
        unionService.examinePlayer(1078490924780228608L, 1071010079177838592L);
//        1077939648774410240
//        1077941486252855296
    }

    @RequestMapping("/test/testZk")
    public void testZk() {
        s.reset();
        s.start();
        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Thread(() -> {
                lock.lock("a");
                try {
                    System.out.println(Thread.currentThread().getId() + " 获得了锁");
                    int i1 = this.a.incrementAndGet();
                    if (i1 != 0 && i1 % 1000 == 0) {
                        s.stop();
                        System.out.println("1000个 锁已经全部完事了，共用了：" + s.getTime());
                    }

                } finally {
                    lock.unlock();
                }

//                RedissonClient lock = redissonConfig.getClient();
//
//
////                RedissonRedLock redLock = new RedissonRedLock(lock.getLock("lock1"), lock.getLock("lock2"), lock.getLock("lock3"));
//
//                RLock redLock = lock.getLock("a");
//                try {
//                    boolean res = false;
//                    try {
//                        res = redLock.tryLock(30, 3, TimeUnit.SECONDS);
//                        if (res) {
//                            System.out.println(Thread.currentThread().getId() + " 获得了锁");
//                            int i1 = this.a.incrementAndGet();
//                            if (i1!=0&&i1 % 1000==0) {
//                                s.stop();
//                                System.out.println("1000个 锁已经全部完事了，共用了："+s.getTime());
//                            }
//                        }
//
//                    } catch (InterruptedException e) {
//                        log.error("", e);
//                    }
//
//                } finally {
//                    redLock.unlock();
//                }

            }
            ).start();
        }
    }


    @RequestMapping("/test/vertxMessage")
    public void testVertxMessage() {
        Random r = new Random();
        Packet message = new Packet();
        LOGIN_MSG.TEST_TIME.Builder builder = LOGIN_MSG.TEST_TIME.newBuilder();
        builder.setMsg("abcdefghigklmnopqrstuvwxyz");

        message.setData(builder.build().toByteArray());
        message.setId(LOGIN_MSG.TEST_TIME.getDescriptor().getOptions().getExtension(Options.messageId));
        message.setFrom(ContextUtil.id);


        for (int i = 0; i < 1000000; i++) {
            message.setUid(1);
            ServerInfoManager.sendMessage("login-1", message);
        }

    }

    @RequestMapping("/test/zeromqMessage")
    public void testZeromqMessage() {
        Packet message = new Packet();
        LOGIN_MSG.TEST_TIME.Builder builder = LOGIN_MSG.TEST_TIME.newBuilder();
        builder.setMsg("abcdefghigklmnopqrstuvwxyz");

        message.setData(builder.build().toByteArray());
        message.setId(LOGIN_MSG.TEST_TIME.getDescriptor().getOptions().getExtension(Options.messageId));
        message.setFrom(ContextUtil.id);
        message.setUid(1);
        for (int i = 0; i < 1000000; i++) {
            RemoteNode remoteNode = ServerInfoManager.getRemoteNode("login-1");
            remoteNode.sendReqMsg(SerializeUtil.mts(message));
        }

    }

    @RequestMapping("/test/ehcache")
    public void aaa() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);

        Cache<Long, PlayerEntry> writeBehindCache = cacheManager.createCache("writeBehindCache",
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, PlayerEntry.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, MemoryUnit.GB))
                        .withLoaderWriter(new PlayerDBStore())
                        .add(WriteBehindConfigurationBuilder
                                .newBatchedWriteBehindConfiguration(1, TimeUnit.SECONDS, 1000)
                                .queueSize(3000)
                                .concurrencyLevel(1)
                                .enableCoalescing()

                        )
                        .build());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
//        for (int i = 0; i < 1000000; i++) {
//            PlayerEntry playerEntry = new PlayerEntry(IdCreator.nextId(PlayerEntry.class));
//
//            writeBehindCache.put(playerEntry.getId(), playerEntry);
//        }
        System.out.println("用时：" + stopWatch.getTime());

    }
}
