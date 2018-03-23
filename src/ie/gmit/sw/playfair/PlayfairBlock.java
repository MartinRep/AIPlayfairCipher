package ie.gmit.sw.playfair;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayfairBlock 
{
	
	private char[][] cipherTable;
	private ArrayList<Character> blockLetters = new ArrayList<>(Arrays.asList('a','b','c','d','e','f','g','h','i','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'));
	private ArrayIndex index;
	
	PlayfairBlock(String key)
	{
		this.cipherTable = prepareBlock(key.toLowerCase().toCharArray());
		this.index = new ArrayIndex(this.cipherTable);
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
		for(char letter : this.blockLetters)
		{
			table[i][j] = letter;
			j++;
			if(j>4)
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
		return charArrayToString(output);
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

	private static String charArrayToString(char[] input)
	{
		StringBuilder sb_stringBuilder = new StringBuilder();
		for(int i = 0; i < input.length; i++)
		{
			sb_stringBuilder.append(input[i]);
		}
		return sb_stringBuilder.toString();
	}

	private static char[] reverseCharArray(char[] input)
	{
		char[] output = new char[input.length];
		int j = 0;
		for(int i=input.length-1; i>=0; i--)
		{
			output[i] = input[j];
			j++;
		}
		return output;
	}

}
