package ie.gmit.sw.ai;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to handle 4 grams to measure the text english like property. 4 characters and their frequency
 * in English language.
 *  @author Martin Repicky g003238337@gmit.ie
 */

class Grams {

	private Map<String, Long> grams;
	
	Grams() {
		this.grams = new HashMap<>();
	}

    /**
     * Process 4grams from a file into HashMap
     * @param fileName Name of the file containing 4grams and their frequency
     * @throws IOException Usually File Not Found
     */
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

    /**
     * Measure the whole text 4grams frequency and return probability how much Text is English-like.
     * @param cipherText Text to be measured.
     * @return Single double digit. Usually around -2000
     */
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
