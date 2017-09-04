package com.gk;

import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestService {
    private final CompletionService<Map<Integer, String>> completionService;
    private final AtomicInteger tasksInProgress;

    public RequestService(AtomicInteger tasksInProgress, ExecutorCompletionService<Map<Integer, String>> completionService ) {
        this.completionService = completionService;
        this.tasksInProgress = tasksInProgress;
    }

    public  synchronized void submit(TranslateTask translateTask) {

        while (tasksInProgress.get() >= 100) { // wait until at least one is ready
            // TODO use CountDownLatch or smthg
        }
        tasksInProgress.incrementAndGet();
        completionService.submit(translateTask);
    }
}
