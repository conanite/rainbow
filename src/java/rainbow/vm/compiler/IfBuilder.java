package rainbow.vm.compiler;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Pair.NotPair;
import rainbow.vm.VM;
import static rainbow.vm.compiler.Compiler.compile;
import rainbow.vm.interpreter.Else;
import rainbow.vm.interpreter.IfClause;
import rainbow.vm.interpreter.IfThen;

import java.util.Map;

public class IfBuilder {
  public static ArcObject build(VM vm, ArcObject body, Map[] lexicalBindings) {
    ArcObject original = body;
    IfClause clause = new IfClause();
    while(body.len() > 0) {
      switch ((int) body.len()) {
        case 0: break;
        case 1: clause.append(new Else()); body = body.cdr(); break;
        case 2:
          clause.append(new IfThen());
          body = body.cdr();
          ((Pair)body).setCdr(new Pair(ArcObject.NIL, ArcObject.NIL));
          body = body.cdr();
          break;
        default: clause.append(new IfThen()); body = body.cdr().cdr(); break;
      }
    }

    body = original;
    while (!(body instanceof Nil)) {
      try {
        body.mustBePairOrNil();
      } catch (NotPair notPair) {
        throw new ArcError("if: unexpected: " + body);
      }
      clause.take(compile(vm, body.car(), lexicalBindings).reduce());
      body = body.cdr();
    }

    return clause.reduce();
  }
}
