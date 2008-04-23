package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.vm.continuations.ArgumentEvaluator;
import rainbow.*;
import rainbow.types.ArcObject;
import rainbow.types.Tagged;
import rainbow.types.Pair;

public class FunctionDispatcher extends ContinuationSupport {
  private ArcObject args;
  private ArcObject functionName;

  public FunctionDispatcher(ArcThread thread, Bindings namespace, Continuation whatToDo, ArcObject args, ArcObject functionName) {
    super(thread, namespace, whatToDo);
    this.args = args;
    this.functionName = functionName;
//    System.out.println("dispatching " + functionName  + " with args " + args);
  }

  public void digest(ArcObject fn) {
    if (fn.isNil()) {
      throw new ArcError("Function " + functionName + " is " + fn);
//    } else if (Tagged.hasTag(fn, "mac")) {
//      Function macro = (Function) ((Tagged)fn).getRep();
//      System.out.println("invoking macro " + macro + " with args " + args);
//      macro.invoke(thread, namespace, new EvaluatorContinuation(thread, namespace, whatToDo), (Pair) args);
    } else if (fn instanceof SpecialForm) {
      SpecialForm special = (SpecialForm) fn;
      special.invoke(thread, namespace, whatToDo, (Pair) args);
    } else if (fn instanceof Function) {
      Function function = (Function) fn;
      new ArgumentEvaluator(thread, namespace, new FunctionInvoker(thread, namespace, whatToDo, function), (Pair) args).start();
    } else {
      throw new ArcError("Expected a function or macro: got " + fn + " with args " + args);
    }
  }

  protected ArcObject getCurrentTarget() {
    return functionName;
  }
}
