import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.text.DecimalFormat;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * 10137 - The Trip
 * 
 * @date 23.12.2019
 * @author Samuel Keusch
 */
public class Main {
	
	public static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("$#0.00");
	
    public static void main(String[] args) {
		NUMBER_FORMAT.setRoundingMode(RoundingMode.UNNECESSARY); // correct rounding mode is required
		
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			StringBuilder sb = new StringBuilder(); // use StringBuilder to collect ALL output (its faster than System.out)
            while (reader.ready()) {
                int numOfExpenses = Integer.parseInt(reader.readLine().trim());
				if(numOfExpenses == 0) {
					System.out.print(sb); // output everything
					return;
				};
				
				// read in all expenses
				BigDecimal[] expenses = new BigDecimal[numOfExpenses];
                for (int i = 0; i < numOfExpenses; i++) {
					expenses[i] = (new BigDecimal(reader.readLine().trim()));
                }
				
				sb.append(solve(expenses));
				sb.append(System.lineSeparator());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	
	public static final BigDecimal ONE_HUNDREDTH = new BigDecimal("0.01");
	public static final BigDecimal FIVE_THOUSANDTH = new BigDecimal("0.005");
	
	/**
	 * Solves "The Trip" according to problem description
     */
	public static String solve(BigDecimal[] expenses){
		// calc the average of all expenses
		BigDecimal sum = BigDecimal.ZERO;
		for(int i = 0; i < expenses.length; i++){
			sum = sum.add(expenses[i]);
		}

		// the average is the equalized amount of money each one has to have at the end
		BigDecimal average = sum.divide(BigDecimal.valueOf(expenses.length), 100, RoundingMode.FLOOR);
		BigDecimal uAverage = sum.divide(BigDecimal.valueOf(expenses.length), 2, RoundingMode.FLOOR);
		
		boolean roundedUp = false;
		BigDecimal moneyToExchange = BigDecimal.ZERO;
		for(int i = 0; i < expenses.length; i++){
			// we could also only consider expenses above average and sum them up
			if(average.compareTo(expenses[i]) == -1){
				BigDecimal avgDiff = expenses[i].subtract(average);
				if(!roundedUp && avgDiff.compareTo(ONE_HUNDREDTH) == -1){
					moneyToExchange = moneyToExchange.add(ONE_HUNDREDTH);
					roundedUp = true;
				} else if(false && avgDiff.compareTo(FIVE_THOUSANDTH) == 1) {
					moneyToExchange = moneyToExchange.add(avgDiff.setScale(2, RoundingMode.CEILING));
					roundedUp = true;
				} else{	
					moneyToExchange = moneyToExchange.add(avgDiff.setScale(2, RoundingMode.FLOOR));
				}
			}
		}
		
		/*
		return "Sum: " + sum + System.lineSeparator() + "AVG: " + average + System.lineSeparator() + 
		"U_AVG: " + uAverage + System.lineSeparator() +
		moneyToExchange.setScale(2, RoundingMode.FLOOR).toPlainString();/**/
		return NUMBER_FORMAT.format(moneyToExchange.setScale(2, RoundingMode.UNNECESSARY));
	}
}