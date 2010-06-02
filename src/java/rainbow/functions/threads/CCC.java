package rainbow.functions.threads;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.instructions.Finally;

import java.util.List;

public class CCC extends Builtin {

  public CCC() {
    super("ccc");
  }

  public void invokef(VM vm, ArcObject fn) {
    if (fn instanceof Nil) {
      throw new ArcError("Can't ccc nil!");
    }
    ContinuationWrapper e = new ContinuationWrapper(vm);


//    TODO no longer need TCV
//    TriggerCopyVM tcv = new TriggerCopyVM(e);
//    tcv.belongsTo(this);
//    vm.pushFrame(tcv);


    fn.invokef(vm, e);
  }

  public void invoke(VM vm, Pair args) {
    checkMaxArgCount(args, getClass(), 1);
    invokef(vm, args.car());
  }

  static class TriggerCopyVM extends Instruction implements Finally {
    private ContinuationWrapper cc;

    public TriggerCopyVM(ContinuationWrapper cc) {
      this.cc = cc;
    }

    public void operate(VM vm) {
      cc.setCopyRequired();
    }

    public String toString() {
      return "#<require-vm-copy #" + cc.vm.threadId + ">";
    }
  }

  public static class ContinuationWrapper extends ArcObject {
    private VM vm;
    private boolean copyRequired = false;

    public ContinuationWrapper(VM vm) {
      this.vm = vm.copy();
    }

    private void applyFinallies(VM vm, List[] finallies) {
      int fc = finallies[0].size();
      for (int i = fc - 1; i >= 0; i--) {
        LexicalClosure lc = (LexicalClosure) finallies[1].get(i);          
        Pair instructions = (Pair) finallies[0].get(i);
        vm.pushInvocation(lc, instructions);
      }
    }

    public void invokef(VM vm, ArcObject arg) {
      int oldIP = vm.lastCommonAncestor(this.vm);
      List[] finallies = vm.gatherFinallies(oldIP);
      this.vm.copyTo(vm);
      vm.pushA(arg);
      applyFinallies(vm, finallies);
    }

    public void faster_but_broken_invokef(VM vm, ArcObject arg) {
      if (copyRequired) {
        int oldIP = vm.lastCommonAncestor(this.vm);
        List[] finallies = vm.gatherFinallies(oldIP);
        this.vm.copyTo(vm);
        vm.pushA(arg);
        applyFinallies(vm, finallies);
      } else {
        List[] finallies = vm.gatherFinallies(this.vm.ip);
        vm.ap = this.vm.ap;
        vm.ip = this.vm.ip;
        vm.currentLc = this.vm.currentLc;
        vm.currentParams = this.vm.currentParams;
        vm.error = this.vm.error;
        vm.dead = this.vm.dead;
        vm.ipThreshold = this.vm.ipThreshold;
        vm.pushA(arg);
        applyFinallies(vm, finallies);
      }
    }

    public void invoke(VM vm, Pair args) {
      invokef(vm, args.car());
    }

    public ArcObject type() {
      return TYPE;
    }

    public void setCopyRequired() {
      this.copyRequired = true;
    }

    public String toString() {
      return "#<continuation ip:" + vm.ip + ";ap:" + vm.ap + ";VM#" + vm.threadId + ">";
    }
  }
}
