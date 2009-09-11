package rainbow.functions.errors;

import rainbow.functions.Builtin;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.Instruction;
import rainbow.vm.instructions.FinallyInvoke;
import rainbow.vm.instructions.PopArg;
import rainbow.vm.interpreter.visitor.Visitor;

public class Protect extends Builtin {
  private final Visitor v = new Visitor() {
    public void accept(Instruction o) {
      o.belongsTo(Protect.this);
    }
  };

  public Protect() {
    super("protect");
  }

  public void invoke(VM vm, Pair args) {
    Pair instructions = new Pair(
            new FinallyInvoke(args.cdr().car()), new Pair(
            new PopArg("clear up 'protect/after' return value"),
            NIL));
    instructions.visit(v);
    vm.pushInvocation(null, instructions);
    args.car().invoke(vm, NIL);
  }
}
