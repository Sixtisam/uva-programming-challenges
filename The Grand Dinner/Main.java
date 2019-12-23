import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.stream.Collectors;

/**
 * 10249 - The Grand Dinner
 * My approach is very simple: First I sort the teams according to their size.
 * Because I need to output it, I memoize the original team index in a separate array.
 * When sorted, I star with the biggest team and distribute the team members to the tables.
 * "Distributing" means, team count decreases by 1 and table capacity by 1.
 * Whenever a team cannot be placed on different tables, I quickly abort and output the "0" line.
 * The placement of each member is logged as a String, which itself is kept in an array at the correct 
 * team order.
 * When all teams could be placed, the member placements of each team are printed out in the correct order
 *
 * @date 15.12.2019
 * @author Samuel Keusch
 */
public class Main {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			// collect all output in a StringBuilder (its faster than System.out)
			StringBuilder sb = new StringBuilder();
            while (reader.ready()) {
				String[] numbers = reader.readLine().trim().split(" ", 2);
				
                int numOfTeams = Integer.parseInt(numbers[0]);
                int numOfTables = Integer.parseInt(numbers[1]);
				if(numOfTeams == 0 && numOfTables == 0) {
					System.out.print(sb); // output everything
					return;
				};
				
                int[] teams = parseIntLine(reader.readLine(), numOfTeams);
				int[] tables = parseIntLine(reader.readLine(), numOfTables);
				
				solveGrandDinner(sb, teams, tables);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	/**
	 * Parses n ints from the reader's next line
	 */
	public static int[] parseIntLine(String line, int n){
		// trim any leading/trailing spaces
		String[] lineValues = line.trim().split(" ", n);
		int[] values = new int[n];
		for (int i = 0; i < n; i++) {
			values[i] = Integer.parseInt(lineValues[i]);
		}
		return values;
	}
	
	/**
	 * The puzzle is solved by first sorting the teams according their size. This guarantees that
	 * no tables are used by small teams and bigger teams have at the end missing tables.
	 */
	public static void solveGrandDinner(StringBuilder sb, int[] teams, int[] tables){
		// sort array according to team size, this is the key point of my solution
		int[] memo = insertionSortMemoized(teams);
		
		// The member placement for each team is stored here
		StringBuilder[] allTeams = new StringBuilder[teams.length];
		
		// team index
		int i = teams.length - 1;
		while(i >= 0){
			int teamMembersLeft = teams[i];
			// log the assigned tables for each team member
			allTeams[memo[i]] = new StringBuilder(teamMembersLeft * 2);
			
			// assign each team member to a table.
			int tableIndex = 0;
			while(tableIndex < tables.length && teamMembersLeft > 0){
				if(tables[tableIndex] > 0){ // if table has free seats
					// table has free seats, place team member here
					teamMembersLeft--; // decrease team member count
					
					allTeams[memo[i]].append(tableIndex + 1); // log the placement of the team member
					allTeams[memo[i]].append(" "); // use the memoized index (the original one) to maintain team order
					tables[tableIndex]--; // decrease table capacity
				} else {
					// table is fully seated, skip it
				}
				tableIndex++;
			}
			// if there are not enough tables for all team members, it's not solvable
			if(teamMembersLeft > 0){
				sb.append("0");
				sb.append(System.lineSeparator());
				return; // quickly abort and dont do any uneccessary work
			}
			i--;
		}
		
		sb.append("1"); // solved mark
		for(int j = 0; j < allTeams.length; j++){
			sb.append(System.lineSeparator());
			sb.append(allTeams[j].substring(0, allTeams[j].length() - 1)); // do not keep the last space
		}
		sb.append(System.lineSeparator());
	}
	
	/**
	 * Sorts the values ascending while memorizing their original position in the returned array.
	 * InsertionSort is O(n^2) but because there are at max 70 teams, this is not so worse
	 */
	public static int[] insertionSortMemoized(int[] values){
		// memo contains as value the index of itself
		int[] memo = new int[values.length];
		for(int i = 0; i < values.length; i++){
			memo[i] = i;
		}
		
		// Insertion sort
		// the memo array is being moved synchronized, so in the end, the origin index of a value can be at the same
		// new index but in the memo array
		for(int first = 1; first < values.length; first++) {
			int x = values[first];
			int xm = memo[first];
			
			int j = first - 1;
			while(j >= 0 && values[j] > x) {
				values[j + 1] = values[j]; 
				memo[j + 1] = memo[j];
				j--;
			}
			values[j + 1] = x;
			memo[j + 1] = xm;
		}
		return memo;
	}
}