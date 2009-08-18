package rainbow.vm.compiler;

import rainbow.ArcError;
import rainbow.types.ArcObject;
import rainbow.types.Pair.NotPair;
import rainbow.vm.VM;
import static rainbow.vm.compiler.Compiler.compile;
import rainbow.vm.interpreter.Else;
import rainbow.vm.interpreter.IfClause;
import rainbow.vm.interpreter.IfThen;
import rainbow.vm.interpreter.LastIfThen;

import java.util.Map;

public class IfBuilder {
  public static ArcObject build(VM vm, ArcObject body, Map[] lexicalBindings) {
    ArcObject original = body;
    IfClause clause = new IfClause();
    while(body.len() > 0) {
      switch ((int) body.len()) {
        case 0: break;
        case 1: clause.add(new Else()); body = body.cdr(); break;
        case 2: clause.add(new LastIfThen()); body = body.cdr().cdr(); break;
        default: clause.add(new IfThen()); body = body.cdr().cdr(); break;
      }
    }

    body = original;
    while (!body.isNil()) {
      try {
        body.mustBePairOrNil();
      } catch (NotPair notPair) {
        throw new ArcError("if: unexpected: " + body);
      }
      ArcObject expr = compile(vm, body.car(), lexicalBindings);
      clause.take(expr);
      body = body.cdr();
    }

    return clause;
  }
}
