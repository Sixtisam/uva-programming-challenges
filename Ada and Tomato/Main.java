import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	public final static long M = (int) (Math.pow(10,9) + 7);
	
	public static final StringBuilder OUTPUT = new StringBuilder();
	
    public static void main(String[] args) {
//long now = System.nanoTime();
        try (Scanner scanner = new Scanner(System.in)) {
			scanner.useDelimiter("\\p{javaWhitespace}+").useRadix(10);
			int testcaseCount = scanner.nextInt();
			int rows = 0;
			int columns = 0;
			for(int i = 0; i < testcaseCount; i++){
				int tomatoes = scanner.nextInt();
				int a = scanner.nextInt();
				int b = scanner.nextInt();
				int x1 = scanner.nextInt();
				
			
				//System.err.println(Arrays.deepToString(map)); /**/
				OUTPUT.append(solve(tomatoes, a, b, x1) + "");

				// no double blank line at the end
				OUTPUT.append(System.lineSeparator());
			}
			System.out.print(OUTPUT);
		
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

	public static long solve(int noTom, int a, int b, long x1){		
		
		long[] tomatoes = new long[noTom];
		tomatoes[0] = x1;
		
		for(int i = 1; i < noTom; i++){
			tomatoes[i] = ((tomatoes[i-1] * a) + b) % M;
		}
		
		radixSort(tomatoes, 4, 30);
		
		long price = 0;
		for(int j = 0; j < tomatoes.length; j++){
			price += (j+1) * tomatoes[j];
		}
		return price % M;
	}
	
	public static void mai2n(String[] args) {
		long[] arr = new long[]{6,2,1,3,0,5};
		//radixSort(arr);
		System.err.println(Arrays.toString(arr));
	}
	
	// 27 bit length
	public static void radixSort(long[] input, int radix, int bitlength) {
		int RADIX = radix;
		long initialMask = 0b1;
		
		for(int i = 1; i < RADIX; i++){
			initialMask = (initialMask << 1) | 0b1;
		}
		
		//System.err.println("RADIX: " + Long.toBinaryString(initialMask));
		
		// initialize buckets
		@SuppressWarnings("unchecked")
		List<Long>[] buckets = new ArrayList[(int) Math.pow(2, RADIX)];
		for (int i = 0; i < buckets.length; i++) {
			buckets[i] = new ArrayList<Long>();
		}
		

		for(int round = 0; round * RADIX <= bitlength; round++){
			long mask = initialMask << (round * RADIX);
			//System.err.println("RADIX AFTER ROUND " + round + ": " + Long.toBinaryString(mask));
			// for each value
			for (long i : input) {
				// get the associated bucket
				int index = (int) (i & mask) >> (round * RADIX);
				// put into bucket
				buckets[index].add(i);
			}
			
			// iterate all buckets and push elements into array
			int j = 0;
			for (List<Long> bucket : buckets) {
				for (Long value : bucket) {
					input[j++] = value;
				}
				bucket.clear();
			}
		}
	}
}