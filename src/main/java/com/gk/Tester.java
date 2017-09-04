package com.gk;

import com.gk.model.Review;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

public class Tester {
    private static final String FILE_PATH = "./Reviews.csv";
    //    static final ExecutorService service = Executors.newFixedThreadPool(100);
    private static final Executor executor = Executors.newFixedThreadPool(100);
    private static AtomicInteger tasksInProgress = new AtomicInteger(1);
    private static AtomicInteger sentenceSequence = new AtomicInteger(1);
    private final ExecutorCompletionService<Map<Integer, String>> COMPLETION_SERVICE = new ExecutorCompletionService<>(executor);
    private Function<String, Review> mapToReview = (line) -> {
        String[] p = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");// a CSV has comma separated lines
        Review item = new Review();
        item.setId(Long.valueOf(p[0]));//<-- this is the first column in the csv file
        item.setProductId(p[1]);
        item.setUserId(p[2]);
        item.setProfileName(p[3]);
        item.setHelpfulnessNumerator(Integer.valueOf(p[4]));
        item.setHelpfulnessDenominator(Integer.valueOf(p[5]));
        item.setScore(Integer.valueOf(p[6]));
        item.setTime(Long.valueOf(p[7]));
//        item.setSummary(p[8]);
        //item.setText(p[9]);
        Map<Integer, String> sentences = new HashMap<>();

        synchronized (this) {
            item.setFirstSentenceId(sentenceSequence.get());

            for (String sentence : p[9].split("\\.")) {
                sentences.put(sentenceSequence.getAndIncrement(), sentence);
            }
            item.setIdToSentence(sentences);
            item.setNotTranslatedSentences(item.getIdToSentence().size());
        }
        return item;
    };
    private TranslateTask translateTask = new TranslateTask();
    private RequestService requestService = new RequestService(tasksInProgress, COMPLETION_SERVICE);
    private ResponseService responseService = new ResponseService(tasksInProgress, COMPLETION_SERVICE);

    public static Stream<String> readFile(String FILE_PATH) {
        Stream<String> lines = null;
        File file = new File(FILE_PATH);
        try {
            InputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            lines = br.lines();
        } catch (IOException e) {
            // todo exception handling  
        }
        return lines;

    }

    public static void main(String... a) throws InterruptedException {
        Tester tester = new Tester();
        tester.start();
    }

    private void start() {
        responseService.start();

        Stream<String> lines = readFile(FILE_PATH);
        lines
                .parallel()
                .filter(line -> !line.startsWith("Id"))
                .map(mapToReview)
                .forEach(review -> {
                    addReviewToCallTask(review);
                });
        System.out.println("Done!!!!");
    }

    private void addReviewToCallTask(Review review) {
        System.out.println("Submitting.." + review.getId());
        responseService.addProcessingReview(review);
        sentenceToTranslateTask(review);
    }

    private synchronized void sentenceToTranslateTask(Review review) {
        for (Entry<Integer, String> entry : review.getIdToSentence().entrySet()) {
            if (!translateTask.canAddSentence(entry.getValue())) {
                // translateTask is full. Sending and start to fill new one.
                requestService.submit(translateTask);
                translateTask = new TranslateTask();
                translateTask.addSentence(entry.getKey(), entry.getValue());
            }
            translateTask.addSentence(entry.getKey(), entry.getValue());
            // todo handle? case when one sentence is longer that 1000 symbols
        }
    }
    // Do not publish more than 100 tasks to save RAM.


}