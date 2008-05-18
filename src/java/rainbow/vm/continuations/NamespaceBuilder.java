package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.Interpreter;

public class NamespaceBuilder extends ContinuationSupport {
  private static final Symbol o = (Symbol) Symbol.make("o");
  private ArcObject parameters;
  private Pair args;

  private NamespaceBuilder(ArcThread thread, LexicalClosure lc, Continuation whatToDo, ArcObject parameters, Pair arguments) {
    super(thread, lc, whatToDo);
    this.parameters = parameters;
    this.args = arguments;
  }

  public static void build(ArcThread thread, LexicalClosure lc, Continuation whatToDo, ArcObject parameters, Pair arguments) {
    if (parameters.isNil()) {
      whatToDo.receive(parameters);
    } else {
      try {
        new NamespaceBuilder(thread, lc, whatToDo, parameters, arguments).start();
      } catch (Error e) {
        System.out.println("Error building namespace for params " + parameters + " and args " + arguments);
        e.printStackTrace(System.out);
        throw e;
      }
    }
  }

  public void start() {
    if (parameters.isNil()) {
      caller.receive(parameters);
      return;
    } else if (parameters instanceof Symbol) {
      lc.add(args);
      caller.receive(parameters);
      return;
    }
    ArcObject nextParameter = parameters.car();
    ArcObject nextArg = args.car();
    if (nextParameter instanceof Symbol) {
      lc.add(nextArg);
    } else if (optional(nextParameter)) {
      Pair optional = optionalParam(nextParameter);
      if (!args.isNil()) {
        lc.add(nextArg);
      } else {
        Interpreter.interpret(thread, lc, this, optional.cdr().car());
        return;
      }
    } else {
      Continuation toDo = new NestedNamespaceBuilder(this);
      shift();
      new NamespaceBuilder(thread, lc, toDo, Pair.cast(nextParameter, this), Pair.cast(nextArg, this)).start();
      return;
    }

    shift();
    start();
  }

  private void shift() {
    parameters = parameters.cdr();
    args = (Pair) args.cdr();
  }

  public static boolean optional(ArcObject nextParameter) {
    if (!(nextParameter instanceof Pair)) {
      return false;
    }

    Pair p = (Pair) nextParameter;
    return p.car() == o;
  }

  public void onReceive(ArcObject o) {
    args = new Pair(o, args);
    start();
  }

  private Pair optionalParam(ArcObject nextParameter) {
    return (Pair) nextParameter.cdr();
  }

  public Continuation cloneFor(ArcThread thread) {
    NamespaceBuilder e = (NamespaceBuilder) super.cloneFor(thread);
    e.parameters = this.parameters.copy();
    e.args = this.args.copy();
    return e;
  }
}
