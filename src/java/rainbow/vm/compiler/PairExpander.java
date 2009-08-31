package rainbow.vm.compiler;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.Nil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PairExpander {
  public static Pair expand(VM vm, ArcObject body, Map[] lexicalBindings) {
    List result = new LinkedList();

    while (!(body instanceof Nil) && body instanceof Pair) {
      ArcObject next = body.car();
      body = body.cdr();
      result.add(Compiler.compile(vm, next, lexicalBindings).reduce());
    }

    return (Pair)Pair.buildFrom(result, Compiler.compile(vm, body, lexicalBindings).reduce());
  }
}
