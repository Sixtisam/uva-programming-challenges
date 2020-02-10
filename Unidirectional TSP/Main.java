import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.io.IOException;
import java.math.BigInteger;

/**
 * 116 - Unidirectional TSP
 * My approach is to calculate the minimal path for all rows on a specific column.
 * Starting with the rightmost column, the table is gradually built up until the leftmost columm.
 * The tricky thing with choosing the correct solution (when multiple paths have the same weight) is solved intercepting
 * the case, when a path from A to Z via B has the same weight as path A to Z via C (which was discovered before).
 * In this case, the row numbers (in this case B and C) are compared and if B is lower than C, the path from A to Z will go now via B.
 * But in the worst case, all row numbers can be equal except the latest one, which therefore requires a while-loop instead just one IF.
 *
 * @date 10.02.2020
 * @author Samuel Keusch
 */
public class Main {
	
	public static final StringBuilder OUTPUT = new StringBuilder();
	
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (reader.ready()) {
				int height = parseNextNumber(reader);
				if(height == -1) break; // parseNextNumber returns -1 when end of stream reached
				int width = parseNextNumber(reader);
				
				// column major format 
				int[][] matrix = new int[width][height]; 
				for(int y = 0; y < height; y++){
					for(int x = 0; x < width; x++){
						matrix[x][y] = parseNextNumber(reader);
					}
				}
				solve(height, width, matrix);

            }
			System.out.print(OUTPUT);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
	
	// holds the latest read char from the IN stream
	public static char lastChar = 0;
	
	public static int parseNextNumber(BufferedReader reader) throws IOException {
		do {
			lastChar = (char) reader.read();
		} while(Character.isWhitespace(lastChar));
		
		boolean negative = false;
		if(lastChar == '-'){
			lastChar = (char) reader.read();
			negative = true;
		}
		
		int number = 0;
		while(Character.isDigit(lastChar)){
			number = (10 * number) + Character.digit(lastChar, 10); // 48=ASCII 0

			lastChar = (char) reader.read(); // if this is already not a digit, we can safely discard it since there is at least one whitespace between numbers
		}
		
		return negative ? -number : number;
	}
	
	public static void solve(int height, int width, int[][] matrix){
		
		// Contains for each cell the move that must be taken to the next column for a minimal path to the right end of the board
		int[][] dpPathTable = new int[width][height];
		// Contains for each cell the minimal weight needed to walk to the right end of the board
		int[][] dpCostTable = new int[width][height];
		
		// Last column weights equal to the cell weight
		for(int y = 0; y < height; y++){
			dpPathTable[width - 1][y] = -1; // no successor flag
			dpCostTable[width - 1][y] = matrix[width - 1][y];
		}
		
		// The weight of all other cells will be determined
		for(int x = 0; x < width-1; x++){
			for(int y = 0; y < height; y++){
				dpCostTable[x][y] = Integer.MAX_VALUE;
			}
		}
		
		
		// for each column (going from right to left, except the last column)
		for(int x = width - 2; x >= 0; x--){
			
			// for each row (from top to bottom)
			for(int y = 0; y < height; y++){
				
				// try the tree possible moves (diag up, right, diag down)
				for(int i = -1; i < 2; i++){
					// we have to use floorMod here, so we only get positive values
					int targetRow = Math.floorMod(y + i, height);
					
					// calculate cost of this path (from column x row y to the right)
					int cost = dpCostTable[x+1][y] + matrix[x][targetRow];
					
					// in case new path is cheaper we can replace the existing one
					if(cost < dpCostTable[x][targetRow]){
						dpCostTable[x][targetRow] = cost;
						dpPathTable[x][targetRow] = y;
					} else if(cost == dpCostTable[x][targetRow]){
						// when cost is same, we have to search for the "lexograpically" smallest path
						// for example "1 2" is smaller than "2 1"
						int newCurrRow = y;
						int newOldRow = dpPathTable[x][targetRow];
						int xIncr = 1; // column counter
						// traverse path until one row is not the same
						while(newCurrRow == newOldRow && newCurrRow != -1){
							newCurrRow = dpPathTable[xIncr][newCurrRow];
							newOldRow = dpPathTable[xIncr][newOldRow];
							xIncr++;
						}
						
						// only if the new row is smaller than the new, replace existing path
						// in case the condition is false because newCurrRow == newOldRow, we must do nothing because that would mean the path is exactly the same
						if(newCurrRow < newOldRow){
							dpCostTable[x][targetRow] = cost;
							dpPathTable[x][targetRow] = y;
						}
					}
				}
			}
		}
		
		/*
		System.err.println("PATH TABLE: ------------------------------------------");
		System.err.println(Arrays.deepToString(dpPathTable));
		System.err.println("COST TABLE: ------------------------------------------");
		System.err.println(Arrays.deepToString(dpCostTable)); /**/
		
		
		// Search the path with the smallest weight
		int minimalPathWeight = dpCostTable[0][0];
		int minimalPathEndRow = 0;
		for(int y = 1; y < height; y++){
			if(dpCostTable[0][y] < minimalPathWeight){
				minimalPathWeight = dpCostTable[0][y];
				minimalPathEndRow = y;
			}
		}
		
		// Construct the path
		OUTPUT.append(minimalPathEndRow + 1);
		int row = dpPathTable[0][minimalPathEndRow];
		// x++ is incremented and in same statement, row is assigned with the new row.
		for(int x = 1; x < width; row = dpPathTable[x++][row]){
			OUTPUT.append(" ");
			OUTPUT.append(row + 1);
		}
		
		OUTPUT.append(System.lineSeparator());
		OUTPUT.append(minimalPathWeight);
		OUTPUT.append(System.lineSeparator());
	}
}