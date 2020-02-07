import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.math.BigInteger;

/**
 * 10069 - Distinct Subsequences
 * The solution was found by drawing a table (like edit distance) on a piece of paper.
 * The lower-right-most cell should contain the solution.
 * After each cell was filled in by hand, I tried to recognize a pattern. 
 *
 * The algorithm basically calculates the occurences count for all substrings of the needle.
 * By remembering those counts, the solution can be easily built.
 *
 * The basic principle explained:
 * Whenever a substring of the HAYSTACK matches the last character of the current substring of the NEEDLE,
 * the count (referring to the occurence count of the NEEDLE previous substring of HAYSTACK) can increased by
 * the occurence count of the previous substring of the NEEDLE in the previous HAYSTACK.
 * Example:
 * "b" occurs 3x in "babgb"
 * "ba" occurs 1x in "babgb"
 * Calculation of "ba" in "babgba":
 * We know it must occur at least 1x, so we can take the 1x as base.
 * How many times will it occur additionally?
 *   - 1x because we previously, "ba" appears 1x in "babgb"
 *   - The substring of the needle "b" occurs 3x in the substring "babgb".
 *     If we add char "a", we can form 3 different subsequences (we can start at 3 different b's, but end always with the same "a")
 * ==> This results in 4
 * 
 *
 * @date 07.02.2020
 * @author Samuel Keusch
 */
public class Main {
	
	public static final StringBuilder OUTPUT = new StringBuilder();
	
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (reader.ready()) {
				int testcaseCount = Integer.parseInt(reader.readLine().trim());
				
				for(int i = 0; i < testcaseCount; i++){
					solve(reader.readLine().trim(), reader.readLine().trim());
				}
            }
			System.out.print(OUTPUT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public static void solve(String haystack, String needle){
		// table will hold for all substrings (needle and haystack) the occurences count
		// Example: [ba][babg] = 1 means "ba" occurs 1 time in babg
		//          [ba][babgba] = 4 means "ba" occurs 4 times in "babgba"
		BigInteger[][] dpTable = new BigInteger[needle.length() + 1][haystack.length() + 1];
		
		// base case: empty string has 1 distinct occurences
		for(int i = 0; i < dpTable[0].length; i++){
			dpTable[0][i] = BigInteger.ONE;
		}
		// first column must be set to 0 (except first row)
		for(int i = 1; i < dpTable.length; i++){
			dpTable[i][0] = BigInteger.ZERO;
		}
		
		// traverse all substring of the needle. Example: bag -> b, ba, bag
		for(int y = 1; y < dpTable.length; y++){
			char currentChar = needle.charAt(y - 1);
			// traverse each time all substrings of the haystack: Example: babgbag -> b, ba, bab, babg, babgb, babgba, babgbag
			for(int x = 1; x < dpTable[0].length; x++){
				if(currentChar == haystack.charAt(x - 1)) {
					// in case the newly added character of the haystack is equal to the last character of the current needle substring:
					// add the count from the previous substring of needle and previous substring of haystack (diagonally left up)
					// Example: When at [ba][ba], look at [b][b]
					dpTable[y][x] = dpTable[y-1][x-1].add(dpTable[y][x-1]);
				} else {
					// in both cases, the count from the previous
					dpTable[y][x] = dpTable[y][x-1];
				}
			}
		}
		
		OUTPUT.append(dpTable[dpTable.length - 1][dpTable[0].length - 1]);
		OUTPUT.append(System.lineSeparator());
	}
}