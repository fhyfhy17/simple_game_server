package com.util.consistentHashing;

import lombok.Data;

@Data
public class IpNode implements Node<String> {

    private String name;
    private String ip;
    private int weight;


    public IpNode(String name, String ip, int weight) {
        this.name = name;
        this.ip = ip;
        this.weight = weight;
    }

    @Override
    public String getVirtualNodeName(int index) {
        return name + "#" + index;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public String getResource() {
        return ip;
    }


}
