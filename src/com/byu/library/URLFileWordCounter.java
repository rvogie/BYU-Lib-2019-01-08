package com.byu.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Collectors;

public class URLFileWordCounter {

	/**
	 * Return a Map<String,Integer> of each word in the file and the number of
	 * occurrences of the file. 
	 * @param fileContents The contents of the file as a String
	 * @return the Map<String,Integer>
	 */
	public static Map<String, Integer> getMapOfWordCount(String fileContents) {
		String[] words = fileContents.split("\\s");
		Map<String,Integer> wordMap = new HashMap<>();
		for(int i=0; i < words.length; i++) {
			String fileWord = words[i].replaceAll("[^a-zA-Z0-9_-]", "").trim();
			if( fileWord != null && fileWord.length() > 0 ) {
				//System.out.printf("%s replaced %s\n", words[i], fileWord);
				Integer value = wordMap.get(fileWord);
				if( value == null ) {
					wordMap.put(fileWord, new Integer(1));
				} else {
					wordMap.put(fileWord, ++value);
				}
			}
		}
		
		return wordMap;
	}

	/**
	 * 
	 * @param fileContents
	 * @return
	 */
	public static ArrayList<Entry<String, Integer>> getTopThreeWordOccurrences(String fileContents, int count) {
		Map<String,Integer> wordMap = getMapOfWordCount(fileContents);
		Map<String,Integer> sorted = wordMap
				.entrySet()
				.stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(
						Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1,e2) -> e2,
								LinkedHashMap::new));
				
		ArrayList<Entry<String,Integer>> topArrayList = new ArrayList<Entry<String,Integer>>();
		Iterator<Map.Entry<String,Integer>> entries = sorted.entrySet().iterator();
		int currentMax = 0; 
		while( entries.hasNext() && count > 1 ) {
			Entry<String,Integer> entry = entries.next();
			Integer entryValue = entry.getValue();
			if( currentMax == 0 ) {
				currentMax = entryValue.intValue();
				topArrayList.add(entry);
			} else if( entryValue < currentMax ) {
				currentMax = entryValue;
				topArrayList.add(entry);
				count--;
			}
			//System.out.printf("Top values: %s count = %d\n", entry.getKey(), entry.getValue());
		}
		
		return topArrayList;
	}
	
	/**
	 * Determine the number of times a word occurs in a file
	 * @param fileContents
	 * @param word
	 * @return the number of occurrences of word in fileContents.
	 */
	public static int getWordCount(String fileContents, String word) {
		Map<String, Integer> wordMap = getMapOfWordCount(fileContents);
		Integer count = wordMap.get(word);
		
		return (count == null) ? 0:count.intValue();
	}
	
	/**
	 * Get the file data for the URL Path
	 * @param urlPath
	 * @return
	 */
	public static String getFileDataForURLPath(String urlPath) {
		URLFileReader response = new URLFileReader();

		return response.getWordDataForUrlPath(urlPath);
	}

	/**
	 * Print the commands available commands.
	 */
	public static void printHelp() {
		System.out.println("\nThis program will return the top 3 occurrences of words contained in a valid url file path.\n"
				+ "The url file path is optional, if noy entered it will default to http://www.gutenberg.org/files/84/84-0.txt\n"
				+ "To determine the number of times a word is included in the file, enter a comma and the word after the url file path entered.\n");
		System.out.println("You may enter the following Commands:\n");
		System.out.println("exit - will exit.");
		System.out.println("help - will print a description and the list of program commands.");
		System.out.println("\n\nYou may enter an optional URL File Path and/or optional comma and word: <http://www.mywebsite/somefile.txt,happy>");
	}
	
	/**
	 * Entry point for entering a URL file path or using the default to retrieve word counts
	 * @param args
	 */
	public static void main(String[] args) {
		// Wait for key input
		final String fDefaultURL = "http://www.gutenberg.org/files/84/84-0.txt";
		printHelp();
		Scanner scanner = new Scanner(System.in);
		String inputString = null;

		while( (inputString = scanner.nextLine()) != null ) {
			if( inputString.equalsIgnoreCase("exit") ) {
				break;
			} else if( inputString.equalsIgnoreCase("help") ) {
				printHelp();
			} else {
				String urlPath = fDefaultURL;
				String word = null;
				String[] tokens = inputString.split(",");
				if( tokens.length <= 2 ) {
					for(int i=0; i < tokens.length; i++) {
						if( i == 0 ) {
							if( (tokens[i] != null && tokens[i].length() > 0) ) {
								urlPath = tokens[i];
							}
						} if( tokens[i] != null ){
							word = tokens[i];
						}
					}
					String urlData = getFileDataForURLPath(urlPath);
					if( urlData != null && urlData.length() > 0 ) {
						if( word == null || word.length() == 0 ) {
							// Find the top 3 word occurrences
							ArrayList<Entry<String,Integer>> topThreeMap = getTopThreeWordOccurrences(urlData,3);
							topThreeMap.forEach((e)->System.out.printf("Top values: %d occurrences of the word '%s'\n", e.getValue(), e.getKey()));
						} else {
							// Find the number of times a word occurs
							int wordCount = getWordCount(urlData, word);
							System.out.printf("%d occurrences of the word '%s' in the file %s.\n", wordCount, word, urlPath);
						}
					}
				} else {
					System.out.printf("Too many arguments; URLfilepath,word.\n", urlPath);
				}
				System.out.println("Enter <filepath,word> both are optional:");
			}
		}
		
		scanner.close();
	}

}
