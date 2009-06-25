package rainbow.vm.compiler;

import rainbow.types.ArcObject;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ContinuationSupport;
import rainbow.vm.interpreter.QuasiQuotation;

public class QuasiQuoteBuilder extends ContinuationSupport {
  public QuasiQuoteBuilder(Continuation caller) {
    super(caller);
  }

  protected void onReceive(ArcObject returned) {
    caller.receive(new QuasiQuotation(returned));
  }
}
