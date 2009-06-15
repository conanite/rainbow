package rainbow.vm.compiler;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair.NotPair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import static rainbow.vm.compiler.Compiler.compile;
import rainbow.vm.continuations.ContinuationSupport;
import rainbow.vm.interpreter.Assignment;

import java.util.Map;

public class AssignmentBuilder extends ContinuationSupport {
  private ArcObject body;
  private Assignment assignment = new Assignment();
  private Map[] lexicalBindings;

  public AssignmentBuilder(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject body, Map[] lexicalBindings) {
    super(thread, lc, caller);
    this.body = body;
    this.lexicalBindings = lexicalBindings;
    assignment.prepare((int) body.len());
    start();
  }

  public void start() {
    if (body.isNil()) {
      caller.receive(assignment);
    } else {
      try {
        body.mustBePairOrNil();
      } catch (NotPair notPair) {
        throw new ArcError("assign: unexpected: " + body);
      }
      compile(thread, lc, this, body.car(), lexicalBindings);
    }
  }

  protected void onReceive(ArcObject returned) {
    assignment.add(returned);
    body = body.cdr();
    start();
  }
}
