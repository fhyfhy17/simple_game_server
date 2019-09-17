package com;

import com.node.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BusVerticle extends BaseVerticle {

    @Autowired
    private BusReceiver busReceiver;
    @Override
    public void init() {
        log.info("启动bus node");

        Node node = new Node();
        node.setBaseReceiver(busReceiver);
        new Thread(node::start).start();
    }
}
