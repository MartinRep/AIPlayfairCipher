//Adopted from: https://github.com/ayushn21/Playfair-Cipher

package ie.gmit.sw.ai;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {

    public static void main(String[] args) {
        int sampleSize = 500;
        int transitions = 50000;
        int temp = 10;
        int numOfWorkers = 10;
        char [] sample = new char[sampleSize];
        ArrayBlockingQueue<String> servLog = new ArrayBlockingQueue<>(numOfWorkers);
        char[] blockLetters = PlayfairBlock.getBlockLetters();
        String ngramFile = "resources/4grams.txt";
        String encryptedFile = "resources/devHobbit.txt";
        String logFile = "logfile.txt";
        HashMap<String, Double> ngrams = new HashMap<>();

        LogService.init(servLog, logFile);
        try {
            ngrams = getNgrams(ngramFile);
            sample = getFromFile(encryptedFile, sampleSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(String.valueOf(sample), ngrams, blockLetters, transitions, temp);
        LogService.logMessage("Best result key: " + simulatedAnnealing.decrypt());
        LogService.shutdown();
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
