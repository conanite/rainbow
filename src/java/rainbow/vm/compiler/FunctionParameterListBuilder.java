package rainbow.vm.compiler;

import rainbow.functions.interpreted.ComplexArgs;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.Nil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FunctionParameterListBuilder {
  public static final Symbol O = Symbol.mkSym("o");

  public static ArcObject build(VM vm, ArcObject parameters, Map[] lexicalBindings) {
    index(parameters, lexicalBindings[0], new int[]{0}, false);
    ArcObject complexParams = ArcObject.NIL;
    List result = new LinkedList();

    while (!parameters.isNotPair()) {
      ArcObject first = parameters.car();
      parameters = parameters.cdr();
      if (!(first instanceof Pair)) {
        result.add(first);
      } else {
        complexParams = ArcObject.T;
        Pair maybeOptional = (Pair) first;
        if (ComplexArgs.optional(maybeOptional)) {
          ArcObject optionalParamName = maybeOptional.cdr().car();
          ArcObject compiledOptionalExpression = Compiler.compile(vm, maybeOptional.cdr().cdr().car(), lexicalBindings);
          result.add(Pair.buildFrom(O, optionalParamName, compiledOptionalExpression));
        } else {
          result.add(first);
        }
      }
    }

    if (result.size() == 0) {
      return returnParams(complexParams, parameters);
    } else {
      return returnParams(complexParams, Pair.buildFrom(result, parameters));
    }
  }

  private static ArcObject returnParams(ArcObject complexParams, ArcObject params) {
    return new Pair(complexParams, params);
  }

  private static void index(ArcObject parameterList, Map map, int[] i, boolean optionable) {
    if (parameterList instanceof Nil) {
      return;
    }

    if (parameterList instanceof Pair) {
      if (optionable && ComplexArgs.optional(parameterList)) {
        index(parameterList.cdr().car(), map, i, true);
      } else {
        index(parameterList.car(), map, i, true);
        index(parameterList.cdr(), map, i, false);
      }
    } else {
      map.put(parameterList, i[0]);
      i[0]++;
    }
  }
}
