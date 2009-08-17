package rainbow.vm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class DebugInterceptor implements VMInterceptor {
  public void check(VM vm) {
    try {
      vm.show();
      System.out.println("q to resume execution, any other key to step");
      String command = new BufferedReader(new InputStreamReader(System.in)).readLine();
      if ("q".equalsIgnoreCase(command)) {
        vm.setInterceptor(VMInterceptor.NULL);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void end(VM vm) {
    try {
      System.out.println("VM thread finished: returning.");
      vm.show();
      System.out.println("q to resume execution, any other key to step");
      String command = new BufferedReader(new InputStreamReader(System.in)).readLine();
      if ("q".equalsIgnoreCase(command)) {
        vm.setInterceptor(VMInterceptor.NULL);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
