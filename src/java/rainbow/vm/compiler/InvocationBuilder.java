package rainbow.vm.compiler;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Pair.NotPair;
import rainbow.vm.VM;
import rainbow.vm.interpreter.Invocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvocationBuilder {
  public static ArcObject build(VM vm, ArcObject body, Map[] lexicalBindings) {
    ArcObject original = body;
    List list = new ArrayList();
    while (!(body instanceof Nil)) {
      try {
        body.mustBePairOrNil();
      } catch (NotPair notPair) {
        throw new ArcError("can't compile " + original + "; not a proper list");
      }
      list.add(Compiler.compile(vm, body.car(), lexicalBindings));
      body = body.cdr();
    }
    return new Invocation(Pair.buildFrom(list)).reduce();
  }
}
