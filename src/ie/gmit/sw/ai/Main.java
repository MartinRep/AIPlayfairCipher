//Adopted from: https://github.com/ayushn21/Playfair-Cipher

package ie.gmit.sw.ai;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {

    public static void main(String[] args) {
        int sampleSize = 500;
        int transitions = 500;
        int temp = 10;
        int numOfWorkers = 2;
        char [] sample = new char[sampleSize];
        ArrayBlockingQueue<String> servLog = new ArrayBlockingQueue<>(numOfWorkers);
        char[] blockLetters = PlayfairBlock.getBlockLetters();
        String ngramFile = "resources/4grams.txt";
        String encryptedFile = "resources/devHobbit.txt";
        String logFile = "logfile.txt";
        HashMap<String, Double> ngrams = new HashMap<>();
        ArrayBlockingQueue<Result> results = new ArrayBlockingQueue<>(numOfWorkers);
        LogService.init(servLog, logFile);
        try {
            ngrams = getNgrams(ngramFile);
            sample = getFromFile(encryptedFile, sampleSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Running playfaircrack, this could take a few minutes..");
        Worker[] workers = new Worker[numOfWorkers];
        for (int i = 0; i < numOfWorkers; i++) {
            workers[i] = new Worker(sample, ngrams, blockLetters, transitions, temp, results);
            new Thread(workers[i]).start();
        }
        int numResults = 0;
        while(numResults < numOfWorkers){
            Result result = results.peek();
            if(result != null){
//                LogService.logMessage(result.toString());
                numResults++;
            }
        }
        Result bestResult = new Result("","", Double.MIN_VALUE);
        for (Result result: results) {
            if(bestResult.getProbability() > result.getProbability()){
                bestResult = result;
            }
        }
        System.out.println();
        LogService.logMessage(bestResult.toString());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        LogService.shutdown();
        System.exit(0);
    }

    private static HashMap<String, Double> getNgrams(String file) throws IOException {
        HashMap<String, Double> ngrams = new HashMap<>();
        File ngramsFile = new File(file);
            String line;
            double totalNgrams = 0;
            FileReader fr = new FileReader(ngramsFile);
            BufferedReader bf = new BufferedReader(fr);
            while ((line = bf.readLine()) != null)
            {
                String [] values = line.split(" ");
                ngrams.put(values[0], Double.valueOf(values[1]));
                totalNgrams += Long.valueOf(values[1]);
            }
            for (String ngram: ngrams.keySet()) {
                ngrams.replace(ngram, Math.log10(ngrams.get(ngram) / totalNgrams));
            }
        return ngrams;
    }

    private static char[] getFromURL(String url, int sampleSize) throws IOException {
        char[] sample = new char[sampleSize];
        URL fileUrl = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(fileUrl.openStream()));
        if(in.read(sample) != sampleSize){
            throw new IOException();
        }
        in.close();
        return sample;
    }

    private static char[] getFromFile(String filename, int sampleSize) throws IOException {
        File encryptFile = new File(filename);
        char[] sample = new char[sampleSize];
        FileReader fr = new FileReader(encryptFile);
        if(fr.read(sample) != sampleSize){
            throw new IOException();
        }
        return sample;
    }

}
