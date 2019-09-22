package com.enums;

import com.exception.NoEnumTypeException;

import java.util.Arrays;
import java.util.List;

public interface TypeEnum {

    default int getType() {
        Enum e = (Enum) this;
        return e.ordinal();
    }

    default String getName() {
        Enum e = (Enum) this;
        return e.name();
    }

    static <T extends TypeEnum> T getEnumByType(Class<T> enumTyps, int type) {
        List<T> enums = Arrays.asList(enumTyps.getEnumConstants());
        return enums.stream().filter(x -> x.getType() == type).findAny().orElseThrow(() -> new NoEnumTypeException(""));
    }

    static <T extends TypeEnum> T getEnumByName(Class<T> enumTyps, String name) {
        List<T> enums = Arrays.asList(enumTyps.getEnumConstants());
        return enums.stream().filter(x -> name.equals(x.getName())).findAny().orElseThrow(() -> new NoEnumTypeException(""));
    }

    /**
     * 任务类型
     */
    enum TaskTypeEnum implements TypeEnum {
        /**
         * 未接取，有些任务要求，未接取，不生效，但是要求可以提前查看
         */
        NOT_RECEIVE,
        /**
         * 接取正在进行中
         */
        ON,
        /**
         * 完成
         */
        FINISH,
        /**
         * 领取奖励
         */
        REWARD,
        ;


    }

    /**
     * 分发线程类型
     */
    enum GroupEnum implements TypeEnum {
        /**
         * gate发client
         */
        GATE_TO_CLIENT_GROUP,
        /**
         * client发gate
         */
        GATE_GROUP,//
        GAME_GROUP,//
        LOGIN_GROUP,//
        BUS_GROUP,//
        BATTLE_GROUP,//
        ;

    }

    /**
     * 服务器类型
     */
    enum ServerTypeEnum implements TypeEnum {

        GATE,//
        GAME,//
        LOGIN,//
        BATTLE,//
        MATCH,//
        BUS,//
        ;
    }
    
    /**
     * 服务器启动状态
     */
    enum ServerStatus implements TypeEnum {
        STARTING,//启动中
        OPEN,//启动完毕
        CLOSING,//关闭中
        ;
    }
    
    /**
     * Player状态
     */
    enum PlayerStatus implements TypeEnum {
        ONLINE,//在线
        SWITCH,//切换服中，下线中，被挤中，不接受消息
        ;
    }
    

}