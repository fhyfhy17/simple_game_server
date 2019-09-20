package com;


import com.manager.GameServerManager;
import com.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@SpringBootApplication
@DependsOn(value = {"springUtils", "contextUtil"})
@EnableAsync
public class Game implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Game.class, args);
    }

    public void run(String... args) throws Exception {

    }

    @EventListener
    void afterSpringBoot(ApplicationReadyEvent event) throws Exception {
        SpringUtils.getBean(GameServerManager.class).onServerStart();
    }
    
}

