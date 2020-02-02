import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/**
 * 10131 - Is Bigger Better?
 * 
 * @date 02.02.2020
 * @author Samuel Keusch
 */
public class Main {
	
	public static final StringBuilder OUTPUT = new StringBuilder();
	
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			
			ArrayList<Elephant> elephants = new ArrayList<>(100);
			int i = 1;
            while (reader.ready()) {
				String line = reader.readLine();
				String[] params = line.trim().split(" ");
				int weight = Integer.parseInt(params[0]);
				int iq = Integer.parseInt(params[1]);
				elephants.add(new Elephant(i++, weight, iq));
            }
			solve(elephants);
			System.out.print(OUTPUT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public static void solve(ArrayList<Elephant> elephants){
		Elephant[] weightAsc = elephants.toArray(new Elephant[elephants.size()]);
		Elephant[] iqDesc = new Elephant[elephants.size()];
		System.arraycopy(weightAsc, 0, iqDesc, 0, weightAsc.length); 
		
		Arrays.sort(weightAsc, (a,b) -> {
			return Integer.compare(a.weight, b.weight);
			/*if(r == 0){
				return Integer.compare(b.iq, a.iq);
			} else {
				return r;
			}*/
		});
		Arrays.sort(iqDesc, (a,b) -> {
			return Integer.compare(b.iq, a.iq);
			/*if(r == 0){
				return Integer.compare(a.weight, b.weight);
			} else {
				return r;
			}*/
		});
		
		Stack<Elephant> els = longestCommonSubsequence(weightAsc, iqDesc);
		
		OUTPUT.append(els.size());
		OUTPUT.append(System.lineSeparator());
		while(!els.isEmpty()){
			Elephant el = els.pop();
			OUTPUT.append(el.id + " (" + el.weight + "," + el.iq + ")");
			OUTPUT.append(System.lineSeparator());
		}
	}
	
	public static Stack<Elephant> longestCommonSubsequence(Elephant[] arr1, Elephant[] arr2){
		// dp contains not the edit distance, it contains the length of the LCS
		final int[][] dp = new int[arr1.length + 1][arr2.length + 1];
		final Direction[][] dpPath = new Direction[arr1.length + 1][arr2.length + 1];
		
		/*
		// only works because arr1.length == arr2.length
		assert arr1.length == arr2.length;
		for (int i = 1; i < arr2.length + 1; i++) {
			dp[0][i] = i;
			dp[i][0] = i;
			dpPath[0][i] = Direction.INSERT;
			dpPath[i][0] = Direction.DELETE;
			// 0:0 is dpPath null and dp 0
		}*/

		for (int i = 1; i < dp.length; i++) {
			for (int j = 1; j < dp[0].length; j++) {
				// if the elephant is the same in both arrays
				if(arr1[i-1].id == arr2[j-1].id){
					dp[i][j] = dp[i-1][j-1] + 1;
					dpPath[i][j] = Direction.MATCH;
				} else if(dp[i][j-1] > dp[i-1][j]){
					dp[i][j] = dp[i][j-1];
					dpPath[i][j] = Direction.INSERT;
				} else {
					dp[i][j] = dp[i-1][j];
					dpPath[i][j] = Direction.DELETE;
				}
				/*
				int matchCost = dp[i-1][j-1] + matchLCS(arr1[i-1], arr2[j-1]);
				int insertCost = dp[i][j-1] + 1;
				int deleteCost = dp[i-1][j] + 1;
				
				// take lowest cost for the current matrix field
				dp[i][j] = matchCost;
				dpPath[i][j] = Direction.MATCH;
				if(dp[i][j] > insertCost){
					dp[i][j] = insertCost;
					dpPath[i][j] = Direction.INSERT;
				}
				if(dp[i][j] > deleteCost){
					dp[i][j] = deleteCost;
					dpPath[i][j] = Direction.DELETE;
				}*/
			}
		}
		
		int x = arr1.length;
		int y = arr2.length;
		Stack<Elephant> stack = new Stack<>();
		while(dpPath[x][y] != null){
			// if x and y are matched, add to array
			switch(dpPath[x][y]){
				case MATCH: {
					if(!stack.isEmpty()){
						//Elephant top = stack.peek();
						//if(top.weight > arr1[x-1].weight && top.iq < arr1[x-1].iq){
							stack.push(arr1[x-1]);
						//}
					} else {
						stack.push(arr1[x-1]);
					}
					x--;
					y--;
					break;
				}
				case INSERT: {
					y--;
					break;
				}
				case DELETE: {
					x--;
					break;
				}
			}
		}
		
		System.err.println(Arrays.deepToString(dp));
		System.err.println();
		System.err.println("selected elephants: " + stack.size());
		System.err.println("LCS: " + dp[arr1.length][arr2.length]);
		return stack;
	}
	
	public static int matchLCS(Elephant val1, Elephant val2){
		if(val1.id == val2.id){
			return 0;
		} else {
			return 1000;
		}
	}
	
	public static enum Direction {
		INSERT,DELETE,MATCH;
	}
	
	public static class Elephant {
		public final int id;
		public final int weight;
		public final int iq;
		public Elephant(int id, int weight, int iq){
			this.id = id;
			this.weight = weight;
			this.iq = iq;
		}
		@Override
		public String toString(){
			return id + "(" + weight + "," + iq + ")";
		}
	}
}