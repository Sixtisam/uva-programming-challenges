import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.io.IOException;
import java.util.BitSet;

/**
 * 10271 - Chopsticks
 * Assumption: If we have 4 Sticks, one must ALWAYS pick neighbouring sticks for a pair.
 * Proof: 4 Chopsticks, Pick 2: a b c d :   
 *   Then this need to hold:   
 *           (d-a)^2 + (b + c)^2  < (b-a)^2 + (d-c)^2
 *   Minimal value on left side can be reached by assuming b == c
 *			 (d-a)^2 < (b-a)^2 + (d-b)^2
 *   We can infer that (b-a) + (d-b) == d-a 
 *   So the following equation must be false:
 *      x^2 < (x/2)^2 + (x/2)^2   =>  x^2 < (x^2)/2 
 *   Which is obviously false, which means our assumption that neighbours are always better than any other combination is proven.
 *								
 * See approach in comment below.
 *
 * @date 12.03.2020/13.03.2020
 * @author Samuel Keusch
 */
public class Main {
	
	public static final StringBuilder OUTPUT = new StringBuilder();
	
	
    public static void main(String[] args) {
		//long now = System.nanoTime();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			int testcaseCount = Integer.parseInt(reader.readLine().trim());
			
			for(int i = 0; i < testcaseCount; i++){	
				int guestCount = nextInt(reader) + 8; // TODO ADD 8
				int stickCount = nextInt(reader);
				int[] sticks = new int[stickCount];
				
				// store sticks decreasing order
				for(int j = stickCount - 1; j >= 0; j--){
					sticks[j] = nextInt(reader);
				}
				
				OUTPUT.append(solveSticks(guestCount, sticks));
				OUTPUT.append(System.lineSeparator());
			}
			System.out.print(OUTPUT);
			//System.err.println("Took " + ((System.nanoTime() - now) / 1_000_000) + "ms");

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
	
	public static int nextInt(BufferedReader reader) throws IOException {
		int next;
		
		do {
			next = reader.read();
		} while(!(next >= '0' && next <= '9'));
		
		int number = 0;
		while(next >= '0' && next <= '9'){
			number = (10 * number) + (next - '0');
			next = reader.read();
		}
		return number;
	}
	
	/*
	 * Example Table.
	 *  - "XXX" means MAX_VALUE resp. not used because not a possible case
	 * |100| 50 | 40 | 30 | 20 | 10 |  8 |  4 |  1 |       (Coordinate system topleft edge 0/0)
	 * |XXX|XXXX|100 |100 |100 |100 |  4 |  4 |  4 |
	 * |XXX|XXXX|XXXX|XXXX|XXXX|200 |104 |104 |  8 |
	 * |XXX|XXXX|XXXX|XXXX|XXXX|XXXX|XXXX|XXXX|113 |
	 * 
	 *
	 * The very first cell is 100 because (50-40)^2, it is the leftmost position for the first triple (40,50,100)
	 * Row 1 means we have to pick one stick-set, row 2 for 2 guest, etc.
	 * Column 1 means, there is only 1 stick (therefor never possible), as well as Column 2 is never possible
	 * The row are traverse top->bottom and columns left->right.
	 * Let's say we are calculate cell [g][s], which implies we have calculated all cells to the left and all rows above g.
	 * dp[g][s] will adopt whichever of the two values is lower:
	 *  - badness + dp[g-1][s-2]
	 *     - badness for the stick s and s-1
	 *     - stick s and s-1 are already used for the badness, therefore we need the optimal stick-set(s) if we can choose from s-2 sticks.
	 *  - the value of the cell to left (Is the case when the current set (stick s and stick (s-1)) has a big difference
	 *
	 * The requirement that the third stick always have to be longer than the two shorter sticks is automatically solved by using a 
	 * decreasing array. The second stick-set (for guest 2) can only be selected if there are 6 or more sticks.
	 */
	public static int solveSticks(int guests, int[] chopsticks){
		//System.err.println("Guests: " + guests);
		//System.err.println("Chopsticks: " + Arrays.toString(chopsticks));
		
		// because of the first row, guest index starts at 1
		int[][] dp = new int[guests + 1][chopsticks.length];
		
		for(int i = 0; i < dp.length; i++){
			Arrays.fill(dp[i], Integer.MAX_VALUE);
		}
		// first row has badness of 0 as it indicates picking 0 stick-sets
		for(int j = 0; j < dp[0].length; j++){
			dp[0][j] = 0;
		}
		
		// traverse all guests
		for(int g = 1; g <= guests; g++){
			// start with 3 sticks, expand to n sticks   (3 sticks because one cannot selected a triple-set from 2 sticks)
			for(int s = g*3 - 1; s < chopsticks.length; s++){
				// calculate badness for the 2 sticks
				int badness = chopsticks[s-1] - chopsticks[s];
				badness *= badness;
				
				// either choose newly added stick as the set for this guest OR take already discovered stick from existing s-1 sticks.
				dp[g][s] = Math.min(dp[g][s-1], dp[g-1][s-2] + badness);
			}
		}
		
		return dp[guests][chopsticks.length - 1];
	}	
	
	
	/*	
	    My previous attempt did use an increasing array. It did work for the sample from the uva pdf but no for simple
		cases where there are exactly the amount of sticks given which are required to find a solution.
		// dp[g][s] means pick g chopstick-pairs from s sticks
		for(int g = 1; g <= guests; g++){
			for(int s = g*3; s <= chopsticks.length; s++){
				// new pair: stick (s-2, s-1)
				
				// calculate badness for neighbours
				int badness = chopsticks[s-2] - chopsticks[s-3];
				//System.err.println("Badness: " + chopsticks[s-2] + " - " + chopsticks[s-3]);
				badness *= badness;
				// only consider adding new pair if there is enough place
				System.err.println("S is: " + s + " other is: " + (chopsticks.length - (3*(guests-g))));
				// this condition is to tight. basically it required that every stick set consists of 3 neighbours, but ignores the fact
				// that sometimes, the longest stick in the set is not adjacent to any of the two shorter sticks.
				if(s >= chopsticks.length - (3*(guests-g))){
					// min value from s-1 sticks
					dp[g][s] = dp[g][s-1];
				} else {
					System.err.println("Here");
					// min: min value from s-1 sticks OR min value from g-1 s-2 sticks + badness of current pair
					dp[g][s] = Math.min(dp[g][s-1], dp[g-1][s-2] + badness);
				}
			}
		}*/
}