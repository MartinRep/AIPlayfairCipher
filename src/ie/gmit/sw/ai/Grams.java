package ie.gmit.sw.ai;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

class Grams {

	private Map<String, Long> grams;
	
	Grams() {
		this.grams = new HashMap<>();
	}

	//Process 4grams from a file to HashMap
	void loadGrams(String fileName)  throws IOException {
		long total = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));
		String line;
		
		//Loading grams into map
		while((line = br.readLine()) != null) {
			String[] data = line.split(" ");
			grams.put(data[0], Long.valueOf(data[1]));
			//Summing up the total frequency
			total += Long.valueOf(data[1]);
		}
		//Save the total number of 4grams
		grams.put("TOTAL", total);
		br.close();
	}

	double scoreText(String cipherText) {
		double score = 0;
		long frequency;
		for(int i = 0; i < cipherText.length() - 4; i++){
			//Check if 4gram exist
			if(grams.containsKey(cipherText.substring(i, i + 4))){
				frequency = grams.get(cipherText.substring(i, i + 4));
			}else{
				//If 4gram doesn't exist set frequency to 1
				frequency = 1;
			}
			//Calculate the score and sum it all up
			score += Math.log10((double) frequency/grams.get("TOTAL"));
		}
		
		//Return the score
		return score;
	}
}
