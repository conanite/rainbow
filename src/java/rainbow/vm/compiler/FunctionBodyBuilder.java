package rainbow.vm.compiler;

import rainbow.functions.interpreted.ComplexArgs;
import rainbow.functions.interpreted.SimpleArgs;
import rainbow.functions.interpreted.ZeroArgs;
import rainbow.functions.interpreted.optimise.*;
import rainbow.types.ArcObject;
import static rainbow.types.ArcObject.NIL;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.Nil;

import java.util.HashMap;
import java.util.Map;

public class FunctionBodyBuilder {

  public static ArcObject build(VM vm, Pair args, Map[] lexicalBindings) {
    if (lexicalBindings == null) {
      throw new IllegalArgumentException("can't have null lexical bindings!");
    }
    Map myParams = new HashMap();
    ArcObject parameters = args.car();
    ArcObject complexParams;
    ArcObject parameterList;
    if (parameters instanceof Nil) {
      complexParams = NIL;
      parameterList = NIL;
    } else {
      lexicalBindings = concat(myParams, lexicalBindings);
      ArcObject fpl = FunctionParameterListBuilder.build(vm, parameters, lexicalBindings);
      complexParams = fpl.car();
      parameterList = fpl.cdr();
    }

    Pair body = (Pair) args.cdr();
    Pair expandedBody = PairExpander.expand(vm, body, lexicalBindings);
    return buildFunctionBody(parameterList, myParams, expandedBody, complexParams);
  }

  private static Map[] concat(Map map, Map[] lexicalBindings) {
    Map[] result = new Map[lexicalBindings.length + 1];
    result[0] = map;
    System.arraycopy(lexicalBindings, 0, result, 1, lexicalBindings.length);
    return result;
  }

  private static ArcObject buildFunctionBody(ArcObject parameterList, Map lexicalBindings, Pair expandedBody, ArcObject complexParams) {
    if (parameterList instanceof Nil) {
      return new ZeroArgs(lexicalBindings, expandedBody);
    } else if (!(complexParams instanceof Nil)) {
      return new ComplexArgs(parameterList, lexicalBindings, expandedBody);
    } else if (parameterList instanceof Pair) {
      Pair params = (Pair) parameterList;
//      System.out.println("param list is " + params);
      if (params.isProper()) {
        switch ((int) params.len()) {
          case 1: return new Bind_A(parameterList, lexicalBindings, expandedBody);
          case 2: return new Bind_AA(parameterList, lexicalBindings, expandedBody);
          default: return new SimpleArgs(parameterList, lexicalBindings, expandedBody);
        }
      } else {
        switch (params.improperLen()) {
          case 1: return new Bind_AR(parameterList, lexicalBindings, expandedBody);
          case 2: return new Bind_AAR(parameterList, lexicalBindings, expandedBody);
          default: return new SimpleArgs(parameterList, lexicalBindings, expandedBody);
        }
      }
    } else {
      return new Bind_R(parameterList, lexicalBindings, expandedBody);
    }
  }
}
