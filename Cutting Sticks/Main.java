import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.io.IOException;
import java.math.BigInteger;

/**
 * 10003 - Cutting Sticks
 * 
 * First, I did the "recursive approach" (dont go into recursive function if value already calculated). 
 * But it took more than one sec, so i tried to search for the other approache, Bottom-up
 *
 * Explanation is given in method solveCuttingSticksBottomUp
 *
 * @date 12.02.2020
 * @author Samuel Keusch
 */
public class Main {
	
	public static final StringBuilder OUTPUT = new StringBuilder();
	
    public static void main(String[] args) {
		long now = System.nanoTime();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (reader.ready()) {
				int stickLength = Integer.parseInt(reader.readLine().trim());	
				if(stickLength == 0) {
					break;
				}
				int cutCount = Integer.parseInt(reader.readLine().trim());
				// holds the possible cuts (including end and start "cut")
				int[] cuts = new int[cutCount + 2];

				String[] cutsStr = reader.readLine().trim().split(" ", cutCount);
				for(int i = 0; i < cutCount; ++i){
					cuts[i + 1] = Integer.parseInt(cutsStr[i]);
				}
				cuts[cutCount + 1] = stickLength;
				
				OUTPUT.append("The minimum cutting is " + solveCuttingSticksBottomUp(cuts) + ".");
				OUTPUT.append(System.lineSeparator());
            }
			System.out.print(OUTPUT);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
	
	/**
	 * bottom-up variant
	 */
	public static int solveCuttingSticksBottomUp(int[] cuts){
		// represents costs between 2 cuts.
		// dpTable[2][4] is the minimal cost (already known) between cut 2 and 4
		int[][] dpTable = new int[cuts.length][cuts.length];
		
		// The first 3 diagonals to the right can be precomputed:
		
		// - First is cut A to cut A which is 0 cost (is never used)
		// - Second is cut A to cut B which is also 0 cost
		/* unnecessary because of java always inits with 0
		for(int i = 0; i < cuts.length - 1; ++i){
			 // between cut A and cut B is no other cut -> cost of 0
			dpTable[i][i + 1] = 0;
		}*/
		
		// - Third is cut A to cut C, which is always "cut C - cut A" (there one cut between, cut B)
		// could also be calculated below (but I think its faster and better understandable)
		for(int i = 0; i < cuts.length - 2; ++i){
			 // between cut A and cut C is only cut B -> this requires one cut (cut B), which costs exactly cut C - cut A
			dpTable[i][i+2] = cuts[i + 2] - cuts[i];
		}
		
		
		
		//System.err.println(Arrays.deepToString(dpTable));

		// Explanation:

		// We have to start from the "bottom", therefore
		// Some possibilites as example:
		// Cut 1 to 2: needs no cut (already explained above
		// Cut 1 to 3: 1-2 2-3   -> needs one cut, which is always "cut 3 - cut 1"
		// Cut 1 to 4: 1-2 2-4, 1-3 3-4
		// Cut 1 to 5: 1-2 2-5, 1-3 3-5, 1-4 4-5
		// ... and so on
		
		// If we think of Cut 1 to 5 as a cell, we need somehow access all the combinations based on that cell.
		// This can be represented by putting all segments starting with 1- (1-2, 1-3, 1-4) in the same row
		//      and segments ending with 5 in the same column (2-5, 3-5, 4-5)
		//
		// To conclude, to be able to compute a cell, all cells to the left and to the bottom must already be calculated:
		// Each cell needs  																example: cell [1][5] which stands for min cost between cut 1 and cut 5
		//      a) all cells to the left calculated  (contains min cost for cuts to COLUMN) example: 1-2 1-3 1-4
		//      b) all cells to the bottom calculated (contains min cost for cuts to ROW)   example: 2-5 3-5 4-5
		// .. in order to be computed.
		// To achieve this, we can compute all fields on the same diagonal line, then move on to the next diagonal line, 
		// until the last cell remaining (top right cell), which is the solution

		// d is the iteration counter (how many times we have to move the diagonal line)
		// this is "#cuts - 1" (table contains #cuts + 2 columns, the first 3 are skipped -> #cuts - 1)
		for(int d = 3; d <= dpTable.length; ++d){
			// i increments column and row
			for(int i = 0; i < dpTable.length - d; ++i){
				int x = i; // x is the column pointer
				int y = d + i; // y is the column pointer
				
				
				// We now look specifically on the stick left between cut X and cut Y
				// The number of possibilites is the number of cuts between X and Y
				// The min cost of each segment needed is already calculated due to the table structure.
				
				dpTable[x][y] = Integer.MAX_VALUE;
				// k is the border between X and Y 
				for(int k = x + 1; k < y; ++k){
					// cost composes of cut cost 1st segment (cut X to cut K) and 2nd segment (cut K to cut Y) AND cost of the cut itself (cut K)
					int cost = dpTable[x][k] + dpTable[k][y] + (cuts[y] - cuts[x]);
					if(cost < dpTable[x][y]){
						dpTable[x][y] = cost;
					}
				}
			}
		}
		
		// This cell contains the min cost for cutting the whole stick 
		return dpTable[0][dpTable.length - 1];
	}
}