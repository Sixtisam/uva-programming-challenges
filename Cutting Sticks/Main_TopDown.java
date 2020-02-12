import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.io.IOException;
import java.math.BigInteger;

/**
 * 10003 - Cutting Sticks
 * this very easy understandable variant has the big disadvantage of being a bit slow.
 * Because on every function call, a new stack is being created, java slows down the program remarkably
 *
 * @date 12.02.2020
 * @author Samuel Keusch
 */
public class Main {
	
	public static final StringBuilder OUTPUT = new StringBuilder();
	
	public static int[] s_cuts;
	public static int[][] s_cache;
	
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (reader.ready()) {
				int stickLength = Integer.parseInt(reader.readLine().trim());	
				if(stickLength == 0) {
					break;
				}
				int cutCount = Integer.parseInt(reader.readLine().trim());
				// holds the possible cuts
				s_cuts = new int[cutCount];
				// holds the cached results for a specific start and endpoint of a stick
				String[] cutsStr = reader.readLine().trim().split(" ", cutCount);
				for(int i = 0; i < cutCount ; ++i){
					s_cuts[i] = Integer.parseInt(cutsStr[i]);
				}
				
				s_cache = new int[stickLength][stickLength + 1];
				
				OUTPUT.append("The minimum cutting is " + solveCuttingSticks(0, stickLength, 0, cutCount) + ".");
				OUTPUT.append(System.lineSeparator());
            }
			System.out.print(OUTPUT);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
	
	/**
	 * Solves recursively the Cutting Sticks problem.
	 * Top-Down variant
	 * 
	 * @param start - The start of the stick
	 * @param end - The end of the stick
	 * @param indexStart - First index of a possible cut
	 * @param indexEnd - Last index of a possible cut
	 */
	public static int solveCuttingSticks(int start, int end, int indexStart, int indexEnd){
		// if value exists in cache, use cache value
		if(s_cache[start][end] != 0){
			return s_cache[start][end];
		}
		
		// if no cuts need to be done between
		if(indexStart == indexEnd){
			return 0;
		}
		
		int minCost = Integer.MAX_VALUE;
		// for each possible cut between start and end:
		for(int i = indexStart; i < indexEnd; ++i){
			// calculate cost of left and right stick
			int cost = solveCuttingSticks(start, s_cuts[i], indexStart, i) + solveCuttingSticks(s_cuts[i], end, i+1, indexEnd);
			if(cost < minCost){ // store min cost
				minCost = cost; 
			}
		}
		
		// set value in cache for future iterations
		return s_cache[start][end] = (end - start) + minCost;
	}
}