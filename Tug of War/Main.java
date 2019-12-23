import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Zuerst werden alle Werte eingelesen und in einem einzigen array die Gewichte abgelegt.
 * Dann wird für die Berechnung (als "Cache") ein Array mit allen möglichen Zahlen von 0 bis Gesamtgewicht aller Spieler erstellt.
 * In diesem Cache ist für jede dieser Zahlen (schlussendlich) hinterlegt, mit wievielen Spielern(Gewichten) diese Gewicht erreicht werden kann.
 * Also zum Beispiel könnte allPossibleWeights[520] beinhalten, dass 2 Spieler zusammen dieses Gewicht erreichen aber auch 3 Spieler.
 * Dies hatte ich zuerst mit einem Array gelöst (allPossibleWeights wäre in diesem Fall zweidimensional). In der 2. Dimension stand der Index dann jeweils
 * für die Teamgrösse, also hätte 'allPossibleWeights[520][3] == true' bedeutet, dass es eine Kombination von 3 Spielern gibt, die zusammen 520 wiegen.
 * Dies hatte eine Laufzeit von 2.3 Sekunden auf uva weshalb ich weiter nach Optimierungen gesucht haben.
 * Schlussendlich habe ich herausgefunden, dass ich für die 2. Dimension nie mehr als 50 Werte haben werde (gemäss Aufgabenstellung: Max. Teamgrösse). Und in einer 
 * long-Variable habe ich mit 64-bits mehr als genug Platz um diese Information abzulegen. Ich merke mit somit also die Kombination für alle möglichen (Teil-)Gewichte in
 * einem long-Array (allPossibleWeights). Zum Beispiel bedeutet '10' dass das Gewicht mit 1 Spieler erreicht wird, '100' mit 2 Spielern usw.
 * 
 * 10032 - Tug of War
 * Run time: 0.330
 * uva run number: 24028885
 * @date 10.10.2019
 * @author Samuel Keusch
 */
public class Main {

    public static void main(String[] args) {
		final long startTime = System.nanoTime();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			final int testcases = Integer.parseInt(reader.readLine());			
           for(int i = 0; i < testcases; i++){
				reader.readLine();
                final int nrOfweights = Integer.parseInt(reader.readLine());
				if(nrOfweights > 100) throw new RuntimeException("too many weights");
				
				final int[] weights = new int[nrOfweights];
				for(int j = 0; j < weights.length; j++){
					weights[j] = Integer.parseInt(reader.readLine());
					if(weights[j] > 450) throw new RuntimeException("weights to fat " + weights[j]);
				}

				System.out.println(tugOfWar(weights));
				if(i < testcases - 1){
					System.out.println();
				}
				//System.out.println("XX" + (System.nanoTime() - x) / 1_000_000);
		   }
        } catch (Exception e) {
            e.printStackTrace();
        }
		//System.out.println("Took " + (System.nanoTime() - startTime) / 1_000_000);
    }
	
	public static String tugOfWar(int[] weights){
		final int n = weights.length;
		
		// we need a long array (64-bit) which has enough bits to store combination information.
		// allPossibleWeights[232] = 0 means, sum cannot be reached by any combination
		// allPossibleWeights[5] = 10 means, sum can be reached by one number alone
		// allPossibleWeights[8] = 100 means, sum can be reached a combination of two numbers
		// allPossibleWeights[13] = 110 means, sum can be reached either by a combination of two numbers or by one number alone
		long[] allPossibleWeights = new long[450*100 + 1]; 
		
		final int sum = IntStream.of(weights).sum();

		// initial needed
		allPossibleWeights[0] = 1l;
		
		for(int i = 0; i < n; ++i) { // for each weight
			// add the current weight to all existing sums
			// If sum 10 can be reached with 3 weights (allPossibleWeights[10]=100),
			// then the sum 10+i can be reached with 4 numbers (allPossibleWeights[10+i]=1000)
			for(int j = sum; j >= 0; --j) {
				if(allPossibleWeights[j] != 0) {
					allPossibleWeights[j + weights[i]] |= allPossibleWeights[j] << 1;
					// example:
					// allPossibleWeights[15] = 100 which means, there is one or more combination of weights which are summed up 15
					// now current i=15, which means, sum 15 is also possible by just one number allPossibleWeights[15]10
					// we need to merge both facts which can be done by XOR  -> leds to allPossibleWeights[15]=110 and this means 15 can be
					// represented by one single weight or by a combination of 2 weights
				}
			}
		}
		//debugallPossibleWeightsTable(allPossibleWeights);
		
		// we search for a team which is half the size of total players
		// in case of odd size, the total weight for the smaller team is searched.
		final long combinationSizeSearch = 1l << (weights.length / 2);
		int halfSum = sum / 2; // this is the optimal balanced team weight (both teams have same weight)
		int offset = 0; // how many indexes we already walked away from the optimal balanced team weight
		int optimalTeam1Weight = -1; // stores min weight for team 1
		
		// fromt the point allPossibleWeights[halfSum] we walk more and more to the border of the array
		// until we find a sum, which can be represented by half of the total players
		do {
			// look left of halfSum for a possible solution
			if(allPossibleWeights.length > halfSum+offset && (allPossibleWeights[halfSum+offset] & combinationSizeSearch) > 0){
				optimalTeam1Weight = halfSum + offset;
			// look right of halfSum for a possible solution
			} else if((0 <= halfSum-offset) && (allPossibleWeights[halfSum-offset] & combinationSizeSearch) > 0){
				optimalTeam1Weight = halfSum - offset;
			} else {
				// increment offset to search more outwards
				offset++;
			}
		} while(optimalTeam1Weight == -1);
		// the first team weight we find is also the best, because we started at the optimal team weight. It cannot get better if we search more
		
		int secondTeamWeight = sum - optimalTeam1Weight;
		
		return Math.min(optimalTeam1Weight, secondTeamWeight) + " " + Math.max(optimalTeam1Weight, secondTeamWeight);
	}
	
	/*
	public static void debugallPossibleWeightsTable(long[] allPossibleWeights){
		for(int i = 0; i < allPossibleWeights.length; i++){
			System.out.println("[" + i + "]" + Long.toBinaryString(allPossibleWeights[i]));
		}
	}/**/
}