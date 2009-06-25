package rainbow.vm.compiler;

import rainbow.ArcError;
import rainbow.types.ArcObject;
import rainbow.types.Pair.NotPair;
import rainbow.vm.Continuation;
import static rainbow.vm.compiler.Compiler.compile;
import rainbow.vm.continuations.ContinuationSupport;
import rainbow.vm.interpreter.Assignment;

import java.util.Map;

public class AssignmentBuilder extends ContinuationSupport {
  private ArcObject body;
  private Assignment assignment = new Assignment();
  private Map[] lexicalBindings;

  public AssignmentBuilder(Continuation caller, ArcObject body, Map[] lexicalBindings) {
    super(caller);
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
      compile(lc, this, body.car(), lexicalBindings);
    }
  }

  protected void onReceive(ArcObject returned) {
    assignment.add(returned);
    body = body.cdr();
    start();
  }
}
