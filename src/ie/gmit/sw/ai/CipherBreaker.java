package ie.gmit.sw.ai;

import sun.rmi.runtime.Log;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Console, menu based application using Simulated Annealing to crack Playfair encryption with out provided decryption key.
 * Success of the algorithm depends on parameters Temperature, Transitions and sample size.
 * Multithreaded, with number of workers running simultaneously. Logged.
 */

public class CipherBreaker {
    private static Boolean stop = false;
    private static Thread[] threads;

    public static void main(String[] args) {
        boolean dev = true;
        int sampleSize = 500;
        int transitions = 50000;
        int temp = 11;
        int numOfWorkers = 1;
        String sample = "";
        ArrayBlockingQueue<String> servLog = new ArrayBlockingQueue<>(numOfWorkers);
        char[] blockLetters = Playfair.getBlockLetters();
        String ngramFile = "./4grams.txt";
        String encryptedFile = "./resources/devHobbit.txt", encryptedURL = "", outFile = "./decrypted.txt";
        String logFile = "logfile.txt";
        HashMap<String, Double> ngrams = new HashMap<>();
        ArrayBlockingQueue<Result> results = new ArrayBlockingQueue<>(numOfWorkers);
        Boolean loggingON = false;
        int choice = 0;
        Scanner sc = new Scanner(System.in);
        Scanner fs = new Scanner(System.in);

        threads = new Thread[numOfWorkers + 1];
        if (!dev) {
            while (choice < 2) {
                //User Interface menu
                System.out.println("\nSimulated Annealing algorithm to break Playfair cipher");
                System.out.println("======================================================\n");
                System.out.println("Please choose the file source:");
                if (loggingON) System.out.println("1. Switch Logging service OFF");
                else System.out.println("1. Switch Logging service ON");
                System.out.println("2. File from local storage");
                System.out.println("3. File from online location (URL)");
                System.out.println("4. Exit\n");
                System.out.print("Please enter your choice: ");
                choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        loggingON ^= true;
                        System.out.println("Logging service flipped.");
                        break;
                    case 2:
                        System.out.println("Enter filename with path ('./hobbit.txt'): ");
                        encryptedFile = fs.nextLine();
                        try {
                            sample = getFromFile(encryptedFile, sampleSize);
                        } catch (IOException e) {
                            System.out.println("Error processing the file! Error: " + e.getMessage());
                            shutdown(1);
                        }
                        break;
                    case 3:
                        System.out.println("Enter filename with URL ('http://www.myFileStorage/hobbit.txt'): ");
                        encryptedURL = fs.nextLine();
                        try {
                            sample = getFromURL(encryptedURL, sampleSize);
                        } catch (IOException e) {
                            System.out.println("Error processing the file! Error: " + e.getMessage());
                            shutdown(1);
                        }
                        break;
                    case 4:
                        System.out.println("Exiting...");
                        shutdown(0);
                        break;
                    default:
                        choice = 0;
                }
            }
            // get output file name
            System.out.println("Enter output filename (decryptedHobbit.txt): ");
            outFile = fs.nextLine();
        } else {
            try {
                sample = getFromFile(encryptedFile, sampleSize);
            } catch (IOException e) {
                System.out.println("Error processing the file! Error: " + e.getMessage());
                shutdown(1);
            }
        }

        //Starts a logging service.
        LogService.init(servLog, logFile, loggingON);
        //Retrieve the necessary data
        try {
            ngrams = getNgrams(ngramFile);
        } catch (IOException e) {
            LogService.logMessage("Error reading ngrams file or the file format is not supported.");
            shutdown(2);
        }

        //Dev only
        if (dev){

            LogService.logMessage(String.valueOf(SimulatedAnnealing.logProbability(sample, ngrams)));
            LogService.logMessage(SimulatedAnnealing.decryptText(sample,"THEQUICKBROWNFXMPDVLAZYGS"));
            LogService.logMessage(String.valueOf(SimulatedAnnealing.logProbability(SimulatedAnnealing.decryptText(sample,"THEQUICKBROWNFXMPDVLAZYGS"),ngrams)));
            Grams grams = new Grams("4grams.txt");
            try {
                grams.loadGrams();
            } catch (Exception e) {
                e.printStackTrace();
            }
            LogService.logMessage(String.valueOf(grams.scoreText(SimulatedAnnealing.decryptText(sample,"THEQUICKBROWNFXMPDVLAZYGS"))));
        }

        System.out.println("======================================================\nRunning Play-fair crack, this could take a few minutes..");
        // Creates a pool of Workers, that can work on Playfair cypher in concurrence.
        for (int i = 0; i < numOfWorkers; i++) {
            threads[i] = new Thread(new Worker(sample, ngrams, blockLetters, transitions, temp, results));
            threads[i].start();
        }
        // Creates stop listener for user interruption event.
        threads[numOfWorkers] = new Thread(() -> {
            System.out.println("Press RETURN key to Stop the process at anytime.");
            Scanner s = new Scanner(System.in);
            s.nextLine();
            System.out.println("Interrupted by user.");
            for (Thread thread : threads) thread.interrupt();
            stop = true;
        });
        threads[numOfWorkers].start();

        // Listen for results coming from workers and display them.
        Result bestResult = new Result("","", Double.NEGATIVE_INFINITY);
        while(!stop){
                Result result = results.poll();
                if(result != null && bestResult.getProbability() < result.getProbability()){
                    bestResult = result;
                    LogService.logMessage("Best result so far - "+ System.lineSeparator() + bestResult.toString());
                    System.out.println("Press RETURN key to Stop the process at anytime.");
                }
        }
        // output decrypted text to a file.
        String line;
        try {
            BufferedReader in;
            if(encryptedFile.equals("")){
                URL url = new URL(encryptedURL);
                in = new BufferedReader(new InputStreamReader(url.openStream()));
            } else {
                FileReader fr = new FileReader(encryptedFile);
                in = new BufferedReader(fr);
            }
            while ((line = in.readLine()) != null){
                PrintWriter writer = new PrintWriter(outFile, "UTF-8");
                writer.println(SimulatedAnnealing.decryptText(line, bestResult.getKey()));
            }
            LogService.logMessage("Decrypted Text saved to " + outFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        shutdown(0);
    }

    /**
     * Reads 4grams from the file
     * @param file filename of the 4grams
     * @return  hashmap of 4gram and log10 probability
     * @throws IOException Throws File not found.
     */

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
                totalNgrams += Double.valueOf(values[1]);
            }
            ngrams.put("TOTAL",totalNgrams);
//            for (String ngram: ngrams.keySet()) {
//                ngrams.replace(ngram, Math.log10(ngrams.get(ngram) / totalNgrams));
//            }
        return ngrams;
    }

    /**
     * Get sample text from the file located in URL
     * @param url The URL of encrypted file
     * @param sampleSize Size of the sample from encrypted file
     * @return String containing sample of the file
     * @throws IOException File not found.
     */

    private static String getFromURL(String url, int sampleSize) throws IOException {
        char[] sample = new char[sampleSize];
        URL fileUrl = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(fileUrl.openStream()));
        if(in.read(sample) != sampleSize){
            in.close();
            throw new IOException();
        }
        in.close();
        return String.valueOf(sample);
    }

    /**
     * Get sample text from the file from local file system.
     * @param filename Full name of the file. Absolute path can be used as well
     * @param sampleSize Number of characters to read from the file
     * @return String containing sample of the file
     * @throws IOException Throws File not found.
     */

    private static String getFromFile(String filename, int sampleSize) throws IOException {
        File encryptFile = new File(filename);
        char[] sample = new char[sampleSize];
        FileReader fr = new FileReader(encryptFile);
        if(fr.read(sample) != sampleSize){
            throw new IOException();
        }
        return String.valueOf(sample);
    }

    /**
     * Shutdown function. Terminates all the running threads
     * @param code Exit code for the application. (0 All OK, the rest error messages.)
     */

    private static void shutdown(int code){
        for (Thread t : threads) {
            if(t != null) t.interrupt();
        }
        LogService.shutdown();
        System.exit(code);
    }

}
