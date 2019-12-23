import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Arrays;
import java.io.IOException;

/**
 * @author Samuel Keusch
 * Runtime 0.150
 * 24.10.2019
 */
public class Main {
	
	// true when top line should be displayed
	public static int[][] matrix = new int[][]{
		//        0  1  2  3  4  5  6  7  8  9
		new int[]{1, 0, 1, 1, 0, 1, 1, 1, 1, 1}, // top line
		new int[]{0, 0, 1, 1, 1, 1, 1, 0, 1, 1}, // middle line
		new int[]{1, 0, 1, 1, 0, 1, 1, 0, 1, 1}, // bottom line
		new int[]{1, 3, 3, 3, 1, 2, 2, 3, 1, 1},  // vertical upper
		new int[]{1, 3, 2, 3, 3, 3, 1, 3, 1, 3}  // vertical lower
		
	};

	public static StringBuilder BUFFER = new StringBuilder();

	public static void main(String[] args) {
		HORIZONTAL_SEGMENT = new char[10][][];
		VERTICAL_SEGMENT = new char[10][][];
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			int counter = 0;
			while(reader.ready()){
				String[] line = reader.readLine().trim().split(" ", 2);
				int s = Integer.parseInt(line[0].replace(" ", ""));
				if(s == 0) {
					break;
				}
				String number = line[1].replace(" ", "");
				int[] digits = new int[number.length()];
				for(int i = 0; i < digits.length; ++i){
					digits[i] = ((int) number.charAt(i)) - 48; // 48 is zero in ANSI
				}
				precalcVerticalSegment(s);
				precalcHorizontalSegment(s);
				printDigits(digits, s);
				BUFFER.append("\n");
			}
			System.out.print(BUFFER);
		} catch(Exception e){
			throw new RuntimeException(e);
		}
		
	}
	
		
	public static void printDigits(int[] digits, int s){
		// top line
		for(int i = 0; i < digits.length; ++i){
			BUFFER.append(HORIZONTAL_SEGMENT[s-1][matrix[0][digits[i]]]);
			if(i + 1 != digits.length) BUFFER.append(' ');
		}
		BUFFER.append("\n");
		
		// vertical upper
		for(int j = 0; j < s; ++j){
			for(int i = 0; i < digits.length; ++i){
				BUFFER.append(VERTICAL_SEGMENT[s-1][matrix[3][digits[i]]]);
				if(i + 1 != digits.length) BUFFER.append(' ');
			}
			BUFFER.append("\n");
		}
		
		// middle line
		for(int i = 0; i < digits.length; ++i){
			BUFFER.append(HORIZONTAL_SEGMENT[s-1][matrix[1][digits[i]]]);
			if(i + 1 != digits.length) BUFFER.append(' ');
		}
		BUFFER.append("\n");
		
		// vertical lower
		for(int j = 0; j < s; ++j){
			for(int i = 0; i < digits.length; ++i){
				BUFFER.append(VERTICAL_SEGMENT[s-1][matrix[4][digits[i]]]);
				if(i + 1 != digits.length) BUFFER.append(' ');
			}
			BUFFER.append("\n");
		}
		
				
		// bottom line
		for(int i = 0; i < digits.length; ++i){
			BUFFER.append(HORIZONTAL_SEGMENT[s-1][matrix[2][digits[i]]]);
			if(i + 1 != digits.length) BUFFER.append(' ');
		}
		BUFFER.append("\n");
	}
	
	public static char[][][] HORIZONTAL_SEGMENT;
	public static char[][][] VERTICAL_SEGMENT;
	
	public static void precalcVerticalSegment(int s){
		if(VERTICAL_SEGMENT[s-1] != null){
			return;
		}
		char[] line4 = new char[s + 2]; //          0 (empty line)
		char[] line1 = new char[s + 2]; // |   |    1
		char[] line2 = new char[s + 2]; // |        2
		char[] line3 = new char[s + 2]; //     |    3
		
		line1[0] = '|';
		line2[0] = '|';
		line3[0] = ' ';
		line4[0] = ' ';
		
		line1[s + 1] = '|';
		line2[s + 1] = ' ';
		line3[s + 1] = '|';
		line4[s + 1] = ' ';
		
		for(int i = 1; i < s + 1; i++){
			line1[i] = ' ';
			line2[i] = ' ';
			line3[i] = ' ';
			line4[i] = ' ';
		}
		
		
		VERTICAL_SEGMENT[s-1] = new char[][]{
			line4,
			line1,
			line2,
			line3
		};
	}
	
	public static void precalcHorizontalSegment(int s){
		if(HORIZONTAL_SEGMENT[s-1] != null){
			return;
		}
		char[] dashedLine = new char[s + 2];
		char[] emptyLine = new char[s + 2];
		
		dashedLine[0] = ' ';
		dashedLine[s + 1] = ' ';
		emptyLine[0] = ' ';
		emptyLine[s + 1] = ' ';
		
		for(int i = 1; i < s + 1; i++){
			dashedLine[i] = '-';
		    emptyLine[i] = ' ';
		}
		
		HORIZONTAL_SEGMENT[s-1] = new char[][]{
			emptyLine,
			dashedLine
		};
	}
}