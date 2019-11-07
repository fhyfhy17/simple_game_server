package com.controller;

import com.Constant;
import com.config.ZookeeperConfig;
import com.dao.BagRepository;
import com.dao.CenterMailRepository;
import com.dao.MailRepository;
import com.dao.NoCellBagRepository;
import com.dao.PlayerRepository;
import com.dao.UnionRepository;
import com.dao.UserRepository;
import com.entry.PlayerEntry;
import com.enums.TypeEnum;
import com.lock.DisLock;
import com.lock.LockUtil;
import com.lock.zk.ZkDistributedLock;
import com.manager.GameServerManager;
import com.manager.ServerInfoManager;
import com.mongoListener.MongoEventListener;
import com.net.msg.LOGIN_MSG;
import com.net.msg.Options;
import com.node.RemoteNode;
import com.pojo.Packet;
import com.rpc.RpcProxy;
import com.rpc.RpcRequest;
import com.rpc.interfaces.player.GameToLogin;
import com.service.PlayerService;
import com.thread.schedule.ScheduleTask;
import com.util.ContextUtil;
import com.util.IdCreator;
import com.util.ProtoUtil;
import com.util.ProtostuffUtil;
import com.util.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
    BagRepository bagRepository;
    
    @Autowired
    NoCellBagRepository noCellBagRepository;
    
    @Autowired
    MailRepository mailRepository;
    @Autowired
    CenterMailRepository centerMailRepository;
    @Autowired
    UnionRepository unionRepository;
    
    
    @Autowired
    MongoEventListener saveEventListener;

    @Autowired
    private RpcProxy rpcProxy;

    @Autowired
    private ZookeeperConfig zookeeperConfig;
   
//    @Autowired
//    @Qualifier("ha")
//    private HazelcastInstance hazelcastInstance;
//    @Autowired
//    RedissonConfig redissonConfig;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private PlayerService playerService;
    @RequestMapping("/test/poolThreadLocalTest")
    public void poolThreadLocalTest() {
        playerService.testPool();
    }

    @RequestMapping("/test/testResponse")
    public void testResponse() {
        GameToLogin gameToLogin = rpcProxy.proxy(GameToLogin.class, 123, TypeEnum.ServerTypeEnum.LOGIN, 123);
        CompletableFuture<String> f=gameToLogin.testResponse(2222L);
        f.whenComplete((str,t)->{
            if(t!=null){
                System.out.println(t.getMessage());
            }else{
                System.out.println(str);
            }
        });
       
    }
    
    @RequestMapping("/test/self")
    public void self() {
        //TODO 给自己发个RPC请求，这个以后可以摘出来，做成一个功能。 另看能不能集成在RPC访问对端报错，发送方加后续处理那
        RpcRequest rpcRequest = new RpcRequest();

        rpcRequest.setId(Constant.RPC_REQUEST + UUID.randomUUID().toString());
        rpcRequest.setClassName("com.rpc.interfaces.player.GameToGame");
        rpcRequest.setMethodName("self");
        rpcRequest.setParameters(new Object[]{"a"});

        ServerInfoManager.sendMessage("game-2",
                ProtoUtil.buildRpcRequestMessage(
                        ProtostuffUtil.serializeObject(rpcRequest, RpcRequest.class),
                        123,
                        ContextUtil.id,
                        rpcRequest.getId()));
    }

    @RequestMapping("/test/rpc")
    public void rpc() {
//        StopWatch stopWatch = new StopWatch();
//
//        stopWatch.start();
//        CountDownLatch countDownLatch =new CountDownLatch(10);
//        GameToBus gameToBus = rpcProxy.proxy(GameToBus.class, 123, TypeEnum.ServerTypeEnum.BUS, 123);
//        for (int i = 0; i < 10; i++) {
//            new Thread(() -> {
//                for (int j = 0; j < 100; j++) {
//
//                    gameToBus.needResponse("");
//                    //log.info("发送 RPC 请求时间  "+ System.currentTimeMillis());
//                }
//                countDownLatch.countDown();
//            }).start();
//
//        }
//        try
//        {
//            countDownLatch.await();
//        }
//        catch(InterruptedException e)
//        {
//            e.printStackTrace();
//        }
//        stopWatch.stop();
//        log.info("共用时："+stopWatch.getTime());
    }
    
    @RequestMapping("/test/testinsert")
    public void testinsert() {
        playerEntry = playerRepository.save(new PlayerEntry(0));
    }
    @RequestMapping("/test/testupdate")
    public void testupdate() {
        playerRepository.save(playerEntry);
    }
    @RequestMapping("/test/saveall")
    public void saveall() {
        for(int i=0;i<100;i++)
        {
            //playerRepository.saveAll(Lists.newArrayList(new PlayerEntry( RandomUtils.nextInt(200,300)),new PlayerEntry(RandomUtils.nextInt(100,200)),new PlayerEntry(RandomUtils.nextInt(100,200))));
            PlayerEntry playerEntry=new PlayerEntry(RandomUtils.nextInt(100,200));
            
            playerEntry.setName(RandomUtils.nextDouble()+"");
            playerRepository.save(playerEntry);
            //
            //playerRepository.insert(new PlayerEntry(RandomUtils.nextInt(100,200)));
            //
            //playerRepository.insert(Lists.newArrayList(new PlayerEntry(RandomUtils.nextInt(100,200)),new PlayerEntry(RandomUtils.nextInt(100,200))));
            //
            //userRepository.saveAll(Lists.newArrayList(new UserEntry( RandomUtils.nextInt(100,200)),new UserEntry(RandomUtils.nextInt(100,200)),new UserEntry(RandomUtils.nextInt(100,200))));
            //
            //userRepository.save(new UserEntry( RandomUtils.nextInt(100,200)));
            //
        }
       
        
    
    }
    @RequestMapping("/test/insertt")
    public void insertt() {
      playerRepository.insert(new PlayerEntry(201));
    }


//    @RequestMapping("/test/noneed")
//    public void noneed() {
//        GameToBus gameToBus = rpcProxy.proxy(GameToBus.class, 123, TypeEnum.ServerTypeEnum.BUS, 123);
//        gameToBus.noNeedResponse0();
//    }
    
    private PlayerEntry playerEntry;
    @Autowired
    private GameServerManager gameServerManager;
    
    @RequestMapping("/test/sche1")
    public void sche1() {
        gameServerManager.getThreadSchedule().scheduleOnce(new ScheduleTask(){
            @Override
            public void execute(){
                log.info("这是 sche1");
            }
        },1);
    }
    
    @RequestMapping("/test/sche2")
    public void sche2() {
        gameServerManager.getThreadSchedule().scheduleCron(new ScheduleTask(){
            @Override
            public void execute(){
                log.info("这是 sche2");
            }
        },"*/2 * * * * ?");
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



    @RequestMapping("/test/testZk")
    public void testZk() {
        int count=1000;
        DisLock lock=LockUtil.lock("ccc");
        s.reset();
        s.start();
        for (int i = 0; i < count; i++) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Thread(() -> {
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getId() + " 获得了锁");
                    int i1 = this.a.incrementAndGet();
                    if (i1 != 0 && i1 % count == 0) {
                        s.stop();
                        System.out.println(count+"个 锁已经全部完事了，共用了：" + s.getTime());
                    }

                } finally {
                    lock.unLock();
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
}
