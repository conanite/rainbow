package rainbow.vm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public enum VMInterceptor {
  NULL {
    void check(VM vm) {
    }
    void end(VM vm) {
    }},

  DEBUG {
    void check(VM vm) {
      debug(vm);
    }
    void end(VM vm) {
      System.out.println("VM thread finished: returning.");
      debug(vm);
    }},

  KILL {
    void check(VM vm) {
      vm.die();
    }
    void end(VM vm) {
    }},

  NEXT_FRAME {
    void check(VM vm) {
      if (vm.peekI().len() == 1L) {
        vm.setInterceptor(DEBUG);
      }
    }
    void end(VM vm) {
    }};

  abstract void check(VM vm);

  abstract void end(VM vm);

  private static void debug(VM vm) {
    try {
      vm.show();
      System.out.println("q to resume execution, f to skip to end of this frame, any other key to step");
      String command = new BufferedReader(new InputStreamReader(System.in)).readLine();
      if ("q".equalsIgnoreCase(command)) {
        vm.setInterceptor(VMInterceptor.NULL);
      } else if ("f".equalsIgnoreCase(command)) {
        vm.setInterceptor(VMInterceptor.NEXT_FRAME);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
