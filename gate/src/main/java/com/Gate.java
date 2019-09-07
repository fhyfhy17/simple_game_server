package com;

import com.controller.ControllerFactory;
import com.net.NettyServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@DependsOn(value = {"springUtils", "contextUtil"})
public class Gate implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Gate.class, args);
    }

    public void run(String... args) throws Exception {

    }


    @EventListener
    void afterSpringBoot(ApplicationReadyEvent event) throws Exception {
        ControllerFactory.init();
        //启动 netty
        NettyServer nettyServer = new NettyServer();
        nettyServer.init();

    }
}
