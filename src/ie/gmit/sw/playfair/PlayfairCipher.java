package ie.gmit.sw.playfair;

public class PlayfairCipher {

    private char[][] prepareInputText(String inputText)
    {
        inputText = inputText.replaceAll("\\s", "");
        inputText = inputText.toLowerCase();
        char[][] digraphs = new char[inputText.length() / 2 ][2];
        if(inputText.length() % 2 != 0)
        {
            inputText = inputText + "z";
        }
        int index = 0;
        for(int i = 0; i < inputText.length() / 2; i++)
        {
            for(int j = 0; j < 2; j++)
            {
                digraphs[i][j] = inputText.charAt(index);
                index++;
            }
        }
        return digraphs;
    }

    public String decryptText(String inputText, String key)
    {
        char[][] digraphs = prepareInputText(inputText);
        PlayfairBlock playfairBlock = new PlayfairBlock(key);
        StringBuilder sb_decryptedTextBuilder = new StringBuilder();
        for(char[] each_digraph : digraphs)
        {
            sb_decryptedTextBuilder.append(playfairBlock.decryptDigraph(each_digraph));
        }
        return sb_decryptedTextBuilder.toString();
    }

}
