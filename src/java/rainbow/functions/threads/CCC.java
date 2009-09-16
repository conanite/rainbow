package rainbow.functions.threads;

import rainbow.LexicalClosure;
import rainbow.functions.Builtin;
import rainbow.types.ArcException;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.instructions.Finally;

public class CCC extends Builtin {
  public CCC() {
    super("ccc");
  }

  public void invokef(VM vm, ArcObject fn) {
    ContinuationWrapper e = new ContinuationWrapper(vm);
    TriggerCopyVM tcv = new TriggerCopyVM(e);
    tcv.belongsTo(this);
    vm.pushFrame(tcv);
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
      cc.cloneVM();
    }
  }

  static class ShallowVMState {
    public int ap = -1;
    public int ip = -1;
    private LexicalClosure currentLc;
    private ArcObject[] currentParams;
    private ArcException error;
    private boolean dead = false;
    private int ipThreshold;

    public ShallowVMState(VM vm) {
      this.ap = vm.ap();
      this.ip = vm.ip;
      this.currentLc = vm.lc();
      this.currentParams = vm.currentParams;
      this.error = vm.error;
      this.dead = vm.dead;
      this.ipThreshold = vm.ipThreshold;
    }

    public void restore(VM vm) {
      vm.ap = this.ap;
      vm.ip = this.ip;
      vm.currentLc = this.currentLc;
      vm.currentParams = this.currentParams;
      vm.error = this.error;
      vm.dead = this.dead;
      vm.ipThreshold = this.ipThreshold;
    }
  }

  public static class ContinuationWrapper extends ArcObject {
    private VM vm;
    private ShallowVMState svs;

    public ContinuationWrapper(VM vm) {
      this.vm = vm;
      this.svs = new ShallowVMState(vm);
    }

    public void invokef(VM vm, ArcObject arg) {
      if (svs != null && this.vm == vm) {
        svs.restore(vm);
      } else {
        this.vm.copyTo(vm);
      }
      vm.pushA(arg);
    }

    public void invoke(VM vm, Pair args) {
      invokef(vm, args.car());
    }

    public ArcObject type() {
      return TYPE;
    }

    public void cloneVM() {
      vm = vm.copy();
      svs = null;
    }
  }
}
