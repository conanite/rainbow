package rainbow.vm.interceptor;

import rainbow.functions.Closure;
import rainbow.types.ArcObject;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.instructions.invoke.Invoke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public enum VMInterceptor {
  NULL {
    public void check(VM vm) {
    }
    public void end(VM vm) {
    }
    public void install(VM vm) {
    }},

  DEBUG {
    public void check(VM vm) {
      debug(vm);
    }
    public void end(VM vm) {
      System.out.println("VM thread finished: returning.");
      debug(vm);
    }
    public void install(VM vm) {
    }},

  KILL {
    public void check(VM vm) {
      vm.die();
    }
    public void end(VM vm) {
    }
    public void install(VM vm) {
    }},

  NEXT_FRAME {
    public void check(VM vm) {
      if (vm.ip <= vm.debug_target_frame) {
        vm.setInterceptor(DEBUG);
      }
    }
    public void end(VM vm) {
    }

    public void install(VM vm) {
    }},

  PROFILE {

    private void addInstruction(Instruction i, VM vm) {
      Long c = vm.profileData.instructionProfile.get(i.getClass().getName());
      if (c == null) {
        c = 1L;
      } else {
        c++;
      }
      vm.profileData.instructionProfile.put(i.getClass().getName(), c);
    }

    public void check(VM vm) {
      updateNanoTime(vm);
      Instruction next = vm.nextInstruction();
      if (next == null) {
        return;
      }
      vm.loadCurrentContext();
      addInstruction(next, vm);
      if (next instanceof Invoke) {
        ArcObject fn = ((Invoke) next).getInvokee(vm);
        if (fn instanceof Closure) {
          fn = ((Closure)fn).fn();
        }
        FunctionProfile.get(vm.profileData.invocationProfile, fn).invocationCount++;
        vm.profileData.lastInvokee = fn;
      } else if (next.owner() != null) {
        vm.profileData.lastInvokee = next.owner();
      } else {
        vm.profileData.lastInvokee = ArcObject.NIL;
      }
      vm.profileData.now = System.nanoTime();
    }

    public void end(VM vm) {
    }

    public void install(VM vm) {
      vm.profileData = new ProfileData();
      vm.profileData.now = System.nanoTime();
    }};

  private static void updateNanoTime(VM vm) {
    ArcObject last = vm.profileData.lastInvokee;
    if (last != null) {
      FunctionProfile.get(vm.profileData.invocationProfile, last).addNanoTime(System.nanoTime() - vm.profileData.now);
    }
  }

  public abstract void check(VM vm);

  public abstract void end(VM vm);

  public abstract void install(VM vm);

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
