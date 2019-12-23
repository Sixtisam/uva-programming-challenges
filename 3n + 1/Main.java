import java.util.HashMap;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) {

    final long startTime = System.nanoTime();
    try (Scanner scanner = new Scanner(System.in)) {
      do {
        int i = scanner.nextInt();
        int j = scanner.nextInt();
        if (i <= 0 || j >= 1_000_000) {
          throw new RuntimeException("Number too small or large");
        }
        long maxCycleLength = calcMaxCycleLength(i, j);
        System.out.println(i + " " + j + " " + maxCycleLength);
      } while (scanner.hasNextLine() && scanner.nextLine() != null && scanner.hasNextInt());
    }
    //System.out.println("Took " + ((System.nanoTime() - startTime) / 1_000_000));
    // System.out.println("Cache filled with: " + m_cachedValues.size() + " which
    // approx. uses "
    // + (m_cachedValues.size() * (64 + 32)) + "bits");
  }

  // we could use a LongToInt map to increase efficiency
  public final static HashMap<Long, Integer> m_cachedValues = new HashMap<>();

  public static long calcMaxCycleLength(int from, int to) {
    if (from > to) {
      return calcMaxCycleLength(to, from);
    }

    long max = 0;
    for (int i = from; i <= to; i++) {
      long cycleLength = calcCycleLength(i);
      if (max < cycleLength) {
        max = cycleLength;
      }
    }
    // System.out.println("Max for " + from + "-" + to + ":" + maxN);
    return max;
  }

  public static long calcCycleLength(int n0) {
    if (m_cachedValues.containsKey((long) n0)) {
      return m_cachedValues.get((long) n0);
    }

    // System.out.print("Calc for n0=" + n0 + "...:");
    long n = n0;
    int counter = 1;
    while (n != 1) {
      counter++;
      if ((n & 1) == 0) { // is n even?
        n = n >> 1; // divide 2
      } else {
        n = n * 3 + 1;
      }
      // We could take a shortcut, but it seems to have no relevant time effect
      // if (m_cachedValues.containsKey(n)) {
      // counter += m_cachedValues.get(n) - 1;
      // n = 1;
      // }
      assert n >= 1 : "this should not have happened n0=" + n0 + " n=" + n;
    }
    // System.out.println(counter);
    m_cachedValues.put((long) n0, counter);
    return counter;
  }
}