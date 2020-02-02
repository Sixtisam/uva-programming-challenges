import java.util.Arrays;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * 10196 - Check the Check
 * My approach is the following: I read in the board and while doing this, I keep the position of the two kings
 * Then I first focus on the white king. Is he at any position that can be reached by an enemy figure?
 * And then the same for the black king.
 * 
 * Interesting facts:
 * - The 'Queen' is basically the mixed version of rook and bishop, so I could merge this functionality
 * - The 'King' does not need to be checked. According to the problem description 'there won't be a confguration where both kings are in check.' A king can only
 *   reach the other king that other king can also reach himself - and because we don't know which player is next, we could only say that both are in check.
 * - This problem is actually O(1) because for any board configuration, the worst case 'steps' to check is not dependend on any input value.
 *   For sure some configurations take a bit longer (white king in check vs black king in check for example (because white is checked first)) but this doesnt matter
 * 
 * @date 13.01.2020
 * @author Samuel Keusch
 */
public class Main {
	// collect all output in a StringBuilder (its faster than System.out)
	public static final StringBuilder OUTPUT = new StringBuilder();
	
	public static char[][] chessBoard;
	
    public static void main(String[] args) {
		//long millis = System.nanoTime();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			int gameCount = 1;
            while (reader.ready()) {
				if(gameCount > 1){
					reader.readLine(); //discard empty line
				}
				chessBoard = new char[8][8]; // row major
				int[] whiteKing = null;
				int[] blackKing = null;
				boolean terminating = true;
				for(int y = 0; y < 8; ++y){
					String line = reader.readLine().trim();
					for(int x = 0; x < 8; ++x){
						chessBoard[y][x] = line.charAt(x);
						if(chessBoard[y][x] == 'K'){
							whiteKing = new int[]{x, y};
							terminating = false;
						} else if(chessBoard[y][x] == 'k'){
							blackKing = new int[]{x, y};
							terminating = false;
						} else if(chessBoard[y][x] != '.'){
							terminating = false;
						} 
					}
				}
				
				if(terminating){
					System.out.print(OUTPUT);
					//System.err.println("Took " + ((System.nanoTime() - millis) / 1_000_000) + "ms");
					return;
				} else {
					OUTPUT.append("Game #" + gameCount);
					if(isInCheck(whiteKing)){
						OUTPUT.append(": white king is in check.");
					} else if(isInCheck(blackKing)){
						OUTPUT.append(": black king is in check.");
					} else {
						OUTPUT.append(": no king is in check.");
					}
					OUTPUT.append(System.lineSeparator());
				}
				gameCount++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	public static boolean isInCheck(int[] king){
		int i = 0;
		boolean inCheck = false;
		boolean white = chessBoard[king[1]][king[0]] == 'K';
		while(i < 8 && !inCheck){
			inCheck =    checkPawn(white, king[0], king[1]) // check pawn
					  || checkVerticallyHorizontally(white, king[0], king[1]) // Rook and 1/2 queen
					  || checkDiagonally(white, king[0], king[1]) // Rook and 2/2 queen
					  || checkKnight(white, king[0], king[1]) // check knight
					  || false; // king doesn't need to be checked (see problem description)
			i++;
		}
		return inCheck;
	}
	
	/**
	 * Check if any paw could capture the king at (x,y)
	 */
	public static boolean checkPawn(boolean white, final int x, final int y){
		int DX1 = -1;
		int DX2 = 1;
		int DY = -1;
		
		// mirror when black is playing
		if(!white){
			DY = -DY;
		}
		char k = white ? 'p' : 'P';
		return chessBoard(x + DX1, y + DY, k) || chessBoard(x + DX2, y + DY, k);
	}
	
	public static boolean chessBoard(int x, int y, char type){
		if(x >= 0 && x < chessBoard[0].length && y >= 0 && y < chessBoard.length){
			return chessBoard[y][x] == type;
		} else {
			return false;
		}
	}
	
	/**
	 * Check if any rook could capture the king at (x,y)
	 */ 
	public static boolean checkVerticallyHorizontally(boolean white, final int x, final int y){
		char rook = white ? 'r' : 'R';
		char queen = white ? 'q': 'Q';

		// walk into north (without jumping over other figures)
		int n = 1;
		while(y - n >= 0 && chessBoard[y - n][x] == '.') n++;
		if(y - n >= 0 && (chessBoard[y - n][x] == rook || chessBoard[y - n][x] == queen)) return true;
		
		// walk toward east (without jumping over figures)
		int e = 1;
		while(x + e < chessBoard[0].length && chessBoard[y][x + e] == '.') e++;
		if(x + e < chessBoard[0].length && (chessBoard[y][x + e] == rook || chessBoard[y][x + e] == queen)) return true;
		
		// walk towards south (without..)
		int s = 1;
		while(y + s < chessBoard.length && chessBoard[y + s][x] == '.') s++;
		if(y + s < chessBoard.length && (chessBoard[y + s][x] == rook || chessBoard[y + s][x] == queen)) return true;
		
		// walk towards west  (without ...)
		int w = 1;
		while(x - w >= 0 && chessBoard[y][x - w] == '.') w++;
		if(x - w >= 0 && (chessBoard[y][x - w] == rook || chessBoard[y][x - w] == queen)) return true;
		
		return false; // no rook found
	}
	
	
	/**
	 * Check if any bishop could capture the king at (x,y)
	 */ 
	public static boolean checkDiagonally(boolean white, final int x, final int y){
		char bishop = white ? 'b' : 'B';
		char queen = white ? 'q' : 'Q';
		
		{
			// walk into north-west until figure
			int nw = 1;
			while(y - nw >= 0 && x - nw >= 0 && chessBoard[y - nw][x - nw] == '.'){
				nw++;
			};
			if(y - nw >= 0 && x - nw >= 0 
				&& (chessBoard[y - nw][x - nw] == bishop || chessBoard[y - nw][x - nw] == queen)){
				return true;
			}
		}
		{
			// walk into north-east until figure
			int ne = 1;
			while(y - ne >= 0 && x + ne < chessBoard[0].length && chessBoard[y - ne][x + ne] == '.'){
				ne++;
			};
			if(y - ne >= 0 && x + ne < chessBoard[0].length 
				&& (chessBoard[y - ne][x + ne] == bishop || chessBoard[y - ne][x + ne] == queen)){
				return true;
			}
		}
		{			
			// walk into south-east until figure
			int se = 1;
			while(y + se < chessBoard.length && x + se < chessBoard[0].length && chessBoard[y + se][x + se] == '.'){
				se++;
			};
			if(y + se < chessBoard.length && x + se < chessBoard[0].length 
				&& (chessBoard[y + se][x + se] == bishop || chessBoard[y + se][x + se] == queen)) {
				return true;
			}
		}
		{
			// walk into south-west until figure
			int sw = 1;
			while(y + sw < chessBoard.length && x - sw >= 0 && chessBoard[y + sw][x - sw] == '.'){
				sw++;
			};
			if(y + sw < chessBoard.length && x - sw >= 0 
			   && (chessBoard[y + sw][x - sw] == bishop || chessBoard[y + sw][x - sw] == queen)) {
				return true;
			}
		}
		return false; // no rook or queen found
	}
	
	public static final int[] KNIGHT_DX = new int[]{-1, -2,  1, 2, 2,1, -1,-2};
	public static final int[] KNIGHT_DY = new int[]{-2, -1, -2, -1,1,2, 2,  1};
	
	/**
	 * Checks if the king can be captured by a hostile knight
	 */
	public static boolean checkKnight(boolean white, final int x, final int y){
		char knight = white ? 'n' : 'N';
		
		int i = 0;
		while(i < 8 && !chessBoard(x + KNIGHT_DX[i], y + KNIGHT_DY[i], knight)){
			i++;
		}
	
		return i < 8;
	}
}