import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.math.BigInteger;
import java.util.Collections;
import java.util.ArrayList;

/**
 * https://www.spoj.com/FHNW2003/problems/ACODE/
 * 
 *
 * @date 20.03.2020
 * @author Samuel Keusch
 */
public class Main {
	public static final StringBuilder OUTPUT = new StringBuilder();
	
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			String line = reader.readLine();
			while(!line.equals("0")){
				solve(line);
				line = reader.readLine();
				if(!line.equals("0")){
					OUTPUT.append(System.lineSeparator());
				}
			}
			System.out.print(OUTPUT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	public static void solve(String code){
		// dp[2] describes number of decodings for the last i chars 
		// Example string: 1021
		// dp[1] = 1 (Substring 1)
		// dp[2] = 2 (Substring 21)
		// dp[3] = 0 (Substring 021)
		// dp[4] = 2 (Substring 1021)
		long[] dp = new long[code.length() + 1];
		// base
		dp[0] = 1;
		
		// In case 0 ist the last char, the last char alone is not a valid encoding
		if(code.charAt(code.length() - 1) == '0'){
			dp[1] = 0;
		} else {
			dp[1] = 1;
		}
		
		
		// I traverse the string from end to begin.
		
		for(int i = 2; i < dp.length; i++){
			int curr = code.length() - i;
			// next means to the right of curr
			int next = code.length() - i + 1;
			
			// if current char is 0, this is no valid single char, thus 0
			if(code.charAt(curr) == '0'){
				// a 0 does not increase the amount of combinations
				dp[i] = 0;
			// if current and next char form a valid encoded pair
			} else if(code.charAt(curr) == '1' || (code.charAt(curr) == '2' && code.charAt(next) <= '6')){
				// dp[i-2] for the case curr and next form a pair
				// dp[i-1] for the case curr is interpreted alone
				dp[i] = dp[i-2] + dp[i-1];
			} else {
				dp[i] = dp[i-1];
			}
		}
		OUTPUT.append(dp[code.length()]);
	}
}