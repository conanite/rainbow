package rainbow.vm;

import rainbow.types.ArcObject;
import rainbow.vm.instructions.invoke.Invoke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

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
      if (vm.ip <= vm.debug_target_frame) {
        vm.setInterceptor(DEBUG);
      }
    }
    void end(VM vm) {
    }},

  PROFILE {
    private void addInvocation(ArcObject o, VM vm) {
      if (vm.profileData == null) {
        vm.profileData = new HashMap();
      }
      String key;
      if (o.literal()) {
        key = "literal-ref:" + o.type().toString();
      } else {
        key = o.toString();
      }
      Integer count = vm.profileData.get(key);
      if (count == null) {
        count = 1;
      } else {
        count++;
      }
      vm.profileData.put(key, count);
    }

    void check(VM vm) {
      if (!vm.hasInstructions()) {
        return;
      }
      if (vm.peekI().car() instanceof Invoke) {
        vm.loadCurrentContext();
        addInvocation(((Invoke) vm.peekI().car()).getInvokee(vm), vm);
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
        vm.debug_target_frame = vm.ip - 1;
        vm.setInterceptor(VMInterceptor.NEXT_FRAME);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
