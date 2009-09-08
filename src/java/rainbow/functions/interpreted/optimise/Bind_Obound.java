package rainbow.functions.interpreted.optimise;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;

import java.util.Map;

public class Bind_Obound extends InterpretedFunction {
  private final BoundSymbol optionalExpression;

  public Bind_Obound(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
    Pair opt = (Pair) parameterList.car();
    optionalExpression = (BoundSymbol) opt.cdr().cdr().car();
  }

  public void invokeN(VM vm, LexicalClosure lc) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    lc.add(optionalExpression.interpret(lc));
    vm.pushInvocation(lc, this.instructions);
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    lc.add(arg);
    vm.pushInvocation(lc, this.instructions);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    if (args instanceof Nil) {
      invokeN(vm, lc);
    } else {
      try {
        args.cdr().mustBeNil();
      } catch (NotNil notNil) {
        throw new ArcError("expected 0 or 1 args, got extra " + args.cdr() + " calling " + this);
      }
      invokeN(vm, lc, args.car());
    }
  }
}
