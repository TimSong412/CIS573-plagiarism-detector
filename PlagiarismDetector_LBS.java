
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class implements a simple plagiarism detection algorithm.
 */
public class PlagiarismDetector_LBS {
	
	/*
	 * Returns a Map (sorted by the value of the Integer, in non-ascending order) indicating
	 * the number of matches of phrases of size windowSize or greater between each document in the corpus
	 * 
	 * Note that you may NOT remove this method or change its signature or specification!
	 */
	public static Map<String, Integer> detectPlagiarism(String dirName, int windowSize, int threshold) {
		File dirFile = new File(dirName);
		String[] files = dirFile.list();
		if (files == null) throw new IllegalArgumentException();
		
		Map<String, Integer> numberOfMatches = new HashMap<String, Integer>();

		Map<String, Set<String>> file_cache = new HashMap<String, Set<String>>();
		
		// compare each file to all other files
		for (int i = 0; i < files.length; i++) {
			String file1 = files[i];

			for (int j = i + 1; j < files.length; j++) {
				String file2 = files[j];

				// create phrases for each file
				Set<String> file1Phrases, file2Phrases;
				if (!file_cache.containsKey(file1)) {
					file1Phrases = createPhrases(dirName + "/" + file1, windowSize);
					if(file1Phrases == null)
						return null;
					file_cache.put(file1, file1Phrases);
				} else {
					// if the file is already in the cache, skip it
					file1Phrases = file_cache.get(file1);
				}

				if (!file_cache.containsKey(file2)) {
					file2Phrases = createPhrases(dirName + "/" + file2, windowSize);
					if(file2Phrases == null)
						return null;
					file_cache.put(file2, file2Phrases);
				} else {
					// if the file is already in the cache, skip it
					file2Phrases = file_cache.get(file2);
				}
				
				// find matching phrases in each Set
				int matches = findMatches(file1Phrases, file2Phrases);
				
				if (matches < 0)
					return null;

				// if the number of matches exceeds the threshold, add it to the Map
				if (matches > threshold) {
					String key = file1 + "-" + file2;
					numberOfMatches.put(key, matches);
				}				
			}
			
		}		
		
		// sort the results based on the number of matches
		return sortResults(numberOfMatches);
	}
	
	
	/*
	 * This method reads the given file and then converts it into a List of Strings.
	 * It excludes punctuation and converts all words in the file to uppercase.
	 */
	private static List<String> readFile(String filename) {
		if (filename == null) return null;
		
		List<String> words = new ArrayList<String>();
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = in.readLine())  != null) {
				String[] tokens = line.split(" ");
				for (String token : tokens) { 
					// this strips punctuation and converts to uppercase
					words.add(token.replaceAll("[^a-zA-Z]", "").toUpperCase()); 
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return words;
	}

	/*
	 * This method reads a file and converts it into a Set of distinct phrases,
	 * each of size "window". The Strings in each phrase are whitespace-separated.
	 */
	private static Set<String> createPhrases(String filename, int window) {

		// read the file
		List<String> words = readFile(filename);
		
		if (window < 1) return null;
		
		Set<String> phrases = new HashSet<String>();
		
		// create phrases of size "window" and add to Set
		for (int i = 0; i < words.size() - window + 1; i++) {
			String phrase = "";
			for (int j = 0; j < window; j++) {
				phrase += words.get(i+j) + " ";
			}

			// set can add the phrase without checking duplications
			// if (phrases.contains(phrase) == false)
			phrases.add(phrase);

		}
		
		return phrases;
	}

	
	
	/*
	 * Returns a Set of Strings that occur in both of the Set parameters.
	 * However, the comparison is case-insensitive.
	 */
	private static int findMatches(Set<String> myPhrases, Set<String> yourPhrases) {

		if (myPhrases != null && yourPhrases != null) {
			int matches = 0;
			// construct the lower case version of the phrases
			Set<String> yourPhrasesLower = new HashSet<String>();
			for (String phrase : yourPhrases) {
				yourPhrasesLower.add(phrase.toLowerCase());
			}

			for(String mine: myPhrases) {
				if (yourPhrasesLower.contains(mine.toLowerCase())) {
					matches += 1;
				}
			}
			return matches;
		} else {
			return -1;
		}

	}
	
	
	/*
	 * Returns a LinkedHashMap in which the elements of the Map parameter
	 * are sorted according to the value of the Integer, in non-ascending order.
	 */
	private static LinkedHashMap<String, Integer> sortResults(Map<String, Integer> possibleMatches) {
		
		// Because this approach modifies the Map as a side effect of printing 
		// the results, it is necessary to make a copy of the original Map
		Map<String, Integer> copy = new HashMap<String, Integer>();

		for (String key : possibleMatches.keySet()) {
			copy.put(key, possibleMatches.get(key));
		}	
		
		LinkedHashMap<String, Integer> list = new LinkedHashMap<String, Integer>();

		List<Map.Entry<String, Integer>> entries = new ArrayList<>(copy.entrySet());
		// Sort the entries in reverse order
		entries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

		for (Map.Entry<String, Integer> entry : entries) {
			list.put(entry.getKey(), entry.getValue());
		}

		return list;
	}
	

}
