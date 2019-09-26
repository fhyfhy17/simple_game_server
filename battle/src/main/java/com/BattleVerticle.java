package com;

import com.node.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BattleVerticle extends BaseVerticle {

    @Autowired
    private BattleReceiver battleReceiver;
    @Override
    public void init() {
        log.info("启动battle node");

        Node node = new Node();
        node.setBaseReceiver(battleReceiver);
        new Thread(node::start).start();
    }
}
