package rainbow.functions.interpreted.optimise.stack;

import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.functions.interpreted.StackFunctionSupport;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;

import java.util.Map;

public class Stack_A_Oliteral extends StackFunctionSupport {
  private ArcObject optExpr;
  private InterpretedFunction curried;

  public Stack_A_Oliteral(InterpretedFunction original) {
    super(original.parameterList(), original.lexicalBindings, convert(original.lexicalBindings, original.body));
    this.optExpr = parameterList.cdr().car().cdr().cdr().car();
    ArcObject optParam = parameterList.cdr().car().cdr().car();
    if (canInline((Symbol) optParam, optExpr)) {
      curried = (InterpretedFunction) this.curry((Symbol) optParam, optExpr, false);
      System.out.println("curried " + this + " to get " + curried);
    } else {
      System.out.println("not curryable: " + this);
    }
  }

  public Stack_A_Oliteral(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
    this.optExpr = parameterList.car().cdr().cdr().car();
  }

  public void invokef(VM vm, ArcObject arg) {
    if (curried != null) {
      curried.invokef(vm, arg);
    } else {
      vm.pushInvocation(null, this.instructions, new ArcObject[] { arg, optExpr });
    }
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    vm.pushInvocation(null, this.instructions, new ArcObject[] { arg1, arg2 });
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg) {
    if (curried != null) {
      curried.invokeN(vm, lc, arg);
    } else {
      vm.pushInvocation(lc, this.instructions, new ArcObject[] { arg, optExpr });
    }
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg1, ArcObject arg2) {
    vm.pushInvocation(lc, this.instructions, new ArcObject[] { arg1, arg2 });
  }

  public void invoke(VM vm, Pair args) {
    if (args instanceof Nil) {
      throwArgMismatchError(args);
    } else {
      ArcObject arg1 = args.car();
      if (args.cdr() instanceof Nil) {
        invokef(vm, arg1);
      } else {
        invokef(vm, arg1, args.cdr().car());
      }
    }
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    if (args instanceof Nil) {
      throwArgMismatchError(args);
    } else {
      ArcObject arg1 = args.car();
      if (args.cdr() instanceof Nil) {
        invokeN(vm, lc, arg1);
      } else {
        invokeN(vm, lc, arg1, args.cdr().car());
      }
    }
  }
}
