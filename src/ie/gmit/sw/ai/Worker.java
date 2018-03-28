package ie.gmit.sw.ai;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;


public class Worker implements Runnable{
    private char[] sample;
    private HashMap<String, Double> ngrams;
    private char[] blockLetters;
    private int transitions, temp;
    private ArrayBlockingQueue<Result> results;

    public Worker(char[] sample, HashMap<String, Double> ngrams, char[] blockLetters, int transitions, int temp, ArrayBlockingQueue<Result> results) {
        this.sample = sample;
        this.ngrams = ngrams;
        this.blockLetters = blockLetters;
        this.transitions = transitions;
        this.temp = temp;
        this.results = results;
    }

    @Override
    public void run() {
        SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(String.valueOf(sample), ngrams, blockLetters, transitions, temp);
        Result result = simulatedAnnealing.decrypt();
        try {
            results.put(result);
        } catch (InterruptedException e) {
            LogService.logMessage(e.getMessage());
        }
    }
}
