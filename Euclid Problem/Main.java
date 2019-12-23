import java.util.HashMap;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.BufferedReader;	

/**
 * Zuers werden die Daten alle eingelesen und in einer Instanz von Pair abgelegt.
 * Mit dem erweiterten euklidischen Algorithmus (Methode ggT) wird der ggT berechnet.
 * Die Lösung für das euklidische Problem ist dann dieser ggT wenn man ihn mit einer Linearkombination von A und B (die zwei Eingabewerte)
 * darstellt. Mit der Klass Pair wird der ggT so berechnet, dass jeweils die Linearkombination beibehalten wird, also nicht verloren geht.
 *
 * 10104 - Euclid Problem
 * Runtime: 0.620
 * uva run number: 24028331
 * @date 10.10.2019
 * @author Samuel Keusch
 */
public class Main {

  public static class Pair {
    public int x;
    public int A;
    public int y;
    public int B;
    public int cachedValue;

    public void cacheValue() {
      cachedValue = x * A + y * B;
    }
  }

  public static void main(final String[] array) {
	try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
		final Pair pair = new Pair();
		final Pair pair2 = new Pair();
		
		while (reader.ready()) {
	      final String line = reader.readLine();
		  final String[] splitted = line.split(" ");
		  final int nextInt = Integer.parseInt(splitted[0]);
		  final int nextInt2 = Integer.parseInt(splitted[1]);
		  pair.x = 1;
		  pair.A = nextInt;
		  pair.y = 0;
		  pair.B = nextInt2;
		  pair2.x = 0;
		  pair2.A = nextInt;
		  pair2.y = 1;
		  pair2.B = nextInt2;
		  pair.cacheValue();
		  pair2.cacheValue();

		  final Pair result = ggT(pair, pair2);
		  
		  if(result.A == pair.B && result.B == pair.A && result.y < result.x){
			System.out.println(result.y + " " + result.x + " " + result.cachedValue);
		  } else {
			System.out.println(result.x + " " + result.y + " " + result.cachedValue);
		  }
		}
	} catch(Exception e){
		e.printStackTrace();
	}
  }

  public static Pair ggT(Pair a, Pair b) {
    if (b.cachedValue > a.cachedValue) {
      return ggT(b, a);
    }

    while (b.cachedValue != 0) {
      final int n = a.cachedValue / b.cachedValue;
      a.x -= n * b.x;
      a.y -= n * b.y;
      a.cachedValue -= n * b.cachedValue;

      final Pair tmp = a;
      a = b;
      b = tmp;
    }
    return a;
  }
}