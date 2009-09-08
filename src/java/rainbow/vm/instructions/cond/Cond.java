package rainbow.vm.instructions.cond;

import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.Instruction;
import rainbow.vm.VM;

import java.util.*;
import java.util.Map.Entry;

public class Cond extends Instruction {
  private static Map<String, Long> invocationCounts = new HashMap();
  private static final String spaces = "                                                                                                                                                                         ";

  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        Comparator<Map.Entry<String, Long>> c = new Comparator<Entry<String, Long>>() {
          public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
            return o1.getValue().compareTo(o2.getValue());
          }
        };
        Set<Map.Entry<String, Long>> s = new TreeSet(c);
        s.addAll(invocationCounts.entrySet());
        for (Entry<String, Long> e : s) {
          String k = e.getKey();
          int n = 100 - k.length();
          if (n > 0) {
            k = k + spaces.substring(0, n);
          }
          System.out.println(k + e.getValue());
        }
      }
    });
  }

  public static void invoke(String sig) {
    Long l = invocationCounts.get(sig);
    if (l == null) {
      l = 0L;
    }
    l++;
    invocationCounts.put(sig, l);
  }

  private ArcObject thenExpr;
  private ArcObject elseExpr;
  private Pair thenInstructions;
  private Pair elseInstructions;
  private String sig;

  public Cond(ArcObject thenExpr, ArcObject elseExpr, String sig) {
    this.thenExpr = thenExpr;
    this.sig = sig;
    this.thenInstructions = instructionsFor(thenExpr);
    this.elseExpr = elseExpr;
    this.elseInstructions = instructionsFor(elseExpr);
  }

  public static Pair instructionsFor(ArcObject expr) {
    List list = new ArrayList();
    expr.addInstructions(list);
    return Pair.buildFrom(list);
  }

  public void operate(VM vm) {
//    invoke(sig);
    ArcObject arg = vm.popA();
    if (arg instanceof Nil) {
      vm.pushConditional(elseInstructions);
    } else {
      vm.pushConditional(thenInstructions);
    }
  }

  public String toString() {
    return "(cond then:" + thenExpr + ", else:" + elseExpr + ")";
  }
}
