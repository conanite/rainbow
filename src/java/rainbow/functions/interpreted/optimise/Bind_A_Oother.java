package rainbow.functions.interpreted.optimise;

import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Bind_A_Oother extends InterpretedFunction {
  private Pair optInstructions;

  public Bind_A_Oother(ArcObject parameterList, Map lexicalBindings, Pair expandedBody) {
    super(parameterList, lexicalBindings, expandedBody);
    ArcObject optExpr = parameterList.cdr().car().cdr().cdr().car();
    List i = new ArrayList();
    optExpr.addInstructions(i);
    this.optInstructions = Pair.buildFrom(i);
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    lc.add(arg);
    vm.pushFrame(lc, optInstructions);
    lc.add(vm.thread());
    vm.pushFrame(lc, this.instructions);
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg1, ArcObject arg2) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    lc.add(arg1);
    lc.add(arg2);
    vm.pushFrame(lc, this.instructions);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    requireNotNil(args, args);
    ArcObject arg1 = args.car();
    ArcObject rest = args.cdr();
    if (rest instanceof Nil) {
      invokeN(vm, lc, arg1);
    } else {
      requireNil(rest.cdr(), args);
      invokeN(vm, lc, arg1, rest.car());
    }
  }
}
