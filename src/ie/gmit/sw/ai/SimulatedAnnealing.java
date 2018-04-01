package ie.gmit.sw.ai;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulated Annealing Class - Generates new and modify decryption key with the Fisher–Yates Shuffle and asses the key probability.
 * Uses Playfair class to decrypt sample text with key generated.
 * Simulated annealing algorithm success depends on 3 parameters: Sample size, temperature and number of transitions.
 * @author Martin Repicky g00328337@gmit.ie
 *
 */

public class SimulatedAnnealing {

    private String sample;
    private HashMap<String, Double> ngrams;
    private char[] initKey;
    private int trans, temperature;

    SimulatedAnnealing(String sample, HashMap<String, Double> ngrams, char[] blockLetters, int trans, int temperature) {
        this.sample = sample;
        this.ngrams = ngrams;
        this.initKey = newShuffle(blockLetters);
        this.trans = trans;
        this.temperature = temperature;
    }

    /**
     * Fasade method to run Simulated Annealing algorithm on Playfair cypher.
     * @return Result object containing decrypted text, key used for decryption and probability of the key being the right one.
     */

    public Result findKey() {
        char[] parent = initKey.clone();
        String decrypTextParent = "", decrypTextChild;
        double parentProb = Double.NEGATIVE_INFINITY, childProb, delta;
        for (int temp = temperature; temp >= 0; temp = temp - 1) {
            for (int transitions = trans; transitions >= 0; transitions--) {
                // new key child node created via The Fisher–Yates Shuffle
                char[] child = shuffleKey(parent);
                decrypTextParent = decryptText(sample, String.valueOf(parent));
                decrypTextChild = decryptText(sample, String.valueOf(child));
                childProb = logProbability(decrypTextChild, ngrams);
                parentProb = logProbability(decrypTextParent, ngrams);
                delta = childProb - parentProb;
                if (delta > 0) {
                    parent = child.clone();
                } else if (Math.exp(delta/temp) >= ThreadLocalRandom.current().nextDouble(1.0)) {
                   // System.out.println(Math.pow(Math.E,(delta/temp)));
                        parent = child.clone();
                    }
            }
        }
        return new Result(decrypTextParent, String.valueOf(parent), parentProb);
    }

    /**
     * Decrypt text using key
     * @param inputText Encrypted text
     * @param key - 25 character key used to decrypt inputText
     * @return Plain-decrypted text
     */
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

    /**
     * Creates new random key
     * @param key An old key, or set of Block letters.
     * @return A new random key from elements of key provided
     */
    private char[] newShuffle(char[] key) {
        int index;
        char[] newKey = key.clone();
        for (int i = newKey.length - 1; i > 0; i--) {
            index = getRandomInt(i + 1);
            if (index != i) {
                newKey[index] ^= newKey[i];
                newKey[i] ^= newKey[index];
                newKey[index] ^= newKey[i];
            }
        }
        return newKey;
    }

    /**
     * The Fisher–Yates Shuffle algorithm. Modify existing key matrix with frequency:
     * Swap single letters (90%)
     • Swap random rows (2%)
     • Swap columns (2%)
     • Flip all rows (2%)
     • Flip all columns (2%)
     • Reverse the whole key (2%)
     * @param key - Set of 25 Characters to modify
     * @return A key modified according to the rules.
     */

    private char[] shuffleKey(char[] key){
        int rnd = getRandomInt(100);
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

    /**
     * Convert 5x5 Matrix to and 1d array
     * @param keyMatrix 2 dimensional 5x5 matrix
     * @return 1 Dimensional Array of characters
     */

    private char[] getKey(char[][] keyMatrix){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(String.valueOf(keyMatrix[i]));
        }
        return sb.toString().toCharArray();
    }

    /**
     * Converts 1d array into 5x5 Matrix
     * @param key 1 dimensional array of Characters
     * @return  5x5 Matrix of Characters
     */

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

    /**
     * Swap 2 random Columns in the array
     * @param keyMatrix a 2 dimensional array
     */

    private void swapRndColumns(char[][] keyMatrix){
        int col, colSwap;
        do {
            colSwap = getRandomInt(5);
            col = getRandomInt(5);
        } while (col == colSwap);
        for (int i = 0; i < 5; i++) {
          swapChar(keyMatrix[i][col], keyMatrix[i][colSwap]);
        }
    }

    /**
     * A small utility function to generate random integer within the limit
     * @param limit - Set a limit for random number generation. Maximum value of random number.
     * @return A random integer within 0 - limit.
     */

    private int getRandomInt(int limit){
        Random random = ThreadLocalRandom.current();
        return random.nextInt(limit);
    }

    /**
     * Swap 2 random rows in a 2 dimensional array.
     * @param keyMatrix 2 dimensional array to modify.
     */

    private void swapRndRows(char[][] keyMatrix){
        char[] tempRow;
        int row, rowSwap;
        do {
            rowSwap = getRandomInt(5);
            row = getRandomInt(5);
        } while (row == rowSwap);
        tempRow = keyMatrix[row];
        keyMatrix[row] = keyMatrix[rowSwap];
        keyMatrix[rowSwap] = tempRow;
    }

    /**
     * Reverse order of characters in every row in the 2d array
     * @param keyMatrix 2d Array of Characters
     */

    private void flipRows(char[][] keyMatrix) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 2; j++) {
                swapChar(keyMatrix[i][j], keyMatrix[i][4 - j]);
            }
        }
    }

    /**
     * Reverse order of characters in every column in the 2d array
     * @param keyMatrix 2d Array of Characters
     */

    private void flipColumns(char[][] keyMatrix) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                swapChar(keyMatrix[i][j], keyMatrix[4 - i][j]);
            }
        }
    }

    /**
     * Reverse every row and every column in the Matrix
     * @param keyMatrix 2d Matrix to be modified
     */
    private void reverseKeyMatrix(char[][] keyMatrix){
        flipRows(keyMatrix);
        flipColumns(keyMatrix);
    }

    /**
     * Swap random letters in the 2d array of characters.
     * @param keyMatrix 2d array of characters
     */

    private void swapRndLetter(char[][] keyMatrix){
        int col,row, swapCol, swapRow;
        do {
            col = getRandomInt(5);
            row = getRandomInt(5);
            swapCol = getRandomInt(5);
            swapRow = getRandomInt(5);
        } while (row == swapRow && col == swapCol);
        swapChar(keyMatrix[row][col], keyMatrix[swapRow][swapCol]);
    }

    /**
     * Small utility function to swap 2 characters
     * @param one first character to swap with second
     * @param two second character to swap with first
     */

    @SuppressWarnings("UnusedAssignment")
    private void swapChar(char one, char two){
        char temp = one;
        one = two;
        two = temp;
    }

    /**
     * Calculate the the probability of a sample to be recognized as english language using 4grams as reference.
     * Used as heuristic in search algorithm.
     * @param sample Sample of decrypted text to be assessed as english text
     * @param ngrams Collection of 4 letter grams used the most in english language and their frequency
     * @return Log10 probability of the sample. (Usually in around -2000 range, depends on sample size)
     */

    private static double logProbability(String sample , HashMap<String, Double> ngrams){
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
