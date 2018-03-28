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

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    @Override
    public String toString() {
        return System.lineSeparator() + "Best result so far: " + System.lineSeparator() +
                "plainText= '" + plainText + '\'' + System.lineSeparator() +
                "key= '" + key + '\'' + System.lineSeparator() +
                "probability= " + probability +
                '}';
    }
}
