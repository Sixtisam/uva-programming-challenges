import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.math.BigInteger;
import java.util.Collections;
import java.util.ArrayList;

/**
 * 10154 - Weights and Measures
 * My solutions tries to start with the least strongest turtles.
 * At first, the weakest turtle will form a tower of size 1.
 * Then, for the next turtle, this new turtle is being placed on
 * any existing towers (including tower size 0). 
 * A turtle tower will replace an existing turtle tower whenever the new tower is lighter 
 * than the existing one.
 * e. g. in the end, the strongest turtle will be tried to placed on any existing towers.
 * Finally, the work left to do is to search the highest turtle tower which is possible.
 * 
 *
 * @date 07.02.2020
 * @author Samuel Keusch
 */
public class Main {
	
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			ArrayList<int[]> turtles = new ArrayList<>(1000);
			while (reader.ready()) {
				String[] line = reader.readLine().trim().split(" ", 2);
				int weight = Integer.parseInt(line[0].trim());
				int strength = Integer.parseInt(line[1].trim());
				turtles.add(new int[]{weight, strength, strength - weight});
			}
			
			// sort by turtle strength ascending
			Collections.sort(turtles, (a,b) -> {
				return Integer.compare(a[1], b[1]);
			});
			
			int[] dp = new int[turtles.size() + 1]; // extra place for index 0
			Arrays.fill(dp, Integer.MAX_VALUE); // fill array with max weight
			dp[0] = 0; // a turtle tower with height 0 is 0 heavy
			
			// starting with the least strongest turtle
			for(int i = 0; i < turtles.size(); i++){
				int[] turtle = turtles.get(i);
				// for each tower, start with highest tower first, so we dont duplicate the current turtle
				for(int j = i + 1; j >= 0; j--) { // because we are at the i-th turtle, the biggest tower could be at maximum i high.
					// add the turtle to all existing towers and see, if the newly formed tower is lighter than that (probably) existing tower.
					if(dp[j] <= turtle[2] && dp[j + 1] > dp[j] + turtle[0]){
						dp[j + 1] = dp[j] + turtle[0];
					}
				}
			}
			
			// search for the biggest possible tower
			int i = turtles.size();
			while(i >= 0 && dp[i] == Integer.MAX_VALUE){
				i--;
			}
			System.out.println(i);
			System.exit(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}