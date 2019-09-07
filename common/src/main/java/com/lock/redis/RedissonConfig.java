package com.lock.redis;

//先用zookeeper锁吧，就是稍慢点而已
//@Component
//@Slf4j
//public class RedissonConfig {
//
//    RedissonClient redisson;
//    @Value("${redisson.single.address}")
//    private String singleAddress;
//
//    @PostConstruct
//    public void init() {
//
//        try {
//            Config config;
//            config = new Config();
//            config.useSingleServer().setAddress(singleAddress);
//            redisson = Redisson.create(config);
//        } catch (Exception e) {
//            log.error("redisson启动失败", e);
//        }
//    }
//
//    public RedissonClient getClient() {
//        return redisson;
//    }
//}