package ie.gmit.sw.ai;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SimulatedAnnealing {

    private String sample;
    private HashMap<String, Double> ngrams;
    private char[] blockLetters;
    private int trans, temperature;

    SimulatedAnnealing(String sample, HashMap<String, Double> ngrams, char[] blockLetters, int trans, int temperature) {
        this.sample = sample;
        this.ngrams = ngrams;
        this.blockLetters = blockLetters;
        this.trans = trans;
        this.temperature = temperature;
    }

    public Result decrypt() {
        char[] parent = shuffle(blockLetters.clone());
        String decrypTextParent = "";
        double parentProb = 0;
        for (int temp = temperature; temp >= 0; temp--) {
            for (int transitions = trans; transitions >= 0; transitions--) {
                char[] child = shuffle(parent);
                decrypTextParent = decryptText(sample, String.valueOf(parent));
                String decrypTextChild = decryptText(sample, String.valueOf(child));
                double childProb = logProbability(decrypTextChild, ngrams);
                parentProb = logProbability(decrypTextParent, ngrams);
                double delta = childProb - parentProb;
                if (delta > 0) {
                    parent = child;
                    //System.out.println("Parent: " + String.valueOf(parent) + " Child: " + String.valueOf(child));
                } else {
                    //System.out.println("Child: " + childProb + " exp(-delta/temp): " + Math.exp(-delta / temp));
         // Need to add randomness to search algorithm !!
                    if (logProbability(decrypTextChild, ngrams) == Math.exp(-delta / temp)) {
                        //System.out.println("*Parent: " + String.valueOf(parent) + " Child: " + String.valueOf(child));
                        parent = child;
                    }
                }

                //System.out.println(decryptText(sampleDev, String.valueOf(parent)));
            }
            //System.out.print(".");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            //System.out.println(decryptText(sample, String.valueOf(parent)));
        }
        return new Result(decrypTextParent, String.valueOf(parent), parentProb);
    }


    private String decryptText(String inputText, String key)
    {
        PlayfairBlock playfairBlock = new PlayfairBlock(key);
        char[][] digraphs = playfairBlock.prepareInputText(inputText);
        StringBuilder sb_decryptedTextBuilder = new StringBuilder();

        for(char[] each_digraph : digraphs)
        {
            sb_decryptedTextBuilder.append(playfairBlock.decryptDigraph(each_digraph));
        }
        return sb_decryptedTextBuilder.toString();
    }

    private char[] shuffle(char[] key) {
        int index;
        char[] newKey = key.clone();
        Random random = ThreadLocalRandom.current();
        for (int i = newKey.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            if (index != i) {
                newKey[index] ^= newKey[i];
                newKey[i] ^= newKey[index];
                newKey[index] ^= newKey[i];
            }
        }
        return newKey;
    }

    private double logProbability(String sample , HashMap<String, Double> ngrams){
        double probability = 0;
        sample = sample.toUpperCase();
        for (int index = 0; index <= (sample.length() - 4); index++) {
            String gram = sample.substring(index, index + 4);
            if(ngrams.containsKey(gram)){
                probability += ngrams.get(gram);
            }
        }
        return probability;
    }

}
