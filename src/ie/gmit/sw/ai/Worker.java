package ie.gmit.sw.ai;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Worker class. Attempt to find the correct decryption key for the sample text encrypted using Playfair cypher.
 * Result is put into ArrayBlockingQueue, which is then processed by main.
 * * @author Martin Repicky g00328337@gmit.ie
 */


public class Worker implements Runnable{
    private String sample;
    private HashMap<String, Double> ngrams;
    private char[] blockLetters;
    private int transitions, temp;
    private ArrayBlockingQueue<Result> results;

    public Worker(String sample, HashMap<String, Double> ngrams, char[] blockLetters, int transitions, int temp, ArrayBlockingQueue<Result> results) {
        this.sample = sample;
        this.ngrams = ngrams;
        this.blockLetters = blockLetters;
        this.transitions = transitions;
        this.temp = temp;
        this.results = results;
    }

    /**
     * Creates new instance of SimulatedAnnealing and attempt to find correct key. Continue to do so, until user stops it.
     */

    @Override
    public void run() {
        SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(sample, ngrams, blockLetters, transitions, temp);
        //noinspection InfiniteLoopStatement
        while (true){
            Result result = simulatedAnnealing.findKey();
            try {
                results.put(result);
                simulatedAnnealing.setInitKey(result.getKey());
            } catch (InterruptedException e) {
                LogService.logMessage(e.getMessage());
            }
        }

    }
}
