package rainbow.functions;

import rainbow.ArcError;
import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public abstract class Builtin extends ArcObject implements Function {
  public static final Symbol TYPE = (Symbol) Symbol.make("fn");
  protected final String name;

  protected Builtin() {
    this("");
  }

  protected Builtin(String name) {
    this.name = name;
  }

  protected ArcObject invoke(Pair args) {
    throw new ArcError("Builtin:invoke(args):provide implementation!");
  }

  public void invoke(ArcThread thread, LexicalClosure lc, Continuation whatToDo, Pair args) {
    whatToDo.receive(invoke(args));
  }

  public int compareTo(ArcObject right) {
    return 0;
  }

  public ArcObject type() {
    return TYPE;
  }

  public static void checkMaxArgCount(Pair args, Class functionClass, int maxArgs) {
    if (args.size() > maxArgs) {
      throw new ArcError(functionClass.getSimpleName().toLowerCase() + " expects at most " + maxArgs + " arguments: given " + args);
    }
  }

  public static void checkMinArgCount(Pair args, Class functionClass, int min) {
    if (args.size() < min) {
      throw new ArcError(functionClass.getSimpleName().toLowerCase() + " expects at least " + min + " arguments: given " + args);
    }
  }

  public static void checkExactArgsCount(Pair args, int argCount, Class<? extends Lists.Cons> functionClass) {
    if (args.size() != argCount) {
      throw new ArcError(functionClass.getSimpleName().toLowerCase() + " expects " + argCount + " arguments: given " + args);
    }
  }

  public String source() {
    return "<functions>";
  }

  public String toString() {
    return "<Builtin:" + getClass().getSimpleName() + ">";
  }

  public String code() {
    return toString();
  }

  public String name() {
    return name;
  }
}
