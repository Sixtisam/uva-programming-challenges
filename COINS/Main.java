import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.math.BigInteger;
import java.util.Collections;
import java.util.ArrayList;

/**
 * COINS https://www.spoj.com/FHNW2003/problems/COINS/
 * 
 *
 * @date 20.03.2020
 * @author Samuel Keusch
 */
public class Main {
	public static final StringBuilder OUTPUT = new StringBuilder();
	
	public static long[] dp;
	public static final int PREPARE_LIMIT = 2_000;
	
    public static void main(String[] args) {
		prepareDp(PREPARE_LIMIT);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			String line = reader.readLine();
			while(line != null){
				OUTPUT.append(getSolution(Integer.parseInt(line.trim())));
				line = reader.readLine();
				if(line != null){
					OUTPUT.append(System.lineSeparator());
				}
			}
			System.out.print(OUTPUT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public static long getSolution(int myCoin){
		if(myCoin > PREPARE_LIMIT){
			return Math.max(myCoin, getSolution(myCoin / 2) + getSolution(myCoin / 3) + getSolution(myCoin / 4));
		} else {
			return dp[myCoin];
		}
	}
	
	public static void prepareDp(int maxCoin){
		// dp[i] tells the most dollars that can be traded for a coin i
		dp = new long[maxCoin + 1];
		dp[1] = 1;
		// every place is 0
		
		for(int c = 2; c <= maxCoin; c++){
			int c2 = c / 2;
			int c3 = c / 3;
			int c4 = c / 4;
			dp[c] = Math.max(c, dp[c2] + dp[c3] + dp[c4]);
		}
	}
}