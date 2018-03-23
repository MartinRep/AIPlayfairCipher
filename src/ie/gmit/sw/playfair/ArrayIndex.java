package ie.gmit.sw.playfair;
import java.util.HashMap;

@SuppressWarnings("serial")

public class ArrayIndex extends HashMap<Character, Integer[]>
{
	
	public ArrayIndex(char[][] input)
	{
		for(int i=0; i<input.length; i++)
		{
			for(int j=0; j<input[i].length; j++)
			{
				this.put(new Character(input[i][j]), new Integer[] {i,j});
			}
		}
	}

}
