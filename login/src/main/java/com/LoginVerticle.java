package com;


import com.node.Node;
import com.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoginVerticle extends BaseVerticle {

    @Autowired
    private LoginReceiver loginReceiver;

    public void init() {
        log.info("启动node");


        Node node = new Node();
        node.setBaseReceiver(SpringUtils.getBeansOfType(BaseReceiver.class).values().iterator().next());
        new Thread(node::start).start();
    }

    //    @Override
//    public BaseReceiver getReceiver() {
//        return loginReceiver;
//    }


}
