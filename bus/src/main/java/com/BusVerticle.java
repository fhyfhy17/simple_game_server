package com;

import com.node.Node;
import com.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BusVerticle extends BaseVerticle {

    @Override
    public void init() {
        log.info("启动bus node");

        Node node = new Node();
        node.setBaseReceiver(SpringUtils.getBeansOfType(BaseReceiver.class).values().iterator().next());
        new Thread(node::start).start();
    }
}
