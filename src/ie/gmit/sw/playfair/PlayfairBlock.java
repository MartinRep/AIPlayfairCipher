package ie.gmit.sw.playfair;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayfairBlock 
{
	
	private char[][] cipherTable = new char[5][5];
	private ArrayList<Character> blockLetters = new ArrayList<Character>(Arrays.asList('a','b','c','d','e','f','g','h','i','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'));
	private char[] key;
	private ArrayIndex index;
	
	public PlayfairBlock(String key)
	{
		this.key = prepareKey(key.toLowerCase().toCharArray());
		this.cipherTable = prepareBlock(this.key);
		this.index = new ArrayIndex(this.cipherTable);
	}
	
	private char[][] prepareBlock(char[] key) 
	{
		for(char letter : Utils.reverseCharArray(key))
		{
			blockLetters.remove(blockLetters.indexOf(letter));
			blockLetters.add(0, letter);
		}
		
		char[][] table = new char[5][5];
		int i = 0;
		int j = 0;
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

	private char[] prepareKey(char[] key)
	{
		ArrayList<Character> deduplicatedKey = new ArrayList<Character>();
		for(char keyLetter : key)
		{
			if(!deduplicatedKey.contains(keyLetter))
			{
				deduplicatedKey.add(keyLetter);
			}
		}
		while(deduplicatedKey.contains('j'))
		{
			int index = deduplicatedKey.indexOf('j');
			deduplicatedKey.remove(index);
			deduplicatedKey.add(index, 'i');
		}
		return Utils.characterArrayListToCharArray(deduplicatedKey);
	}

	public char[][] getCipherTable() 
	{
		return this.cipherTable;
	}
	
	public char[] getKey() 
	{
		return this.key;
	}

	public String encryptDigraph(char[] digraph)
	{
		Integer[] coordsOfCharacter1 = index.get(new Character(digraph[0]));
		Integer[] coordsOfCharacter2 = index.get(new Character(digraph[1]));
		char[] output = new char[2];
		
		int X_1 = coordsOfCharacter1[0].intValue();
		int Y_1 = coordsOfCharacter1[1].intValue();
		int X_2 = coordsOfCharacter2[0].intValue();
		int Y_2 = coordsOfCharacter2[1].intValue();
		
		if(X_1 != X_2 && Y_1 != Y_2)
		{
			output[0] = this.cipherTable[X_1][Y_2];
			output[1] = this.cipherTable[X_2][Y_1];
		}
		else if(X_1 == X_2 && Y_1 != Y_2)
		{
			output[0] = this.cipherTable[X_1][getNewEncryptCoordWithWrap(Y_1)];
			output[1] = this.cipherTable[X_2][getNewEncryptCoordWithWrap(Y_2)];
		}
		else if(X_1 != X_2 && Y_1 == Y_2)
		{
			output[0] = this.cipherTable[getNewEncryptCoordWithWrap(X_1)][Y_1];
			output[1] = this.cipherTable[getNewEncryptCoordWithWrap(X_2)][Y_2];
		}
		return Utils.charArrayToString(output);
	}
	
	public String decryptDigraph(char[] digraph)
	{
		Integer[] coordsOfCharacter1 = index.get(new Character(digraph[0]));
		Integer[] coordsOfCharacter2 = index.get(new Character(digraph[1]));
		char[] output = new char[2];
		
		int X_1 = coordsOfCharacter1[0].intValue();
		int Y_1 = coordsOfCharacter1[1].intValue();
		int X_2 = coordsOfCharacter2[0].intValue();
		int Y_2 = coordsOfCharacter2[1].intValue();
		
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
		else if(X_1 != X_2 && Y_1 == Y_2)
		{
			output[0] = this.cipherTable[getNewDecryptCoordWithWrap(X_1)][Y_1];
			output[1] = this.cipherTable[getNewDecryptCoordWithWrap(X_2)][Y_2];
		}
		return Utils.charArrayToString(output);
	}

	private int getNewEncryptCoordWithWrap(int coord) 
	{
		
		if(coord < 4)
		{
			return coord + 1;
		}
		else
		{
			return 0;
		}
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

}
