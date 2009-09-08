package rainbow.vm.compiler;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.functions.interpreted.ComplexArgs;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.StackSymbol;

import java.util.ArrayList;
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

  public static ArcObject isComplex(ArcObject parameters) {
    while (!parameters.isNotPair()) {
      if (parameters.car() instanceof Pair) {
        return ArcObject.T;
      }
      parameters = parameters.cdr();
    }
    return ArcObject.NIL;
  }

  private static ArcObject returnParams(ArcObject complexParams, ArcObject params) {
    return new Pair(complexParams, params);
  }

  public static void index(ArcObject parameterList, Map map, int[] i, boolean optionable) {
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

  public static ArcObject curry(ArcObject params, StackSymbol param, ArcObject arg, int paramIndex) {
    ArcObject last = ArcObject.NIL;
    List list = new ArrayList();
    while (!params.isNotPair()) {
      ArcObject curriedParam = curryParam(param, arg, paramIndex, params.car());
      if (curriedParam != null) {
        list.add(curriedParam);
      }
      params = params.cdr();
    }
    if (params instanceof Symbol) {
      ArcObject rest = curryParam(param, arg, paramIndex, params);
      if (rest != null) {
        last = rest;
      }
    }
    try {
      return Pair.buildFrom(list, last);
    } catch (Exception e) {
      throw new ArcError("couldn't curry params " + params + ", got list " + list + " and last " + last);
    }
  }

  public static ArcObject curry(ArcObject params, BoundSymbol param, ArcObject arg, int paramIndex) {
    ArcObject last = ArcObject.NIL;
    List list = new ArrayList();
    while (!params.isNotPair()) {
      ArcObject curriedParam = curryParam(param, arg, paramIndex, params.car());
      if (curriedParam != null) {
        list.add(curriedParam);
      }
      params = params.cdr();
    }
    if (params instanceof Symbol) {
      ArcObject rest = curryParam(param, arg, paramIndex, params);
      if (rest != null) {
        last = rest;
      }
    }
    try {
      return Pair.buildFrom(list, last);
    } catch (Exception e) {
      throw new ArcError("couldn't curry params " + params + ", got list " + list + " and last " + last);
    }
  }

  private static ArcObject curryParam(StackSymbol param, ArcObject arg, int paramIndex, ArcObject c) {
    ArcObject curriedParam = null;
    if (c instanceof Symbol && !c.isSame(param.name)) {
      curriedParam = c;
    } else if (ComplexArgs.optional(c) && !c.cdr().car().isSame(param.name)) {
      List opt = new ArrayList(3);
      opt.add(O);
      opt.add(c.cdr().car());
      opt.add(c.cdr().cdr().car().inline(param, arg, paramIndex));
      curriedParam = Pair.buildFrom(opt);
    } else if (!ComplexArgs.optional(c) && c instanceof Pair) {
      curriedParam = c;
    }
    return curriedParam;
  }

  private static ArcObject curryParam(BoundSymbol param, ArcObject arg, int paramIndex, ArcObject c) {
    ArcObject curriedParam = null;
    if (c instanceof Symbol && !c.isSame(param.name)) {
      curriedParam = c;
    } else if (ComplexArgs.optional(c) && !c.cdr().car().isSame(param.name)) {
      List opt = new ArrayList(3);
      opt.add(O);
      opt.add(c.cdr().car());
      opt.add(c.cdr().cdr().car().inline(param, arg, false, 0, paramIndex));
      curriedParam = Pair.buildFrom(opt);
    } else if (!ComplexArgs.optional(c) && c instanceof Pair) {
      curriedParam = c;
    }
    return curriedParam;
  }
}
