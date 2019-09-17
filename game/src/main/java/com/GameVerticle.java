package com;


import com.node.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GameVerticle extends BaseVerticle {

    @Autowired
    private GameReceiver gameReceiver;

    public void init() {
        log.info("启动node");


        Node node = new Node();
        node.setBaseReceiver(gameReceiver);
        new Thread(node::start).start();
    }

}
