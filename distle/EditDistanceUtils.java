package main.distle;

import java.util.*;

public class EditDistanceUtils {
    
    /**
     * Returns the completed Edit Distance memoization structure, a 2D array
     * of ints representing the number of string manipulations required to minimally
     * turn each subproblem's string into the other.
     * 
     * @param s0 String to transform into other
     * @param s1 Target of transformation
     * @return Completed Memoization structure for editDistance(s0, s1)
     */
    public static int[][] getEditDistTable (String s0, String s1) {
    	String rowWord = s0;
    	String colWord = s1;
    	int numOfRows = rowWord.length() + 1, numOfCols = colWord.length() + 1, row = 0, col = 0;
    	int[][] table = new int[numOfRows][numOfCols];
    	
    	
    	for (; row < numOfRows; row++) {
    		table[row][0] = row;
    	}
    	
    	for (; col < numOfCols; col++) {
    		table[0][col] = col;
    	}
    	
    	
    	for(row = 1; row < numOfRows; row++) {
    		for(col = 1; col < numOfCols; col++) {
    			ArrayList<Integer> operationValue = new ArrayList<Integer>(4);
    			
    			operationValue.add(table[row-1][col-1] + (colWord.charAt(col - 1) != rowWord.charAt(row - 1) ? 1:0));
    			
				if (row >= 2 && col >= 2 && rowWord.charAt(row - 1) == colWord.charAt(col - 2) && 
					colWord.charAt(col - 1) == rowWord.charAt(row - 2)) {
					operationValue.add(table[row - 2][col - 2] + 1);
				}
    			operationValue.add(table[row][col-1] + 1);
    			operationValue.add(table[row-1][col] + 1);
				
				table[row][col] = Collections.min(operationValue);
    		}
    	}
    	
        return table;
    }
    
    /**
     * Returns one possible sequence of transformations that turns String s0
     * into s1. The list is in top-down order (i.e., starting from the largest
     * subproblem in the memoization structure) and consists of Strings representing
     * the String manipulations of:
     * <ol>
     *   <li>"R" = Replacement</li>
     *   <li>"T" = Transposition</li>
     *   <li>"I" = Insertion</li>
     *   <li>"D" = Deletion</li>
     * </ol>
     * In case of multiple minimal edit distance sequences, returns a list with
     * ties in manipulations broken by the order listed above (i.e., replacements
     * preferred over transpositions, which in turn are preferred over insertions, etc.)
     * @param s0 String transforming into other
     * @param s1 Target of transformation
     * @param table Precomputed memoization structure for edit distance between s0, s1
     * @return List that represents a top-down sequence of manipulations required to
     * turn s0 into s1, e.g., ["R", "R", "T", "I"] would be two replacements followed
     * by a transposition, then insertion.
     */
    public static List<String> getTransformationList (String s0, String s1, int[][] table) {
    	List<String> transformationList = new ArrayList<String>();
    	int i = table.length - 1, j = table[0].length - 1;
		
    	getTransformationListHelper(transformationList, s0, s1, i, j, table);
    	
    	return transformationList;
    }
    /**
     * Recursive helper for getTransformationListHelper which returns the Strings in the transformation list
     * @param transformationList String List to keep track of transformations
     * @param s0 String transforming into other
     * @param s1 Target of transformation
     * @param row Integer to track current row
     * @param col Integer to track current column
     * @param table Precomputed memoization structure for edit distance between s0, s1
     * @return void
     */
	private static void getTransformationListHelper(List<String> transformationList, String s0, String s1, int row, int col, int[][] table) {
		if (row == 0 && col == 0) {
			return;
			
		} else if (row > 0 && col > 0 && table[row][col] == table[row - 1][col - 1] && s0.charAt(row - 1) == s1.charAt(col - 1)) {
//			transformationList.add("R");
			getTransformationListHelper(transformationList, s0, s1, row - 1, col - 1, table);
			
		} else if (row > 0 && col > 0 && table[row][col] == table[row - 1][col - 1] + 1 && s0.charAt(row - 1) != s1.charAt(col - 1)) {
			transformationList.add("R");
			getTransformationListHelper(transformationList, s0, s1, row - 1, col - 1, table);
			

		} else if (row > 1 && col > 1 && table[row][col] == table[row - 2][col - 2] + 1 && s0.charAt(row - 2) == s1.charAt(col - 1)
				&& s0.charAt(row - 1) == s1.charAt(col - 2)) {
			transformationList.add("T");
			getTransformationListHelper(transformationList, s0, s1, row - 2, col - 2, table);
			

		} else if (col > 0 && table[row][col] == table[row][col - 1] + 1) {
			transformationList.add("I");
			getTransformationListHelper(transformationList, s0, s1, row, col - 1, table);
			

		} else if (row > 0 && table[row][col] == table[row - 1][col] + 1) {
			transformationList.add("D");
			getTransformationListHelper(transformationList, s0, s1, row - 1, col, table);	
		}
	}
    
    /**
     * Returns the edit distance between the two given strings: an int
     * representing the number of String manipulations (Insertions, Deletions,
     * Replacements, and Transpositions) minimally required to turn one into
     * the other.
     * 
     * @param s0 String to transform into other
     * @param s1 Target of transformation
     * @return The minimal number of manipulations required to turn s0 into s1
     */
    public static int editDistance (String s0, String s1) {
        if (s0.equals(s1)) { return 0; }
        return getEditDistTable(s0, s1)[s0.length()][s1.length()];
    }
    
    /**
     * See {@link #getTransformationList(String s0, String s1, int[][] table)}.
     */
    public static List<String> getTransformationList (String s0, String s1) {
        return getTransformationList(s0, s1, getEditDistTable(s0, s1));
    }

}
