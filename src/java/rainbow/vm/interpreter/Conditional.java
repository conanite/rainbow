package rainbow.vm.interpreter;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ConditionalContinuation;

public interface Conditional {
  void interpret(ArcThread thread, LexicalClosure lc, Continuation caller, Continuation conditional);
  void execute(ArcThread thread, LexicalClosure lc, Continuation caller);
  void continueFor(ConditionalContinuation conditionalContinuation, Continuation caller);

  void add(Conditional c);
  void take(ArcObject expression);
}
