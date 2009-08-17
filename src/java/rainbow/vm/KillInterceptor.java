package rainbow.vm;

public class KillInterceptor implements VMInterceptor {
  public void check(VM vm) {
    vm.die();
  }

  public void end(VM vm) {
  }
}
