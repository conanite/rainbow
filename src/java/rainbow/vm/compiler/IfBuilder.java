package rainbow.vm.compiler;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair.NotPair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.interpreter.IfClause;
import rainbow.vm.interpreter.Else;
import rainbow.vm.interpreter.LastIfThen;
import rainbow.vm.interpreter.IfThen;
import static rainbow.vm.compiler.Compiler.compile;
import rainbow.vm.continuations.ContinuationSupport;

import java.util.Map;

public class IfBuilder extends ContinuationSupport {
  private IfClause clause = new IfClause();
  private ArcObject body;
  private Map[] lexicalBindings;

  public IfBuilder(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject body, Map[] lexicalBindings) {
    super(thread, lc, caller);
    this.lexicalBindings = lexicalBindings;
    this.body = body;
    while(body.len() > 0) {
      switch ((int) body.len()) {
        case 0: break;
        case 1: clause.add(new Else()); body = body.cdr(); break;
        case 2: clause.add(new LastIfThen()); body = body.cdr().cdr(); break;
        default: clause.add(new IfThen()); body = body.cdr().cdr(); break;
      }
    }
    start();
  }

  public void start() {
    if (body.isNil()) {
      caller.receive(clause);
    } else {
      try {
        body.mustBePairOrNil();
      } catch (NotPair notPair) {
        throw new ArcError("if: unexpected: " + body);
      }
      compile(thread, lc, this, body.car(), lexicalBindings);
    }
  }

  protected void onReceive(ArcObject returned) {
    clause.take(returned);
    body = body.cdr();
    start();
  }
}

