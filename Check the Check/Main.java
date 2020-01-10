import java.util.Arrays;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * 10196 - Check the Check
 * 
 *
 * 
 * @date 10.01.2020
 * @author Samuel Keusch
 */
public class Main {
	// collect all output in a StringBuilder (its faster than System.out)
	public static final StringBuilder OUTPUT = new StringBuilder();
	
    public static void main(String[] args) {
		//long millis = System.nanoTime();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			int gameCount = 1;
            while (reader.ready()) {
				char[][] chessBoard = new char[8][8]; // row major
				int[] whiteKing = null;
				int[] blackKing = null;
				boolean terminating = true;
				for(int i = 0; i < 8; ++i){
					String line = reader.readLine().trim();
					for(int j = 0; j < 8; ++j){
						chessBoard[i][j] = line.charAt(j);
						if(chessBoard[i][j] != '.'){
							terminating = false;
						} else if(chessBoard[i][j] == 'k'){
							whiteKing = new int[]{i, j};
						} else if(chessBoard[i][j] == 'K'){
							blackKing = new int[]{i, j};
						}
					}
				}
				
				OUTPUT.append("Game #" + gameCount);
				if(isInCheck(chessBoard, whiteKing)){
					OUTPUT.append(": white king is in check.");
				} else if(isInCheck(chessBoard, blackKing)){
					OUTPUT.append(": black king is in check.");
				} else {
					OUTPUT.append(": no king is in check.");
				}
				OUTPUT.append(System.lineSeparator());
				if(terminating){
					System.out.print(OUTPUT);
				}
				gameCount++;
            }
			//System.err.println("Took " + ((System.nanoTime() - millis) / 1_000_000) + "ms");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public static boolean isInCheck(char[][] chessBoard, int[] king){
		int i = 0;
		boolean inCheck = false;
		while(i < 8 && !inCheck){
			
			i++;
		}
		return inCheck;
	}
}