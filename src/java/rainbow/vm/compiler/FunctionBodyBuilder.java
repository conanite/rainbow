package rainbow.vm.compiler;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.functions.interpreted.ComplexArgs;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.functions.interpreted.SimpleArgs;
import rainbow.types.ArcObject;
import static rainbow.types.ArcObject.NIL;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.visitor.Visitor;

import java.lang.reflect.Constructor;
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
    Pair expandedBody = null;
    try {
      expandedBody = PairExpander.expand(vm, body, lexicalBindings);
    } catch (Exception e) {
      throw new ArcError("building function fn " + parameterList + " " + body + ": " + e, e);
    }
    return buildFunctionBody(parameterList, myParams, expandedBody, complexParams);
  }

  private static Map[] concat(Map map, Map[] lexicalBindings) {
    Map[] result = new Map[lexicalBindings.length + 1];
    result[0] = map;
    System.arraycopy(lexicalBindings, 0, result, 1, lexicalBindings.length);
    return result;
  }

  public static ArcObject buildFunctionBody(ArcObject parameterList, Map lexicalBindings, Pair expandedBody, ArcObject complexParams) {
    String cname = "rainbow.functions.interpreted.optimise.Bind" + sig(parameterList, false);
    Class c;
    try {
      c = Class.forName(cname);
    } catch (ClassNotFoundException e) {
      return defaultFunctionBody(parameterList, lexicalBindings, expandedBody, complexParams);
    }

    Constructor cons;
    try {
      cons = c.getConstructor(ArcObject.class, Map.class, Pair.class);
    } catch (NoSuchMethodException e) {
      throw new ArcError("Interpreted function constructor not found on " + c, e);
    }

    try {
      return (ArcObject) cons.newInstance(parameterList, lexicalBindings, expandedBody);
    } catch (Exception e) {
      throw new ArcError("Couldn't instantiate " + c + ": " + e, e);
    }
  }

  public static ArcObject buildStackFunctionBody(ArcObject parameterList, Map lexicalBindings, Pair expandedBody, ArcObject complexParams) {
    String sig = sig(parameterList, false);
    if ("".equals(sig)) {
      return buildFunctionBody(parameterList, lexicalBindings, expandedBody, complexParams);
    }

    String cname = "rainbow.functions.interpreted.optimise.stack.Stack" + sig;
    Class c;
    try {
      c = Class.forName(cname);
    } catch (ClassNotFoundException e) {
      throw new ArcError("no stack-based function implementation for " + parameterList + "; couldn't find " + cname);
    }

    Constructor cons;
    try {
      cons = c.getConstructor(ArcObject.class, Map.class, Pair.class);
    } catch (NoSuchMethodException e) {
      throw new ArcError("Interpreted function constructor not found on " + c, e);
    }

    try {
      return (ArcObject) cons.newInstance(parameterList, lexicalBindings, expandedBody);
    } catch (Exception e) {
      throw new ArcError("Couldn't instantiate " + c + ": " + e, e);
    }
  }

  public static ArcObject convertToStackParams(InterpretedFunction ifn) {
    String sig = sig(ifn.parameterList(), false);
    String cname = "rainbow.functions.interpreted.optimise.stack.Stack" + sig;
    Class c;
    try {
      c = Class.forName(cname);
    } catch (ClassNotFoundException e) {
//      System.out.println("no implementation " + cname + " for " + ifn);
      return ifn;
    }

    Constructor cons;
    try {
      cons = c.getConstructor(InterpretedFunction.class);
    } catch (NoSuchMethodException e) {
      throw new ArcError("Interpreted function constructor not found on " + c, e);
    }

    try {
//      System.out.println("creating new stack-fn with sig " + sig + " for ifn " + ifn);
      return (ArcObject) cons.newInstance(ifn);
    } catch (Exception e) {
      throw new ArcError("Couldn't instantiate " + c + ": " + e, e);
    }
  }

  private static ArcObject defaultFunctionBody(ArcObject parameterList, Map lexicalBindings, Pair expandedBody, ArcObject complexParams) {
    if (complexParams instanceof Nil) {
      return new SimpleArgs(parameterList, lexicalBindings, expandedBody);
    } else {
      return new ComplexArgs(parameterList, lexicalBindings, expandedBody);
    }
  }

  public static String sig(ArcObject parameterList, boolean optionable) {
    if (parameterList instanceof Nil) {
      return "";
    }

    if (parameterList instanceof Pair) {
      if (optionable) {
        if (ComplexArgs.optional(parameterList)) {
          ArcObject expr = parameterList.cdr().cdr().car();
          if (expr.literal()) {
            return "_Oliteral";
          } else if (expr instanceof BoundSymbol) {
            return "_Obound";
          } else {
            return "_Oother";
          }
        } else {
          ArcObject next = parameterList.car();
          return "_D" + sig(next, true) + sig(parameterList.cdr(), false) + "_d";
        }
      } else {
        return sig(parameterList.car(), true) + sig(parameterList.cdr(), false);
      }
    } else if (!optionable) {
      return "_R";
    } else {
      return "_A";
    }
  }

  public static void visit(Visitor v, ArcObject parameterList, boolean optionable) {
    if (parameterList instanceof Nil) {
      return;
    }

    if (parameterList instanceof Pair) {
      if (optionable && ComplexArgs.optional(parameterList)) {
        parameterList.cdr().cdr().car().visit(v);
      } else {
        visit(v, parameterList.car(), true);
        visit(v, parameterList.cdr(), false);
      }
    }
  }
}
