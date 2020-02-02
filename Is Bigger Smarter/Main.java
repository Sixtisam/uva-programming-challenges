import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Stack;

/**
 * 10131 - Is Bigger Better?
 * My first idea was doing something the described "maximum monotone subsequence". For this, I would first sort 
 * all elephants after their weight, ascending. This would be compared with the elephants sorted after their IQ, descending.
 * This would result in the edit distance between them. But I needed the length of the subsequence. For this reason, I research on the Internet,
 * and got the information, that I instead of counting up the "edit operations", I could just count the "same letters". The rest of the algorithm would be the same, 
 * which made completely sense. Unfortunately, the described "maximum monotone subsequence" did not work out as described. I think it's because the problem statement wants 
 * a strict ordering.
 * So I researched about how to do "longest increasing subsequence" and found this interesting description there:
 * https://books.google.ch/books?id=7XUSn0IKQEgC&lpg=PA289&ots=z9dCQWIP2j&dq=In%20fact%2C%20this%20is%20just%20a%20longest%20common%20subsequence%20problem%2C%20where%20the%20second%20string%20is%20the%20elements%20of%20S%20sorted%20in%20increasing%20order.&hl=de&pg=PA291#v=onepage&q=In%20fact,%20this%20is%20just%20a%20longest%20common%20subsequence%20problem,%20where%20the%20second%20string%20is%20the%20elements%20of%20S%20sorted%20in%20increasing%20order.&f=false
 * This was way clearer to understand. If the longest subsequence for the first 4 chars is for example 2, for the fifth char is could only be 3 or 2 (if the subsequence cannot be extended)
 * This new concept also eliminates the need to sort after IQ.
 * I now only sort after the weight and then run the longest increasing subsequence algorithm on the array.
 * Special is, that the condition which determines if a subsequence can be extended, not only depends on the IQ. It also checks if predecessor has not the same weight, which
 * would violate the problem statement.
 * 
 * After the algorithm I just need to search through the table to find the longest. Then I can reconstruct the longest sequence by following the predecessors.
 * 
 * @date 02.02.2020
 * @author Samuel Keusch
 */
public class Main {
	
	public static final StringBuilder OUTPUT = new StringBuilder();
	
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			
			LinkedList<Elephant> elephants = new LinkedList<>();
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
	
	public static void solve(LinkedList<Elephant> elephants){
		Elephant[] weightAsc = elephants.toArray(new Elephant[elephants.size()]);
		
		// sort after weight
		Arrays.sort(weightAsc, (a,b) -> {
			return Integer.compare(a.weight, b.weight);
		});
		
		// find longest increasing subsequence on the sorted elephants
		Elephant[] lis = longestIncreasingSubsequence(weightAsc);
		
		// output the results
		OUTPUT.append(lis.length);
		OUTPUT.append(System.lineSeparator());
		for(int i = 0; i < lis.length; i++){
			OUTPUT.append(lis[i].id);
			OUTPUT.append(System.lineSeparator());
		}
	}
	
	public static Elephant[] longestIncreasingSubsequence(Elephant[] arr1){
		// dp contains not the edit distance, it contains the length of the LCS

		// Index i contains the (currently known) max length for an increasing subsequences with only the first i+1 elephants
		final int[] dpLength = new int[arr1.length];
		final int[] dpPredecessor = new int[arr1.length];
		
		// for all possible "subsequences" of any length
		for(int i = 0; i < arr1.length; i++){
			dpLength[i] = 1;
			dpPredecessor[i] = -1;
			// for all existing subsequences
			for(int j = 0; j < i; j++){
				// check if we can append the current elephant
				// and if yes, if this leads to a longer subsequence
				if(arr1[j].iq > arr1[i].iq && arr1[j].weight != arr1[i].weight && dpLength[i] < dpLength[j] + 1){
					dpLength[i] = dpLength[j] + 1;
					dpPredecessor[i] = j;
				}
			}
		}
		
		// Get the index of the LIS
		int maxIndex = 0;
		for(int i = 1; i < arr1.length; i++){
			if(dpLength[maxIndex] < dpLength[i]){
				maxIndex = i;
			}
		}
		
		// reconstruct the LIS by following predecessors
		Elephant[] lis = new Elephant[dpLength[maxIndex]];
		int backCounter = lis.length;
		while(maxIndex != -1){
			lis[--backCounter] = arr1[maxIndex];
			maxIndex = dpPredecessor[maxIndex];
		}
	
		return lis;
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
	}
}