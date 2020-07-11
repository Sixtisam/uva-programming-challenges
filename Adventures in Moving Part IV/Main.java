import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 10201 - Adventures in Moving Part IV
 *
 * The approach is quite simple:
 * For each gas station, we try out all possible fill up amounts: 0, 1, 2, ... 200 (up to 200 in total)
 * The most important fact is, that if we are at station X with tank volume Y, we don't care how this was reached.
 * Because of this, the memory needed is #stations * 200 * 32. Basically we can eliminate some branches of our possibility tree.
 * The tree will never have more than 200 leaves.
 * Impossible is recognized by checking for unreachable gas cost.
 * It's also important to know, that if we can reach the Big City, we exactly have 100 liters left in the tank (as required).
 * any additional liters would not be an optimal solution.
 *
 * @date 05.05.2020
 * @author Samuel Keusch
 */
public class Main {
	
	public static final StringBuilder OUTPUT = new StringBuilder();
	
	// at most 10'000miles, combinated with a price of 2'000  plus 1 is unreachable (or leakage?)
	public static final int UNREACHABLE_GAS_GOST = 2000 * 10000 + 1;
	
	public static class GasStation {
		public final int pos;
		public final int price;
		
		public GasStation(int pos, int price){this.pos = pos; this.price = price;}
		@Override
		public String toString(){ return "{" + pos  + "," + price + "}";}
	}
	
    public static void main(String[] args) {
		//long now = System.nanoTime();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			int testcaseCount = Integer.parseInt(reader.readLine().trim());
			
			reader.readLine(); // skip first empty line
			for(int i = 0; i < testcaseCount; i++){	 
				// read in distance waterloo -> Big City
				int distance = Integer.parseInt(reader.readLine().trim());
				
				// read in the gas stations
				ArrayList<GasStation> stations = new ArrayList<>();
				// 0th station serves as the starting point
				stations.add(new GasStation(0,0));
				while(true){
					String currentLine = reader.readLine();
					// if line is null or empty, we traversed all gas stations of this testcase and can break
					if(currentLine == null || currentLine.trim().equals("")) break;
					
					String[] splitted = currentLine.split(" ");
					int pos = Integer.parseInt(splitted[0]);
					// only consider gas station that are between Waterloo and Big City. Those beyond are not in our interest
					if(pos <= distance){
						int price = Integer.parseInt(splitted[1]);
						GasStation station = new GasStation(pos, price);
						stations.add(station);
					}
				}
				// add the destination as last gas station, as this considers the case that there is no gas station at the destination
				stations.add(new GasStation(distance, UNREACHABLE_GAS_GOST));
// DEBUG LINE   System.err.println(Arrays.toString(stations.toArray()));
				OUTPUT.append(solveMovingAdventure(distance, stations.toArray(new GasStation[0])));
				OUTPUT.append(System.lineSeparator());
				
				// no double blank line at the end
				if(i != testcaseCount - 1){
					OUTPUT.append(System.lineSeparator());
				}
			}
			System.out.print(OUTPUT);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
	
	public static String solveMovingAdventure(int distance, GasStation[] stations){
		// 2 means there's just Waterloo and Big City, no gas stations between.
		if(stations.length == 2) {
			// in this case, if distance is 0, it's possible.
			return distance == 0 ? "0" : "Impossible";
		}
		// meaning: dp[i][j] minimum cost for the state:
		//  - i: Gas station
		//  - j: content of gas tank
		// dp[2][100] means car is at 2. station with 100 liters left
		// dp[3][30] means car is at 3. station with 30 liters left
		int[][] dp = new int[stations.length][201];
		
		// as usual in dp, unreal cost
		for(int[] dpArr : dp) Arrays.fill(dpArr, UNREACHABLE_GAS_GOST);
		
		// starting point: car has 100 in tank
		dp[0][100] = 0;
		
		for(int i = 1; i < stations.length; i++){
			// distance to previous station
			int d = stations[i].pos - stations[i - 1].pos;
			
			// early detection: car cannot reach city if one segment is too long for the tank
			if(d > 200){
				return "Impossible";
			}
			
			// Populate first min costs (as dp[i][j] has only UNREACHABLE_GAS_COST, no Math.min required)
			// Try to not fill up tank (Transfer costs from previous city)
			// this is only possible, if the tank in the previous city was full enough to reach this city
			for(int j = d; j <= 200; j++){
				dp[i][j - d] = dp[i-1][j]; // just keep the previous costs, but reduce tank volume
			}
			
			// Tank any possible amount until tank is full
			// First fill up 1 liter, then add another, then another, etc.
			for(int j = 1; j <= 200; j++){
				// Math.min require because of block above which could have calculated a lower cost (in case the previous gas station was cheaper)
				dp[i][j] = Math.min(dp[i][j], dp[i][j - 1] + stations[i].price);
			}
		}
		
		// the last for-iteration was for the 'destination', which is not a gas station (filling up is prevent with exorbitant cost)
		// the only thing that last iteration will do, is transfer all possible cases, where the tank amount is enough to drive to that destination.
		
		// the optimal end state MUST be exactly 100 liters (101 liters cannot be optimal, in that case we could have filled up 1 liter less)
		int minCost = dp[stations.length - 1][100];
		
		if(minCost >= UNREACHABLE_GAS_GOST){
			return "Impossible";
		} else {
			return minCost + "";
		}
	}
}