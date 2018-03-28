package ie.gmit.sw.ai;

public class Result {
    private String plainText;
    private String key;
    private Double probability;

    Result(String plainText, String key, Double probability) {
        this.plainText = plainText;
        this.key = key;
        this.probability = probability;
    }

    public String getPlainText() {
        return plainText;
    }

    public String getKey() {
        return key;
    }


    public Double getProbability() {
        return probability;
    }

    @Override
    public String toString() {
        return "Best result so far - " + System.lineSeparator() +
                "plainText = '" + plainText + '\'' + System.lineSeparator() +
                "key = '" + key + '\'' + System.lineSeparator() +
                "probability = " + probability;
    }
}
