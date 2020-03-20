import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.HashMap;
import java.math.BigInteger;
import java.util.Collections;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * https://www.spoj.com/FHNW2003/problems/ACODE/
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
			for(int i = 0; i < testcaseNr; i++){
				int noAcc = Integer.parseInt(reader.readLine());
				String[] accounts = new String[noAcc];
				for(int j = 0; j < noAcc; j++){
					accounts[j] = reader.readLine().trim();
				}
				reader.readLine();
				solve(accounts);
				if(i + 1 != testcaseNr){
					OUTPUT.append(System.lineSeparator());
				}
			}
			System.out.print(OUTPUT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public static void solve(String[] accounts){
		HashMap<String, Integer> map = new HashMap<>();
		for(String acc : accounts){
			Integer count = map.computeIfAbsent(acc, k -> 0);
			map.put(acc, count + 1);
		}
		
		String str = map.entrySet().stream()
			.sorted((p1, p2) -> p1.getKey().compareTo(p2.getKey()))
			.map(e -> e.getKey() + " " + e.getValue())
			.collect(Collectors.joining(System.lineSeparator()));
		OUTPUT.append(str);
		OUTPUT.append(System.lineSeparator());

	}
}