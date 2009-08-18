package rainbow.vm.continuations;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;

import java.util.ArrayList;
import java.util.List;

public class NamespaceBuilder {
  private static final Symbol o = Symbol.mkSym("o");

  public static void complex(VM vm, LexicalClosure lc, ArcObject parameters, Pair args) {
    while (!parameters.isNotPair()) {
      ArcObject nextParameter = parameters.car();
      ArcObject nextArg = args.car();

      if (nextParameter instanceof Symbol) {
        lc.add(nextArg);

      } else if (optional(nextParameter)) {
        Pair optional = optionalParam(nextParameter);
        if (!args.isNil()) {
          lc.add(nextArg);
        } else {
          lc.add(evalOptional(vm, lc, optional));
        }

      } else {
        try {
          nextArg.mustBePairOrNil();
        } catch (Pair.NotPair e) {
          throw new ArcError("Expected list argument for destructuring parameter " + nextParameter + ", got " + nextArg);
        }
        complex(vm, lc, nextParameter, (Pair) nextArg);
      }

      parameters = parameters.cdr();
      args = (Pair) args.cdr();
    }

    if (parameters instanceof Symbol) {
      lc.add(args);
    }
  }

  private static ArcObject evalOptional(VM vm, LexicalClosure lc, Pair optional) {
    List i = new ArrayList();
    optional.cdr().car().addInstructions(i);
    vm.pushFrame(lc, Pair.buildFrom(i));
    return vm.thread();
  }

  public static boolean optional(ArcObject nextParameter) {
    if (!(nextParameter instanceof Pair)) {
      return false;
    }

    Pair p = (Pair) nextParameter;
    return p.car() == o;
  }

  private static Pair optionalParam(ArcObject nextParameter) {
    return (Pair) nextParameter.cdr();
  }

  public static void simple(LexicalClosure lc, ArcObject parameterList, ArcObject args) {
    while (!parameterList.isNil()) {
      if (parameterList instanceof Symbol) {
        lc.add(args);
        return;
      } else {
        lc.add(args.car());
        args = args.cdr();
        parameterList = parameterList.cdr();
      }
    }
  }
}
