package ie.gmit.sw.ai;

import java.util.*;

public class Playfair
{
	private char[][] cipherTable;
    private ArrayList<Character> blockLetters = new ArrayList<>(Arrays.asList('A','B','C','D','E','F','G','H','I','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'));
	private HashMap<Character, Integer[]> index = new HashMap<>();

	Playfair(String key)
	{
		this.cipherTable = prepareBlock(key.toUpperCase().toCharArray());
		for(int i = 0; i < this.cipherTable.length; i++)
        {
            for(int j = 0; j < this.cipherTable[i].length; j++)
            {
                index.put(this.cipherTable[i][j], new Integer[] {i,j});
            }
        }
	}

    char[][] prepareInputText(String inputText)
    {
        inputText = inputText.replaceAll("\\s", "");
        inputText = inputText.toUpperCase();
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

	private char[][] prepareBlock(char[] key)
	{
		for(char letter : reverseCharArray(key))
		{
			blockLetters.remove(blockLetters.indexOf(letter));
			blockLetters.add(0, letter);
		}
		
		char[][] table = new char[5][5];
		int i = 0, j = 0;
		for(char letter : blockLetters)
		{
			table[i][j] = letter;
			j++;
			if(j > 4)
			{
				i++;
				j = 0;
			}
		}
		return table;
	}

	public String decryptDigraph(char[] digraph)
	{
		Integer[] coordsOfCharacter1 = index.get(digraph[0]);
		Integer[] coordsOfCharacter2 = index.get(digraph[1]);
		char[] output = new char[2];
		
		int X_1 = coordsOfCharacter1[0];
		int Y_1 = coordsOfCharacter1[1];
		int X_2 = coordsOfCharacter2[0];
		int Y_2 = coordsOfCharacter2[1];
		
		if(X_1 != X_2 && Y_1 != Y_2)
		{
			output[0] = this.cipherTable[X_1][Y_2];
			output[1] = this.cipherTable[X_2][Y_1];
		}
		else if(X_1 == X_2 && Y_1 != Y_2)
		{
			output[0] = this.cipherTable[X_1][getNewDecryptCoordWithWrap(Y_1)];
			output[1] = this.cipherTable[X_2][getNewDecryptCoordWithWrap(Y_2)];
		}
		else if(X_1 != X_2)
		{
			output[0] = this.cipherTable[getNewDecryptCoordWithWrap(X_1)][Y_1];
			output[1] = this.cipherTable[getNewDecryptCoordWithWrap(X_2)][Y_2];
		}
		return String.valueOf(output);
	}

	private int getNewDecryptCoordWithWrap(int coord) 
	{
		
		if(coord > 0)
		{
			return coord - 1;
		}
		else
		{
			return 4;
		}
	}

	private char[] reverseCharArray(char[] input)
	{
		char[] output = new char[input.length];
		int j = 0;
		for(int i = input.length - 1; i >= 0; i--)
		{
			output[i] = input[j];
			j++;
		}
		return output;
	}

    public static char[] getBlockLetters() {
        ArrayList<Character> block = new ArrayList<>(Arrays.asList('A','B','C','D','E','F','G','H','I','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'));
        return Collections.singletonList(block).toString().replaceAll("[,\\s\\[\\]]", "").toCharArray();
    }

}
