package rainbow.vm.compiler;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.functions.interpreted.ComplexArgs;
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
    Pair expandedBody = PairExpander.expand(vm, body, lexicalBindings);
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

  public static int highestLexScopeReference(int highest, ArcObject parameterList, boolean optionable) {
    if (parameterList instanceof Nil) {
      return highest;
    }

    if (parameterList instanceof Pair) {
      if (optionable) {
        if (ComplexArgs.optional(parameterList)) {
          int expr = parameterList.cdr().cdr().car().highestLexicalScopeReference();
          return expr > highest ? expr : highest;
        } else {
          highest = highestLexScopeReference(highest, parameterList.car(), true);
          highest = highestLexScopeReference(highest, parameterList.cdr(), false);
          return highest;
        }
      } else {
        highest = highestLexScopeReference(highest, parameterList.car(), true);
        highest = highestLexScopeReference(highest, parameterList.cdr(), false);
        return highest;
      }
    } else {
      return highest;
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
