//Adopted from: https://github.com/ayushn21/Playfair-Cipher

package ie.gmit.sw.ai;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    private static Boolean stop = false;
    private static Thread stopAnytime;

    public static void main(String[] args) {
        int sampleSize = 300;
        int transitions = 500;
        int temp = 10;
        int numOfWorkers = 5;
        char [] sample = new char[sampleSize];
        ArrayBlockingQueue<String> servLog = new ArrayBlockingQueue<>(numOfWorkers);
        char[] blockLetters = Playfair.getBlockLetters();
        String ngramFile = "resources/4grams.txt";
        String encryptedFile = "resources/devHobbit.txt";
        String encryptedURL = "https://drive.google.com/file/d/193tHElB0VFH5rfT2woBr5jy_1NyV_DvE/view?usp=sharing";
        String logFile = "logfile.txt";
        HashMap<String, Double> ngrams = new HashMap<>();
        ArrayBlockingQueue<Result> results = new ArrayBlockingQueue<>(numOfWorkers);
        ArrayList<Result> finalResults = new ArrayList<>();
        Boolean loggingON = false;

        LogService.init(servLog, logFile, loggingON);
        try {
            ngrams = getNgrams(ngramFile);
            sample = getFromFile(encryptedFile, sampleSize);
            //sample = getFromURL(encryptedURL, sampleSize);
        } catch (IOException e) {
            LogService.logMessage("Error reading the file or the file format is not supported.");
            shutdown(1);
        }
        System.out.println("Running Play-fair crack, this could take a few minutes..");
        String devKey = "THEQUICKBROWNFXMPDVLAZYGS";
        String devPlainText = SimulatedAnnealing.decryptText(String.valueOf(sample), devKey);
        System.out.println("Plain Text: " + devPlainText);
        double devProb = SimulatedAnnealing.logProbability(devPlainText, ngrams);
        System.out.println("Base Probability: " + String.valueOf(devProb));

        Thread[] threads = new Thread[numOfWorkers];
        for (int i = 0; i < numOfWorkers; i++) {
            threads[i] = new Thread(new Worker(sample, ngrams, blockLetters, (transitions*(100*(i+1))), temp, results));
            threads[i].start();
        }
        stopAnytime = new Thread(() -> {
            System.out.println("Press RETURN key to Stop the process at anytime.");
            Scanner s = new Scanner(System.in);
            s.nextLine();
            System.out.println("Interrupted by user.");
            for (Thread thread : threads) thread.interrupt();
            stop = true;
        });
        stopAnytime.start();
        int numResults = 0;
        Result bestResult = new Result("","", Double.NEGATIVE_INFINITY);
        while(!stop){
                Result result = results.poll();
                if(result != null && result.getProbability() > bestResult.getProbability()){
                    numResults++;
                    finalResults.add(result);
                    bestResult = result;
                    LogService.logMessage("Best result so far - "+ System.lineSeparator() + bestResult.toString());
                }
        }

        for (Result result: finalResults) {
            if(bestResult.getProbability() < result.getProbability()){
                bestResult = result;
            }
        }
        if (bestResult.getProbability() != Double.NEGATIVE_INFINITY){
            System.out.println();
            LogService.logMessage(bestResult.toString());
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        shutdown(0);
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
            in.close();
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

    private static void shutdown(int code){
        LogService.shutdown();
        if(stopAnytime != null) stopAnytime.interrupt();
        System.exit(code);
    }

}
