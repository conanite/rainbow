package rainbow.functions.rainbow;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.vm.VM;
import rainbow.vm.interceptor.VMInterceptor;

public class RainbowProfile extends Builtin {
  public RainbowProfile() {
    super("rainbow-profile");
  }

  public void invokef(VM vm) {
    profile(vm);
    vm.pushA(NIL);
  }

  public void invokef(VM vm, ArcObject arg) {
    profile((VM) arg);
    vm.pushA(NIL);
  }

  public void profile(VM target) {
    target.setInterceptor(VMInterceptor.PROFILE);
  }
}
