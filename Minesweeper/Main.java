import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Arrays;

public class Main {
	public static int BOMB = '*';
	
	public static int[] dx = new int[]{-1,  0,  1, 1, 1, 0, -1, -1};
	public static int[] dy = new int[]{-1, -1, -1, 0, 1, 1,  1,  0};


	public static void main(String[] args) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			int boardCounter = 1;
			while (reader.ready()) {
				String[] dimensions = reader.readLine().split(" ", 2);
				//System.out.println("read line: " + Arrays.toString(dimensions));
				int h = Integer.parseInt(dimensions[0]);
				int w = Integer.parseInt(dimensions[1]);
				
				// end of input
				if(w == 0 && h == 0){
					return;
				}
				// space between boards
				if(boardCounter != 1) {
					System.out.println("");
				}
				
				// create board
				char[][] board = new char[h][w];
				
				for(int y = 0; y < h; ++y){
					String line = reader.readLine();
					for(int x = 0; x < w; ++x){
						board[y][x] = line.charAt(x);
					}
				}		
				System.out.println("Field #" + (boardCounter++) + ":");
				computeAndPrintBoard(board, h, w);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void computeAndPrintBoard(char[][] board, int h, int w){
		for(int y = 0; y < h; ++y){
			for(int x = 0; x < w; ++x){
				if(board[y][x] != BOMB){
					int bombCounter = 0;
					for(int i = 0; i < dx.length; ++i){
						int fx = x + dx[i];
						int fy = y + dy[i];
						
						if(fx >= 0 && fx < w && fy >= 0 && fy < h && board[fy][fx] == BOMB){
							bombCounter++;
						}
					}
					board[y][x] = (char) (bombCounter + 48); // 48 is ansi zero character 
				}
				System.out.print(board[y][x] + "");
			}
			System.out.println("");
		}
	}
}