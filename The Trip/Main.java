import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.text.DecimalFormat;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * 10137 - The Trip
 * My first idea was that I just can calculate the differences of all expenses above the average and sum them up.
 * The first problem I encountered was that people cannot give "half a penny" away. Naturally, I need to always round down.
 * When finished, the program did not output the correct values. They mostly had a difference of 0.01, so my guess was
 * it has to do something with floating point, or even that my assumption about "rounding down" is not correct.
 * Initially, I picked calculating the difference of all expenses above the average, but this was more or less "random".
 * I even commented that in the code, that I could also sum up the difference of all expenes below average.
 * So after I spent some hours trying to find the missing piece, I decided to sum up the difference of all expenses below average.
 * Surprisingly, this did not result in the same value. When inspecting those both values I got for each testcase, I noticed
 * that sometimes the sum for above average, sometimes the sum for below average were the correct results (according to an uDebug testcase)
 * After analyzing all results, I could quickly conclude that the higher of both values has to be the result.
 * But uva was not happy and I continued to search for the problem. I had strong suspicion that double is the reason and therefore, I switched
 * to BigDecimal. This was finally the reason why it didn't work on uva but on udebug.
 * 
 * @date 23.12.2019
 * @author Samuel Keusch
 */
public class Main {
	
	public static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("$#0.00");
	
    public static void main(String[] args) {
		NUMBER_FORMAT.setRoundingMode(RoundingMode.FLOOR); // correct rounding mode is required

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			StringBuilder sb = new StringBuilder(); // use StringBuilder to collect ALL output (its faster than System.out)
            while (reader.ready()) {
                int numOfExpenses = Integer.parseInt(reader.readLine().trim());
				if(numOfExpenses == 0) {
					System.out.print(sb); // output everything
					return;
				};
				
				// read in all expenses
				double[] expenses = new double[numOfExpenses];
                for (int i = 0; i < numOfExpenses; i++) {
					expenses[i] = Double.parseDouble(reader.readLine().trim());
                }
				
				sb.append(solve(expenses));
				sb.append(System.lineSeparator());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	/**
	 * Solves "The Trip" according to problem description
     */
	public static String solve(double[] expenses){
		// calc the average of all expenses
		double sum = 0.0;
		for(int i = 0; i < expenses.length; i++){
			sum += expenses[i];
		}

		// the average is the optimal equalized amount of money each one has to have at the end
		double average = sum / expenses.length;
	
		
		BigDecimal moneyToReceive = BigDecimal.ZERO; // holds the money those above average need to receive to reach average
		BigDecimal moneyToGive = BigDecimal.ZERO; // holds the money those below average need to give away to reach average
		for(int i = 0; i < expenses.length; i++){
			// calculate difference to average (can be negative!)
			BigDecimal difference = BigDecimal.valueOf(expenses[i])
										.subtract(BigDecimal.valueOf(average))
										.setScale(2, RoundingMode.DOWN); // truncate to 2 digits after comma (rounding down to negative infinity)

			// condition to decide wheter expense[i] is below or above average
			if(difference.compareTo(BigDecimal.ZERO) == 1){
				moneyToReceive = moneyToReceive.add(difference);
			} else {
				moneyToGive = moneyToGive.add(difference);
			}
		}
		

		// absolute value because its negative
		moneyToGive = moneyToGive.abs();
		/*
		System.out.println("----");
		System.out.println("SUM: " + sum);
		System.out.println("AVG: " + average);
		System.out.println("RECEIVE: " + moneyToReceive);
		System.out.println("GIVE: " + moneyToGive);/**/
		
		// the final result is whichever value is higher.
		// the lower value would not be the optimal solution: not all people would had spent the average (by within one cent)
		return NUMBER_FORMAT.format(moneyToGive.max(moneyToReceive));
	}
}