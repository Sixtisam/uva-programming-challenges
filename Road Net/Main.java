import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.Arrays;
import java.math.BigInteger;
import java.util.Collections;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * https://www.spoj.com/FHNW2003/problems/ROADNET/
 * 
 *
 * @date 20.03.2020
 * @author Samuel Keusch
 */
public class Main {
	public static final StringBuilder OUTPUT = new StringBuilder();
	
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			int testcaseNr = Integer.parseInt(reader.readLine());
			for(int i = 1; i <= testcaseNr; i++){
				int noTowns = Integer.parseInt(reader.readLine());
				int[][] table = new int[noTowns + 1][noTowns + 1];
				for(int j = 1; j <= noTowns; j++){
					String[] splittedLine = reader.readLine().trim().split(" ", noTowns);
					for(int x = 0; x < noTowns; x++){
						table[j][x+1] = Integer.parseInt(splittedLine[x]);
					}
				}
				solve(table);
				if(i != testcaseNr){
					reader.readLine();
					OUTPUT.append(System.lineSeparator());
				}
			}
			System.out.print(OUTPUT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public static void solve(int[][] table){
		for(int i = 1; i < table.length; i++){
			for(int j = i; j < table[0].length; j++){
				int dist = table[i][j];
				// do not calculate 
				if(dist != 0){
					boolean isNeighbour = true;
					for(int x = 1; x < table[0].length; x++){
						if(i != x && j != x && dist == table[i][x] + table[j][x]){
							isNeighbour = false;
						}
					}
					
					if(isNeighbour){
						OUTPUT.append(i + " " + j);
						OUTPUT.append(System.lineSeparator());
					}
				}
			}
		}
	}
}