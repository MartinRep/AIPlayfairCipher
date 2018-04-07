package ie.gmit.sw.ai;

/**
 * Model type class. Stores result of an Simulated Annealing on Playfair cypher. Populated by Worker and read by main class.
 * @author Martin Repicky g00328337@gmit.ie
 */

public class Result {
    private String plainText;
    private String key;
    private Double probability;

    Result(String plainText, String key, Double probability) {
        this.plainText = plainText;
        this.key = key;
        this.probability = probability;
    }

    String getKey() {
        return key;
    }


    Double getProbability() {
        return probability;
    }


    @Override
    public String toString() {
        return "plainText = '" + plainText + System.lineSeparator();
    }
}
