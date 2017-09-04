package com.gk;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class TranslateTask implements Callable<Map<Integer, String>> {
    private AtomicInteger availableSymbols = new AtomicInteger(500); // Translate API allows 1000 symbols
    private Map<Integer, String> idSentance = new HashMap<>(); // sentence unique identifier to sentence text

    public synchronized void addSentence(int id, String sentence) {
        if (sentence == null) {
            idSentance.put(id, "");
            return;
        }

        availableSymbols.addAndGet(-sentence.length());
        idSentance.put(id, sentence);
    }


    public synchronized boolean canAddSentence(String sentence) {
        return isEmpty(sentence) ||
                availableSymbols.get() >= sentence.length();
    }

    @Override
    public Map<Integer, String> call() throws Exception {

        // GOOGLE TRANSLATE API CALL SHOULD GO HERE...
        RestTemplateMock rt = new RestTemplateMock();

        // result is returned to a Future in next line. Overall TranslateTask takes about 200 millis to complete.
        return rt.getForObject(idSentance);
    }
}


