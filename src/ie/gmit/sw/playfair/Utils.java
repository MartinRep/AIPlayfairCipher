package ie.gmit.sw.playfair;

import java.util.ArrayList;

public class Utils 
{
	public static char[] characterArrayListToCharArray(ArrayList<Character> input)
	{
		int i = 0;
		char[] output = new char[input.size()];
		for(char letter : input)
		{
			output[i] = letter;
			i++;
		}	
		return output;
	}
	
	public static String charArrayToString(char[] input)
	{
		StringBuilder sb_stringBuilder = new StringBuilder();
		for(int i=0; i<input.length; i++)
		{
			sb_stringBuilder.append(input[i]);
		}
		return sb_stringBuilder.toString();
	}
	
	public static char[] reverseCharArray(char[] input)
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
	
	public static void print2DArray(char[][] input)
	{
		for(int i=0; i<input.length; i++)
		{
			for(int j=0; j<input[i].length; j++)
			{
				System.out.print(input[i][j]);
			}
			System.out.println();
		}
	}
	
	public static void print2DArray(int[][] input)
	{
		for(int i=0; i<input.length; i++)
		{
			for(int j=0; j<input[i].length; j++)
			{
				System.out.print(input[i][j]);
			}
			System.out.println();
		}
	}
	
	public static <type> void print2DArray(type[][] input)
	{
		for(int i=0; i<input.length; i++)
		{
			for(int j=0; j<input[i].length; j++)
			{
				System.out.print(input[i][j]);
			}
			System.out.println();
		}
	}
	
	public static void print(Object a)
	{
		System.out.println(a);
	}
	
}
