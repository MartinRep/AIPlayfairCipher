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
    private Grams grams;
    private char[] blockLetters;
    private int transitions, temp;
    private ArrayBlockingQueue<Result> results;

    Worker(String sample, Grams grams, char[] blockLetters, int transitions, int temp, ArrayBlockingQueue<Result> results) {
        this.sample = sample;
        this.grams = grams;
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
        SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(sample, blockLetters, transitions, temp, grams, results);
            Result result = simulatedAnnealing.findKey();
            try {
                results.put(result);
                simulatedAnnealing.setInitKey(result.getKey());
            } catch (InterruptedException e) {
                LogService.logMessage(e.getMessage());
            }
    }
}
