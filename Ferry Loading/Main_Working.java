import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.io.IOException;
import java.util.BitSet;

/**
 * 10261 - Ferry Loading
 * This solution is basically the same as Main.java with 2 differences:
 *  - Instead of a table, just a dpLaneRead (previous column) and dpLaneWrite are used
 *  - the index of the solution is not memoized but rather the dpLaneRead is searched for the first solution.
 * Speed is more or less the same
 *
 *
 * @date 13.02.2020
 * @author Samuel Keusch
 */
public class Main {
	
	public static final StringBuilder OUTPUT = new StringBuilder();
	
	// there are at max 20_000 cars (a car is minimum 100 long and the ferry 2x100, that means 200 * 100 is the maximum of cars
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
		BitSet[] dpLaneRead = new BitSet[ferryLength + 1];
		// writable "layer" for the cases
		BitSet[] dpLaneWrite = new BitSet[ferryLength + 1];
		dpLaneWrite[0] = new BitSet(); // base
		
		// holds the total car length of all cars already "loaded"
		int totalCarLength = 0;
		int i = -1;
		// flag to detect whether the ferry is full or not
		boolean full = true;
		int prevLastModIndex = -1;
		
		
		do {
			i++;
			int currCarL = s_cars[i];

			// last car has weight 0, then we have to abort
			if(currCarL == 0){
				break;
			}
			
			// add car to total length
			totalCarLength += currCarL;
			
			// is full will be set below if there is any case the car can be loaded
			full = true;
			
			// transfer writable layer to read layer
			dpLaneRead = dpLaneWrite;
			dpLaneWrite = new BitSet[ferryLength + 1];
			
			// for each possible loading length that was already discovered
			for(int j = 0; j <= totalCarLength && j <= ferryLength; j++){
				if(dpLaneRead[j] != null){		
					// the index length is always the length of the bigger lane.
					// That means "totalCarLength - j < j" holds
				
					// car can be placed on biggest line so far
					if(j + currCarL <= ferryLength){
						BitSet x = (BitSet) dpLaneRead[j].clone();
						x.set(i, true);
						full = false;
						dpLaneWrite[j + currCarL] = x;
					}
					
					// car can be placed on smaller lane
					int otherLaneLength = (totalCarLength - currCarL) - j + currCarL;
					if(otherLaneLength <= ferryLength){
						BitSet x = (BitSet) dpLaneRead[j].clone();
						x.set(i, false);
						// if other lane will be longer, swap lanes
						if(j < otherLaneLength){
							// flip because a 1-bit means its on the side of the index
							x.flip(0, i+1);
							dpLaneWrite[otherLaneLength] = x;
							full = false;
						} else {
							dpLaneWrite[j] = x;
							full = false;
						}
					}
				}
			}
			
			//System.err.println("Total Length " + totalCarLength + " Field");
			//debugFields(dpLaneWrite);
			//System.err.println(Arrays.toString(dpLaneS));
			// discard old dpLane data (switch dpLane dpLaneS)
			//System.err.println("I is : " + i);
		} while(i < s_cars.length && !full);
		
		if(i == 0){
			OUTPUT.append(0);
			OUTPUT.append(System.lineSeparator());
		} else {
			// i++ 
			int carsLoadable = i;
			
			int f = 100; // we can start at 100, min car width is 100
			while(f < dpLaneRead.length && dpLaneRead[f] == null){
				f++;
			}
			
			BitSet solution = dpLaneRead[f];
			OUTPUT.append(carsLoadable);
			OUTPUT.append(System.lineSeparator());
			for(int s = 0; s < carsLoadable; s++){
				OUTPUT.append(solution.get(s) ? "starboard" : "port");
				OUTPUT.append(System.lineSeparator());
			}
			//System.err.println("SAmount: " + carsLoadable);
		}
		


	}
	
	public static void debugFields(BitSet[] arr){
		for(int i = 0; i < arr.length; i++){
			if(arr[i] != null){
				System.err.println("arr[" + i + "] == " + arr[i]);
			}
		}
	}
}