package rainbow.vm;

import rainbow.LexicalClosure;
import rainbow.vm.interpreter.visitor.Visitor;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;

public abstract class Instruction extends ArcObject {
  private ArcObject owner;

//  private static Map<Class<? extends Instruction>, Long> invocationCounts = new HashMap();
//  private static final String spaces = "                                                                       ";
//
//  static {
//    Runtime.getRuntime().addShutdownHook(new Thread() {
//      public void run() {
//        Comparator<Map.Entry<Class<? extends Instruction>, Long>> c = new Comparator<Entry<Class<? extends Instruction>, Long>>() {
//          public int compare(Entry<Class<? extends Instruction>, Long> o1, Entry<Class<? extends Instruction>, Long> o2) {
//            return o1.getValue().compareTo(o2.getValue());
//          }
//        };
//        Set<Map.Entry<Class<? extends Instruction>, Long>> s = new TreeSet(c);
//        s.addAll(invocationCounts.entrySet());
//        for (Entry<Class<? extends Instruction>, Long> e : s) {
//          String k = e.getKey().getName();
//          int n = 100 - k.length();
//          if (n > 0) {
//            k = k + spaces.substring(0, n);
//          }
//          System.out.println(k + e.getValue());
//        }
//      }
//    });
//  }
//
//  public static void invoke(Instruction i) {
//    Long l = invocationCounts.get(i.getClass());
//    if (l == null) {
//      l = 0L;
//    }
//    l++;
//    invocationCounts.put(i.getClass(), l);
//  }

  public ArcObject type() {
    return Symbol.mkSym("instruction");
  }

  public String toString(LexicalClosure lc) {
    return toString();
  }

  protected static String symValue(Symbol s) {
    if (s.bound()) {
      return String.valueOf(s.value());
    } else {
      return "#unbound#";
    }
  }

  public abstract void operate(VM vm);

  public void belongsTo(ArcObject fn) {
    this.owner = fn;
  }

  public void visit(Visitor v) {
    v.accept(this);
  }

  public ArcObject owner() {
    return owner;
  }
}
