import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.io.IOException;
import java.util.BitSet;

/**
 * 10261 - Ferry Loading
 * Not really faster than the other one 
 *
 * The concept is to memoize the possible cases, which do not exceed the fery size in a dp table.
 * The memo key is the size of the longer lane.
 * With this approach, I can add the cars in the queue order. All possible constellations, in which a single lange exceeds the ferry length are discarded.
 * 
 *
 * To not waste space and time, the longer lange is always the "left" lane (lane with the memo index).
 *   That means if somewhere, when I have a lane 200 and the other 300, the constellation must be accessed by dp[x][300] and never by dp[x][200]
 * The "memoization" of the lane per car is done by a BitSet. 1 means its in the index lane, 0 in the other lane.
 * This could be optimized further I think, because each time I load another car, I copy many BitSet's which is probably not fast.
 * 
 *
 * @date 13.02.2020
 * @author Samuel Keusch
 */
public class Main {
	
	public static final StringBuilder OUTPUT = new StringBuilder();
	
	// there are at max 20_000 cars (a car is min 100 long and the ferry 2x100, that means 200 is the maximum of cars
	public static int[] s_cars = new int[202];
	
    public static void main(String[] args) {
		//long now = System.nanoTime();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			int testcaseCount = Integer.parseInt(reader.readLine().trim());
			
			for(int i = 0; i < testcaseCount; i++){	
				reader.readLine(); // skip empty line
				// read in ferry length and convert to centimeters
				int ferryLength = Integer.parseInt(reader.readLine().trim()) * 100;
				
				
				// read in the car lengths
				int j = 0;
				do {
					s_cars[j++] = Integer.parseInt(reader.readLine().trim());
				} while(s_cars[j-1] != 0 && j < 201);
				s_cars[201] = 0;
				// last "car" has always 0 weight, it will therefore overwrite existing weight at that position
				// therefore its guaranteed, that the testcases will not conflict with eachother
				
				solveFerryLoading(ferryLength);
				if(i != testcaseCount - 1){
					OUTPUT.append(System.lineSeparator());
				}
			}
			System.out.print(OUTPUT);
			//System.err.println("Took " + ((System.nanoTime() - now) / 1_000_000) + "ms");

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
	
	/**
	 * bottom-up variant
	 */
	public static void solveFerryLoading(int ferryLength){
		// represents one lane of a ferry
		BitSet[][] dpLane = new BitSet[202][ferryLength + 1];
		dpLane[0][0] = new BitSet(); // base
		
		// holds the total car length of all cars already "loaded"
		int totalCarLength = 0;
		int i = -1;
		// flag to detect whether the ferry is full or not
		int lastModIndex = -1;
		int prevLastModIndex = -1;
		
		assert s_cars[201] == 0;
		do {
			i++;
			int currCarL = s_cars[i];
			
			// must be before the break
			// is full will be set below if there is any case the car can be loaded
			prevLastModIndex = lastModIndex;
			// last car has weight 0, then we have to abort
			if(currCarL == 0){
				prevLastModIndex = lastModIndex;
				break;
			}
			
			// this after the break
			lastModIndex = -1;

			// add car to total length
			totalCarLength += currCarL;
			
			// for each possible loading length that was already discovered
			for(int j = 0; j <= totalCarLength && j <= ferryLength; j++){
				if(dpLane[i][j] != null){		
					// the index length is always the length of the bigger lane.
					// That means "totalCarLength - j < j" holds
				
					// car can be placed on biggest line so far
					if(j + currCarL <= ferryLength){
						BitSet x = (BitSet) dpLane[i][j].clone();
						x.set(i, true);
						lastModIndex = j + currCarL;
						dpLane[i+1][lastModIndex] = x;
					}
					
					// car can be placed on smaller lane
					int otherLaneLength = (totalCarLength - currCarL) - j + currCarL;
					if(otherLaneLength <= ferryLength){
						BitSet x = (BitSet) dpLane[i][j].clone();
						x.set(i, false);
						// if other lane will be longer, swap lanes
						if(j < otherLaneLength){
							// flip because a 1-bit means its on the side of the index
							x.flip(0, i+1);
							dpLane[i+1][otherLaneLength] = x;
							lastModIndex = otherLaneLength;
						} else {
							dpLane[i+1][j] = x;
							lastModIndex = j;
						}
					}
				}
			}
			
			//System.err.println("Total Length " + totalCarLength + " Field");
			
			//System.err.println(Arrays.toString(dpLaneS));
			// discard old dpLane data (switch dpLane dpLaneS)
			//System.err.println("I is : " + i);
		} while(lastModIndex != -1);
		
		//debugFields(dpLane);
		
		if(prevLastModIndex == -1){
			OUTPUT.append(0);
			OUTPUT.append(System.lineSeparator());
		} else {
			int carsLoadable = i;
			//System.err.println("Try to extract solution from [" + (i) + "][" + prevLastModIndex + "]");
			BitSet solution = dpLane[i][prevLastModIndex];
			assert solution != null;
			OUTPUT.append(carsLoadable);
			OUTPUT.append(System.lineSeparator());
			for(int s = 0; s < carsLoadable; s++){
				OUTPUT.append(solution.get(s) ? "starboard" : "port");
				OUTPUT.append(System.lineSeparator());
			}
			//System.err.println("SAmount: " + carsLoadable);
		}
		


	}
	
	public static void debugFields(BitSet[][] arr){
		for(int j = 0; j < arr.length; j++){
			for(int i = 0; i < arr[0].length; i++){
				if(arr[j][i] != null){
					System.err.println("arr["+j+"][" + i + "] == " + arr[j][i]);
				}
			}
		}
	}
}