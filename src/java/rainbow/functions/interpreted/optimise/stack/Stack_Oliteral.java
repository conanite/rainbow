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

public class Stack_Oliteral extends StackFunctionSupport {
  private ArcObject optExpr;
  private InterpretedFunction curried;

  public Stack_Oliteral(InterpretedFunction original) {
    super(original.parameterList(), original.lexicalBindings, convert(original.lexicalBindings, original.body));
    this.optExpr = parameterList.car().cdr().cdr().car();
    ArcObject optParam = parameterList.car().cdr().car();
    if (canInline((Symbol) optParam, optExpr)) {
      curried = (InterpretedFunction) this.curry((Symbol) optParam, optExpr, false);
    }
  }

  public Stack_Oliteral(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
    this.optExpr = parameterList.car().cdr().cdr().car();
  }

  public void invokef(VM vm) {
    if (curried != null) {
      curried.invokef(vm);
    } else {
      vm.pushInvocation(null, this.instructions, new ArcObject[] { optExpr });
    }
  }

  public void invokef(VM vm, ArcObject arg) {
    vm.pushInvocation(null, this.instructions, new ArcObject[] { arg });
  }

  public void invokeN(VM vm, LexicalClosure lc) {
    if (curried != null) {
      curried.invokeN(vm, lc);
    } else {
      vm.pushInvocation(lc, this.instructions, new ArcObject[] { optExpr });
    }
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg) {
    vm.pushInvocation(lc, this.instructions, new ArcObject[] { arg });
  }

  public void invoke(VM vm, Pair args) {
    if (args instanceof Nil) {
      invokef(vm);
    } else {
      checkArgsLength(1, args);
      invokef(vm, args.car());
    }
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    if (args instanceof Nil) {
      invokeN(vm, lc);
    } else {
      checkArgsLength(1, args);
      invokeN(vm, lc, args.car());
    }
  }
}
