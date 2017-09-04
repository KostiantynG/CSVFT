package com.gk;

import com.gk.model.Review;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ResponseService extends Thread {
    private static final String PATH_STRING = "./TranslatedReviews.csv";
    private final AtomicInteger tasksInProgress;
    private final CompletionService<Map<Integer, String>> completionService;
    private final FileWriter fileWriter = new FileWriter(PATH_STRING);
    private List<Review> reviewsInProgress = Collections.synchronizedList(new ArrayList<>());

    public ResponseService(AtomicInteger tasksInProgress, CompletionService<Map<Integer, String>> completionService) {
        this.tasksInProgress = tasksInProgress;
        this.completionService = completionService;
    }

    @Override
    public void run() {
        while (true) {
            processResult();
        }
    }

    public synchronized void addProcessingReview(Review review) {
        reviewsInProgress.add(review);
    }

    protected void processResult() {
        Future<Map<Integer, String>> resultFuture = null; //blocks if none available
        try {
            resultFuture = completionService.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Map<Integer, String> result = resultFuture.get();
            tasksInProgress.decrementAndGet();

            List<Review> completed = findAndWriteSentence(result);
//            System.out.println("Completed next reviews: " + completed);
            reviewsInProgress.removeAll(completed);

            // todo do something with the result in separate THREAD. And get Review by sentence unique ids
            //   System.out.println("Got result. Should store it in file: " + result);
        } catch (Exception e) {
            //todo log
            e.printStackTrace();
        }
    }

    private synchronized List<Review> findAndWriteSentence(Map<Integer, String> result) {
        List<Review> completed = new ArrayList<>();
        for (Review review : reviewsInProgress) {
            for (Map.Entry<Integer, String> entry : result.entrySet()) {
                //fixme
                if (inReview(review, entry.getKey())) {
                    review.getIdToSentence().put(entry.getKey(), entry.getValue());
                    review.decrementNotTranslatedSentances();

                    if (review.getNotTranslatedSentences() <= 0) {
                        if (review.getId() == 10) {
                            System.out.println("ololo");
                        }
                        fileWriter.write(review);
                        completed.add(review);
                    }
                }
            }
        }
        return completed;
    }

    private boolean inReview(Review review, Integer k) {
        return k >= review.getFirstSentenceId() && k <= review.getFirstSentenceId() + review.getIdToSentence().size();
    }
}
