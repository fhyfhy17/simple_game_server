package com;

import com.util.SpringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@DependsOn(value = {"springUtils", "contextUtil"})
@EnableAsync
public class Battle implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Battle.class, args);
    }

    public void run(String... args) throws Exception {

    }


    @EventListener
    void afterSpringBoot(ApplicationReadyEvent event) throws Exception {
        BusServerManager busServerManager = SpringUtils.getBean(BusServerManager.class);
        busServerManager.onServerStart();
    }
}

