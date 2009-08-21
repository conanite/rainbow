package rainbow.functions;

import rainbow.ArcError;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;

public abstract class Builtin extends ArcObject {
  public static final Symbol TYPE = Symbol.mkSym("fn");
  protected final String name;

  protected Builtin(String name) {
    this.name = name;
    Symbol.mkSym(name).setValue(this);
  }

  protected ArcObject invoke(Pair args) {
    throw new ArcError("Builtin:invoke(args):provide implementation! " + name() + " args " + args);
  }

  public void invokef(VM vm) {
    invoke(vm, NIL);
  }

  public void invokef(VM vm, ArcObject arg) {
    invoke(vm, new Pair(arg, NIL));
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    invoke(vm, new Pair(arg1, new Pair(arg2, NIL)));
  }

  public void invoke(VM vm, Pair args) {
    vm.pushA(invoke(args));
  }

  public int compareTo(ArcObject right) {
    return 0;
  }

  public ArcObject type() {
    return TYPE;
  }

  public static void checkMaxArgCount(Pair args, Class functionClass, int maxArgs) {
    if (args.len() > maxArgs) {
      System.out.println(functionClass + " got args " + args + " was expecting at most " + maxArgs);
      throw new ArcError(functionClass.getSimpleName().toLowerCase() + " expects at most " + maxArgs + " arguments: given " + args);
    }
  }

  public static void checkMinArgCount(Pair args, Class functionClass, int min) {
    if (args.size() < min) {
      throw new ArcError(functionClass.getSimpleName().toLowerCase() + " expects at least " + min + " arguments: given " + args);
    }
  }

  public static void checkExactArgsCount(Pair args, int argCount, Class functionClass) {
    if (args.len() != argCount) {
      throw new ArcError(functionClass.getSimpleName().toLowerCase() + " expects " + argCount + " arguments: given " + args);
    }
  }

  public String toString() {
    return "<Builtin:" + (name == null || name.length() == 0 ? getClass().getSimpleName() : name) + ">";
  }

  public String name() {
    return "".equals(name) ? getClass().getSimpleName().toLowerCase() : name;
  }
}
