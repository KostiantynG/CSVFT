package com.gk;

import java.util.Map;

public class RestTemplateMock {
    public Map<Integer, String> getForObject(Map<Integer, String> idSentance) throws InterruptedException {
        //  System.out.println("Emulate real request time. Sleep for 200 ms. Ids: " + idSentance.keySet());
        Thread.sleep(200);
        for (Map.Entry<Integer, String> entry : idSentance.entrySet()) {
            entry.setValue(entry.getValue().toUpperCase());
        }

        return idSentance;
    }
}
