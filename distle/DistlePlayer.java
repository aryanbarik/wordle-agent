package main.distle;

import static main.distle.EditDistanceUtils.*;
import java.util.*;

/**
 * AI Distle Player! Contains all logic used to automagically play the game of
 * Distle with frightening accuracy (hopefully).
 */
public class DistlePlayer {
	private int dictionaryWordCount, minWordLength, maxWordLength, editDistance;

	private ArrayList<String> pastGuesses, transformationList, wordList;
	private Set<String> dictionary;
	private Map<wordTuple, Integer> wordGraph;
	private Map<String, Double> ratedGuesses; 


	
    /**
     * Constructs a new DistlePlayer.
     * [!] You MAY NOT change this signature, meaning it may not accept any arguments.
     * Still, you can use this constructor to initialize any fields that need to be,
     * though you may prefer to do this in the {@link #startNewGame(Set<String> dictionary, int maxGuesses)}
     * method.
     */
    public DistlePlayer () {
    	this.minWordLength = Integer.MAX_VALUE;
    	this.maxWordLength = Integer.MIN_VALUE;
    	this.wordGraph = new HashMap<>();
    	this.ratedGuesses = new HashMap<>(); 
    }
    
    /**
     * Called at the start of every new game of Distle, and parameterized by the
     * dictionary composing all possible words that can be used as guesses / one of
     * which is the correct.
     * 
     * @param dictionary The dictionary from which the correct answer and guesses
     * can be drawn.
     * @param maxGuesses The max number of guesses available to the player.
     */
    public void startNewGame (Set<String> dictionary, int maxGuesses) {
    	this.wordList = new ArrayList<String>(1);
    	wordList.addAll(dictionary);
    	
    	this.dictionaryWordCount = dictionary.size();

        this.minWordLength = this.getMinWordLength(this.wordList);
        this.maxWordLength = this.getMaxWordLength(this.wordList);
        this.pastGuesses = new ArrayList<String>();
        this.minWordLength = this.getMinWordLength(wordList);
        this.maxWordLength = this.getMaxWordLength(wordList);
        this.pastGuesses = new ArrayList<String>(maxGuesses);
    }
    
    /**
     * Method to find the length of the shortest word in the dictionary
     * 
     * @param dictionary Set of words which we can guess from
     * @return the length of the shortest word in the dictionary
     */
    private int getMinWordLength(ArrayList<String>wordList) {
    	for (int i = 0; i < wordList.size() ; i++) {
    		if (wordList.get(i).length() < minWordLength) {
    			minWordLength = wordList.get(i).length();
    		}
    	}
    	return minWordLength;
    }
    
    /**
     * Method to find the length of the longest word in the dictionary
     * 
     * @param dictionary Set of words which we can guess from
     * @return the length of the longest word in the dictionary
     */
    private int getMaxWordLength(ArrayList<String>wordList) {
    	for (int i = 0; i < wordList.size() ; i++) {
    		if (wordList.get(i).length() > maxWordLength) {
    			maxWordLength = wordList.get(i).length();
    		}
    	}
    	return maxWordLength;
    }
    
    /**
     * Requests a new guess to be made in the current game of Distle. Uses the
     * DistlePlayer's fields to arrive at this decision.
     * 
     * @return The next guess from this DistlePlayer.
     */
    public String makeGuess () {
    	
    	
    	if (this.pastGuesses.isEmpty()) {
    		// 1st guess behavior
	    	ArrayList<String>wordList = new ArrayList<String>();
	    	wordList.addAll(dictionary);
	    	return wordList.get(0);
    		
    	} else {
    		// Directional guessing behavior (try to rule out many words as possible)
    		
 //   		guess the word with the lowest average editDistance
//    		return this.ratedGuesses.firstEntry().getValue() ;
    		// for each entry in DistleNode
    		
    		// now set
    		
        	// Guessing to win behavior, AKA educatedGuess
	    	ArrayList<String>wordList = new ArrayList<String>();
	    	wordList.addAll(dictionary);
	    	return wordList.get(0);
	    	// lol idk
    	}
    }
    
    /**
     * Called by the DistleGame after the DistlePlayer has made an incorrect guess. The
     * feedback furnished is as follows:
     * <ul>
     *   <li>guess, the player's incorrect guess (repeated here for convenience)</li>
     *   <li>editDistance, the numerical edit distance between the guess and secret word</li>
     *   <li>transforms, a list of top-down transforms needed to turn the guess into the secret word</li>
     * </ul>
     * [!] This method should be used by the DistlePlayer to update its fields and plan for
     * the next guess to be made.
     * 
     * @param guess The last, incorrect, guess made by the DistlePlayer
     * @param editDistance Numerical distance between the guess and the secret word
     * @param transforms List of top-down transforms needed to turn the guess into the secret word
     */
    public void getFeedback (String guess, int editDistance, List<String> transforms) {
        this.pastGuesses.add(guess);
        
        
        // always want this to happen
        Iterator<String> dictIterator = dictionary.iterator();
        while (dictIterator.hasNext()) {	
    		String currentWord = dictIterator.next();
        	
        	List<String> guessTransforms = EditDistanceUtils.getTransformationList(guess, currentWord);
        	
        	if (!(guessTransforms.equals(transforms))) {
        		dictIterator.remove();
        	}
    	}
        
        // specific behavior for 2nd guess: initialize the graph
        if (this.pastGuesses.size() <= 1) {
        	// make graph of all words
    		this.getDistleNodeGraph(dictionary);
    		// now let makeGuess() handle it by perusing our graph!
    		// let makeGuess() know we want a directional guess
        }
    	
    	this.dictionaryWordCount = this.dictionary.size();
        this.minWordLength = this.getMinWordLength(this.wordList);
        this.maxWordLength = this.getMaxWordLength(this.wordList);
    }
    
	/**
	 * Constructs a graph where each word is a key-value pair with 
	 * every other word in the dictionary, and places that map to a
	 * key-value pair putting an int as the key and the pair of
	 * words as the value
	 * 
	 * @param dictionary
	 */
    private void getDistleNodeGraph(Set<String> dictionary) {
    	Iterator<String> dictIterator = dictionary.iterator();
    	
		while (dictIterator.hasNext()) {
			
			String currentWord = dictIterator.next();
			String nextWord = dictIterator.next();
			
        	this.wordGraph.put(this.linkWords(currentWord, nextWord), EditDistanceUtils.editDistance(currentWord, nextWord));
        }
		
    }
    
	/**
	 * Creates a wordTuple from two words given
	 * 
	 * @param parentWord word to be creating edges from
	 * @param dictionary rest of the words to be connected to
	 */
    private wordTuple linkWords (String word1, String word2) {
		return new wordTuple(word1, word2);
	}
    
    /**
     * Calculates average rated guesses from the remaining dictionary entries and should exclude tuples
     * with words no longer in the dictionary
     * 
     * @param dictionary
     */
    private void makeRatedGuesses (Set<String> dictionary) {
    	ratedGuesses.put(this.getAverageEditDistance(word), word);
		
	}
	
	/**
	 * Calculates the average editDistance of a word using all wordTuples
	 * 
	 * @param word whose average editDistance to be calculated
	 * @return Double value average editDistance to all other words
	 */
	private double getAverageEditDistance (String word) {
		// entropy calculation here basically
		
		int sum = 0, count = 0;
		Map<wordTuple, Integer> currentNodeGraph = this.wordGraph;
		
		for (Map.Entry<wordTuple,Integer> currentWord : currentNodeGraph.entrySet()) {
            
			if (currentWord.getKey().hasWord(word)) {
				sum += currentWord.getValue();
	            count++;
			}
        }
		
		double average = (double) sum / count;
		return average;
	

    }
	
	private class wordTuple {
		String word1;
		String word2;
		
		wordTuple(String word1, String word2) {
			this.word1 = word2;
			this.word2 = word2;
			
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			
			if (this.getClass() != obj.getClass())
				return false;
						
			wordTuple otherTuple = (wordTuple) obj;
			
			if (otherTuple.word1.equals(this.word2) && otherTuple.word2.equals(this.word1))
				return true;
			
			return false;
		}
		
		@Override
		public int hashCode() {
			return this.word1.hashCode() * this.word2.hashCode();
		}
		
		private boolean hasWord (String wordToCheck) {
			return (wordToCheck.equals(this.word1) || wordToCheck.equals(this.word2));
		}
	}

}
