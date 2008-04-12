package rainbow;

import junit.framework.TestCase;

import rainbow.types.ArcObject;
import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.vm.continuations.TopLevelContinuation;

public abstract class ArcTestSupport extends TestCase {
  protected ArcObject vmEval(ArcObject expressionUnderTest) {
    final ArcThread thread = new ArcThread();
    TopLevelContinuation toDo = new TopLevelContinuation(thread);
    Interpreter.interpret(thread, new TopBindings(), toDo, expressionUnderTest);
    thread.run();
    return thread.finalValue();
  }
}
