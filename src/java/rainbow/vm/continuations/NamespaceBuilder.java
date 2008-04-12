package rainbow.vm.continuations;

import rainbow.Bindings;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.Interpreter;

public class NamespaceBuilder extends ContinuationSupport {
  private ArcObject originalParameters;
  private Pair originalArgs;
  private ArcObject parameters;
  private Pair args;

  public NamespaceBuilder(ArcThread thread, Bindings namespace, Continuation whatToDo, ArcObject parameters, Pair arguments) {
    super(thread, namespace, whatToDo);
    this.parameters = parameters;
    this.args = arguments;
    this.originalParameters = parameters;
    this.originalArgs = arguments;
  }

  public void start() {
    if (parameters.isNil()) {
      whatToDo.eat(parameters);
      return;
    } else if (parameters instanceof Symbol) {
      namespace.addToLocalNamespace(((Symbol) parameters).name(), args);
      whatToDo.eat(parameters);
      return;
    }
    ArcObject nextParameter = parameters.car();
    ArcObject nextArg = args.car();
    if (nextParameter instanceof Symbol) {
      namespace.addToLocalNamespace(((Symbol) nextParameter).name(), nextArg);
    } else if (optional(nextParameter)) {
      Pair optional = optionalParam(nextParameter);
      String optionalName = ((Symbol) optional.car()).name();
      if (!args.isNil()) {
        namespace.addToLocalNamespace(optionalName, nextArg);
      } else {
        Interpreter.interpret(thread, namespace, this, optional.cdr().car());
        return;
      }
    } else {
      Continuation toDo = new NestedNamespaceBuilder(this);
      shift();
      new NamespaceBuilder(thread, namespace, toDo, ArcObject.cast(nextParameter, Pair.class), ArcObject.cast(nextArg, Pair.class)).start();
      return;
    }

    shift();
    start();
  }

  private void shift() {
    parameters = parameters.cdr();
    args = (Pair) args.cdr();
  }

  private boolean optional(ArcObject nextParameter) {
    if (!(nextParameter instanceof Pair)) {
      return false;
    }

    Pair p = (Pair) nextParameter;
    return p.car() instanceof Symbol && ((Symbol) p.car()).name().equals("o");
  }

  public void digest(ArcObject o) {
    args = new Pair(o, args);
    start();
  }

  private Pair optionalParam(ArcObject nextParameter) {
    return (Pair) nextParameter.cdr();
  }

  protected ArcObject getCurrentTarget() {
    return new Pair(originalParameters, originalArgs);
  }

  public Continuation cloneFor(ArcThread thread) {
    NamespaceBuilder e = (NamespaceBuilder) super.cloneFor(thread);
    e.parameters = this.parameters.copy();
    e.args = this.args.copy();
    return e;
  }
}
