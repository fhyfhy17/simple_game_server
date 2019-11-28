package com.hot;



import java.lang.instrument.Instrumentation;


public class Agent {

    public static Instrumentation instrumentation;
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        Agent.instrumentation = instrumentation;
    }

}