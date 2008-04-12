package rainbow;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class CountLines {
  public static void main(String[] args) throws IOException {
    File src = new File("src/java/rainbow");
    int[] counts = {0,0,0};
    count(src, counts);

    File cc = new File("src/cc");
    count(cc, counts);

    File parser = new File("src/java/rainbow/parser");
    int[] minus = {0,0,0};
    count(parser, minus);

    counts[0] -= minus[0];
    counts[1] -= minus[1];
    counts[2] -= minus[2];

    String name = "Java";
    printSummary(counts, name);

    File arc = new File("src/java/arc/ac.scm");
    int[] arcCounts = {0,0,0};
    count(arc, arcCounts);

    printSummary(arcCounts, "Arc");
  }

  private static void printSummary(int[] counts, String name) {
    System.out.println(name + ":");
    System.out.println("files " + counts[0] + "; lines " + counts[1] + "; bytes " + counts[2]);
    System.out.println("" + (counts[1]/counts[0]) + " lines per file; " + (counts[2]/counts[1]) + " bytes per line; " + (counts[2]/counts[0]) + " bytes per file");
    System.out.println("=========");
  }

  private static void count(File src, int[] counts) throws IOException {
    if (src.isDirectory()) {
      File[] files = src.listFiles();
      for (File file : files) {
        count(file, counts);
      }
    } else if (!src.getName().endsWith(".gif")) {
      counts[0]++;
      update(counts, src);
    }
  }

  private static void update(int[] counts, File file) throws IOException {
    InputStream is = new FileInputStream(file);
    byte[] content = new byte[is.available()];
    is.read(content);
    String s = new String(content);
    counts[1] += s.split("\n").length;
    counts[2] += content.length;

  }
}
