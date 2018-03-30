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

    public void setBlockLetters(char[] blockLetters) {
        this.blockLetters = blockLetters;
    }

    public Result decrypt() {
        char[] parent = newShuffle(blockLetters.clone());
        String decrypTextParent;
        double parentProb;
        Result bestResult = new Result();
        bestResult.setProbability(logProbability(decryptText(sample,String.valueOf(parent)), ngrams));
        for (int temp = temperature; temp >= 0; temp--) {
            for (int transitions = trans; transitions >= 0; transitions--) {
                // Shuffle needs to be changed
                char[] child = shuffleKey(parent);
                decrypTextParent = decryptText(sample, String.valueOf(parent));
                String decrypTextChild = decryptText(sample, String.valueOf(child));
                double childProb = logProbability(decrypTextChild, ngrams);
                parentProb = logProbability(decrypTextParent, ngrams);
                double delta = childProb - parentProb;
                if (delta > 0) {
                    parent = child;
                    //System.out.println("Parent: " + String.valueOf(parent) + " Child: " + String.valueOf(child));
                } else if (temp > 0){
                    //System.out.println("Child: " + childProb + " exp(-delta/temp): " + Math.exp(-delta / temp));
         // Need to add randomness to search algorithm !!
//                    LogService.logMessage("Random: " + Math.random()*1000);
//                    LogService.logMessage(String.valueOf(Math.exp(-delta / temp)));
                    if (0.5 > Math.pow(Math.E,(-delta/temp))) {
                        //System.out.println("*Parent: " + String.valueOf(parent) + " Child: " + String.valueOf(child));
                        parent = child;
                    }
                }
                if(childProb > bestResult.getProbability()){
                    bestResult.setProbability(childProb);
                    bestResult.setKey(String.valueOf(child));
                    bestResult.setPlainText(decrypTextChild);
                }

                //System.out.println(decryptText(sampleDev, String.valueOf(parent)));
            }
            //System.out.println(decryptText(sample, String.valueOf(parent)));
        }
        return bestResult;
    }


    static String decryptText(String inputText, String key)
    {
        Playfair playfair = new Playfair(key);
        char[][] digraphs = playfair.prepareInputText(inputText);
        StringBuilder sb_decryptedTextBuilder = new StringBuilder();
        for(char[] each_digraph : digraphs)
        {
            sb_decryptedTextBuilder.append(playfair.decryptDigraph(each_digraph));
        }
        return sb_decryptedTextBuilder.toString();
    }

    private char[] newShuffle(char[] key) {
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

    private char[] shuffleKey(char[] key){
        Random random = ThreadLocalRandom.current();
        int rnd = random.nextInt(100);
        char[][] keyMatrix = getMatrix(key);
        switch (rnd) {
            case 91:
            case 92:
                swapRndRows(keyMatrix);
                break;
            case 93:
            case 94:
                swapRndColumns(keyMatrix);
                break;
            case 95:
            case 96:
                flipRows(keyMatrix);
                break;
            case 97:
            case 98:
                flipColumns(keyMatrix);
                break;
            case 99:
                reverseKeyMatrix(keyMatrix);
                break;
            default:
                swapRndLetter(keyMatrix);
                break;
        }
        return getKey(keyMatrix);
    }

    private char[] getKey(char[][] keyMatrix){
        String key = "";
        for (int i = 0; i < 5; i++) {
            key += String.valueOf(keyMatrix[i]);
        }
        return key.toCharArray();
    }

    private char[][] getMatrix(char[] key){
        char[][] keyMatrix = new char[5][5];
        int index = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                keyMatrix[i][j] = key[index];
                index++;
            }
        }
        return keyMatrix;
    }

    private void swapRndColumns(char[][] keyMatrix){
        int col, colSwap;
        Random random = ThreadLocalRandom.current();
        do {
            colSwap = random.nextInt(5);
            col = random.nextInt(5);
        } while (col == colSwap);
        for (int i = 0; i < 5; i++) {
          swapChar(keyMatrix[i][col], keyMatrix[i][colSwap]);
        }
    }

    private void swapRndRows(char[][] keyMatrix){
        char[] tempRow;
        int row, rowSwap;
        Random random = ThreadLocalRandom.current();
        do {
            rowSwap = random.nextInt(5);
            row = random.nextInt(5);
        } while (row == rowSwap);
        tempRow = keyMatrix[row];
        keyMatrix[row] = keyMatrix[rowSwap];
        keyMatrix[rowSwap] = tempRow;
    }

    private void flipRows(char[][] keyMatrix) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 2; j++) {
                swapChar(keyMatrix[i][j], keyMatrix[i][4 - j]);
            }
        }
    }

    private void flipColumns(char[][] keyMatrix) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                swapChar(keyMatrix[i][j], keyMatrix[4 - i][j]);
            }
        }
    }
    
    private void reverseKeyMatrix(char[][] keyMatrix){
        flipRows(keyMatrix);
        flipColumns(keyMatrix);
    }

    private void swapRndLetter(char[][] keyMatrix){
        int col,row, swapCol, swapRow;
        Random random = ThreadLocalRandom.current();
        do {
            col = random.nextInt(5);
            row = random.nextInt(5);
            swapCol = random.nextInt(5);
            swapRow = random.nextInt(5);
        } while (row == swapRow && col == swapCol);
        swapChar(keyMatrix[row][col], keyMatrix[swapRow][swapCol]);
    }

    private void swapChar(char one, char two){
        char temp = one;
        one = two;
        two = temp;
    }


    static double logProbability(String sample , HashMap<String, Double> ngrams){
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
