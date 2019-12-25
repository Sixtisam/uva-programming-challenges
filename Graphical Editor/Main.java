import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.LinkedList;
import java.util.Queue;
/**
 * 10267 - Graphical Editor
 * 
 * @date 23.12.2019
 * @author Samuel Keusch
 */
public class Main {
	
	public static final StringBuilder OUTPUT = new StringBuilder(); // use StringBuilder to collect ALL output (its faster than System.out)       
	/**
	 * Image is represented in bytes, this is the most efficient java datatype for this application.
	 * All colors (latin capital letters) are casted to their ASCII number
	 * 0 represents white.
	 * ROW MAJOR FORMAT
	 */
	public static byte[][] IMAGE;
	
    public static void main(String[] cmdArgs) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			while (reader.ready()) {
				String line = reader.readLine().trim();
				
				// extract command letter and pass all params as second param
				String[] args = null;
				if(line.length() > 2){
					args = line.substring(2).split(" ");
				}
				executeCommand(line.charAt(0), args);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	/**
	 * Accepts a command (single char) and an argument string to the command
	 */
	public static void executeCommand(char command, String[] args){
		switch(command){
			case 'I': 
				createImage(args);
				return;
			case 'L': 
				colorPixel(args);
				return;
			case 'C':
				clearImage();
				return;
			case 'V': 
				drawVerticalSegement(args);
				return;
			case 'H': 
				drawHorizontalSegment(args);
				return;
			case 'K': 
				drawRectangle(args);
				return;
			case 'F':
				fillRegion(args);
				return;
			case 'S':
				printImage(args);
				return;
			case 'X':
				terminateSession();
				return;
			default:
				// ignore unrecognized commands
		}
	}

	public static void drawVerticalSegement(String[] args){
		int col = parseCoord(args[0]);
		int yStart = parseCoord(args[1]);
		int yEnd = parseCoord(args[2]);
		byte color = fromColor(args[3].charAt(0));
		
		if(yStart > yEnd){
			int tmp = yStart;
			yStart = yEnd;
			yEnd = tmp;
		}
		
		for(int y = yStart; y <= yEnd; ++y){
			IMAGE[y][col] = color;
		}
	}
	
	public static void drawHorizontalSegment(String[] args){
		int xStart = parseCoord(args[0]);
		int xEnd = parseCoord(args[1]);
		int row = parseCoord(args[2]);
		byte color = fromColor(args[3].charAt(0));
		
		if(xStart > xEnd){
			int tmp = xStart;
			xStart = xEnd;
			xEnd = tmp;
		}
		
		
		for(int x = xStart; x <= xEnd; ++x){
			IMAGE[row][x] = color;
		}
	}
	
	public static int parseCoord(String coord){
		return Integer.parseInt(coord) - 1;
	}
	
	public static void drawRectangle(String[] args){
		int x1 = parseCoord(args[0]);
		int y1 = parseCoord(args[1]);
		int x2 = parseCoord(args[2]);
		int y2 = parseCoord(args[3]);
		
		if(x1 > x2){
			int tmp = x1;
			x1 = x2;
			x2 = tmp;
		}
		
		if(y1 > y2){
			int tmp = y1;
			y1 = y2;
			y2 = tmp;
		}
		
		byte color = fromColor(args[4].charAt(0));
		// TODO maybe check if y1 is really above y2
		for(int y = y1; y <= y2; ++y){
			for(int x = x1; x <= x2; ++x){
				IMAGE[y][x] = color;
			}
		}
	}
	
	
	/**
	 * Fills a region.
	 * A region is an area in the image where all pixels have the same color and the pixels are connected
	 */
	public static void fillRegion(String[] args){
		// parse arguments
		int x = parseCoord(args[0]);
		int y = parseCoord(args[1]);
		byte newColor = fromColor(args[2].charAt(0));
		
		// in case of the pixel is also the desired color, we skip any work
		// this is necessary to avoid a infinite loop in the next step
		if(IMAGE[y][x] == newColor){
			return;
		}
		
		// the old color (that will be replaced)
		byte oldColor = IMAGE[y][x];
		
		// holds all coordinates that needs to be
		Queue<int[]> remainingCoords = new LinkedList<>();
		
		// first step is to draw the first point, add that coord to the queue
		IMAGE[y][x] = newColor;
		remainingCoords.add(new int[]{x, y});
		
		// process pixels until there are no more pixels to discover
		while(!remainingCoords.isEmpty()){
			int[] coord = remainingCoords.poll();
			fillPoint(coord[0], coord[1], oldColor, newColor, remainingCoords);
		}
		
	}
	public static final int[] DX = new int[]{-1, 0, 1, 0};
	public static final int[] DY = new int[]{0, -1, 0, 1};
	
	/**
	 * @param pX - x-coord to examine
	 * @param pY - y-coord to examine
	 * @param oldColor - original color that
	 * @param newColor - color that needs to be drawn
	 * @param remainingCoords - queue to store any coords that need to be examined further
	 */
	public static void fillPoint(int pX, int pY, byte oldColor, byte newColor, Queue<int[]> remainingCoords){
		for(int i = 0; i < DX.length; ++i){
			int y = pY + DY[i];
			int x = pX + DX[i];
			
			if(y >= 0 && y < IMAGE.length &&
			   x >= 0 && x < IMAGE[0].length && 
			   IMAGE[y][x] == oldColor){
			   IMAGE[y][x] = newColor;
			   remainingCoords.add(new int[]{x, y});
		   }
		}
	}

	public static void colorPixel(String[] args){
		int x = parseCoord(args[0]);
		int y = parseCoord(args[1]);
		byte color = fromColor(args[2].charAt(0));
		
		IMAGE[y][x] = color;
	}

	public static void printImage(String[] args){
		OUTPUT.append(args[0]);
		OUTPUT.append(System.lineSeparator());
		
		for(int y = 0; y < IMAGE.length; ++y){
			// IMAGE[0] is guaranteed to be always set
			for(int x = 0; x < IMAGE[0].length; ++x){
				OUTPUT.append(toColor(IMAGE[y][x]));
			}
			OUTPUT.append(System.lineSeparator());
		}
	}
	
	public static byte fromColor(char color){
		return (byte) color;
	}
		
	public static void createImage(String[] args){
		int w = Integer.parseInt(args[0]);
		int h = Integer.parseInt(args[1]);
		IMAGE = new byte[h][w];
		clearImage();
	}
	
	public static void clearImage(){
		for(int y = 0; y < IMAGE.length; ++y){
			for(int x = 0; x < IMAGE[0].length; ++x){
				IMAGE[y][x] = (byte) 'O';
			}
		}
	}
	
	public static void terminateSession(){
		System.out.print(OUTPUT);
		System.exit(0);
	}
	
	public static char toColor(byte color){
		return (char) color;
	}
}