import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.io.IOException;
import java.util.BitSet;

/**
 * 10271 - Chopsticks
 *
 * Disproved assumptions:
 *   - Pairs are only possible to neighbouring sticks (disproved by 1,5,5,7,100,200 (Best solution 4^2 + 2^2)
 *   - Third stick does not matter (it has to be ensured that the third stick is longer than the other 2 sticks)
 *
 *
 *
 * @date 09.03.2020
 * @author Samuel Keusch
 */
public class Main {
	
	public static final StringBuilder OUTPUT = new StringBuilder();
	
	
    public static void main(String[] args) {
		//long now = System.nanoTime();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			int testcaseCount = Integer.parseInt(reader.readLine().trim());
			
			for(int i = 0; i < testcaseCount; i++){	
				int guestCount = nextInt(reader) + 8;
				int stickCount = nextInt(reader);
				int[] sticks = new int[stickCount];
				for(int j = 0; j < stickCount; j++){
					sticks[j] = nextInt(reader);
				}
				
				OUTPUT.append(solveSticks(guestCount, sticks));
				OUTPUT.append(System.lineSeparator());
			}
			System.out.print(OUTPUT);
			//System.err.println("Took " + ((System.nanoTime() - now) / 1_000_000) + "ms");

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
	
	public static int nextInt(BufferedReader reader) throws IOException {
		int next;
		
		do {
			next = reader.read();
		} while(!(next >= '0' && next <= '9'));
		
		int number = 0;
		while(next >= '0' && next <= '9'){
			number = (10 * number) + (next - '0');
			next = reader.read();
		}
		return number;
	}
	
	/**
	 * bottom-up variant
	 */
	public static int solveSticks(int guests, int[] chopsticks){
		System.err.println("Guests: " + guests);
		System.err.println("Chopsticks: " + Arrays.toString(chopsticks));
		
		int[][] dp = new int[0][0];
		
		
		return 0;
	}	
}