import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

public class Main {
	
	public static final StringBuilder OUTPUT = new StringBuilder();
	
    public static void main(String[] args) {
//long now = System.nanoTime();
        try (Scanner scanner = new Scanner(System.in)) {
			scanner.useDelimiter("\\p{javaWhitespace}+").useRadix(10);
			int testcaseCount = scanner.nextInt();
			int rows = 0;
			int columns = 0;
			for(int i = 0; i < testcaseCount; i++){
				int[][] map = null;
				//String[] firstLine = reader.readLine().trim().split(" ");
				rows = scanner.nextInt();
				columns = scanner.nextInt();
				map = new int[rows][columns];
				for(int y = 0; y < rows; y++){
					//String[] line = reader.readLine().trim().split("\\s", columns);
					//if(line.length != columns) System.exit(0);
					for(int x = 0; x < columns; x++){
				//try { System.err.println(line[x]); } catch(ArrayIndexOutOfBoundsException e) { System.exit(0); return; }
						map[y][x] = scanner.nextInt();//Integer.parseInt(line[x]);
						
					}
				}
				
			
				//System.err.println(Arrays.deepToString(map)); /**/
				OUTPUT.append(solve(rows, columns, map) + "");

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

	public static int solve(int rows, int columns, int[][] map){
		
		int[][] dp = new int[1 + rows][columns];
		Arrays.fill(dp[0], 0);
		
		
		// traverse rows from bottom
		int dpIndex = 1;
		for(int y = rows - 1; y >= 0; y--){
			for(int x = 0; x < columns; x++){
				int below = dp[dpIndex-1][x];
				
				int leftBelow = (x - 1 >= 0) ? dp[dpIndex-1][x-1] : (Integer.MIN_VALUE / 2);
				
				int rightBelow = (x + 1 < columns) ? dp[dpIndex-1][x+1] : (Integer.MIN_VALUE / 2);
				
				dp[dpIndex][x] = map[y][x] + Math.max(below, Math.max(leftBelow, rightBelow));
			}
			dpIndex++;
		}
		

		
		//System.err.println(Arrays.deepToString(dp));
		int max = 0;
		for(int i = 0; i < columns; i++){
			if(max < dp[dp.length-1][i]){
				max = dp[dp.length-1][i];
			}
		}
		
		return max;
	}
}