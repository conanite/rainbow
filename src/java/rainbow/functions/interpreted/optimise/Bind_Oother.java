package rainbow.functions.interpreted.optimise;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Bind_Oother extends InterpretedFunction {
  private ArcObject optExpr;
  private Pair optInstructions;

  public Bind_Oother(ArcObject parameterList, Map lexicalBindings, Pair expandedBody) {
    super(parameterList, lexicalBindings, expandedBody);
    this.optExpr = parameterList.car().cdr().cdr().car();
    List i = new ArrayList();
    this.optExpr.addInstructions(i);
    this.optInstructions = Pair.buildFrom(i);
  }

  public void invokeN(VM vm, LexicalClosure lc) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    vm.pushFrame(lc, optInstructions);
    lc.add(vm.thread());
    vm.pushFrame(lc, this.instructions);
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    lc.add(arg);
    vm.pushFrame(lc, this.instructions);
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
