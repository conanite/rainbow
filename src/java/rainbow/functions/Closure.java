package rainbow.functions;

import rainbow.LexicalClosure;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

import java.util.ArrayList;
import java.util.List;

public class Closure extends ArcObject {
  private static int closurecount = 0;
  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        System.out.println("closures: " + closurecount);
      }
    });
  }
  private InterpretedFunction expression;
  private LexicalClosure lc;

  public Closure(InterpretedFunction expression, LexicalClosure lc) {
    closurecount++;
    this.expression = expression;
    this.lc = lc;
  }

  public void invokef(VM vm) {
    expression.invokeN(vm, lc);
  }

  public void invokef(VM vm, ArcObject arg) {
    expression.invokeN(vm, lc, arg);
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    expression.invokeN(vm, lc, arg1, arg2);
  }

  public void invoke(VM vm, Pair args) {
    expression.invoke(vm, lc, args);
  }

  public ArcObject type() {
    return Builtin.TYPE;
  }

  public String toString() {
    return expression.toString();
  }

  public static void main(String[] args) {
    List closures = new ArrayList();
    long now = System.currentTimeMillis();
    for (int i = 0; i < 2000000; i++) {
      closures.add(new Closure(null, null));
      if (i % 1000000 == 0) {
        closures.clear();
      }
    }
    System.out.println("count: " + closures.size() + "; time: " + (System.currentTimeMillis() - now) + "ms");
  }
}
