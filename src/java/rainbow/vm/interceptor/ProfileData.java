package rainbow.vm.interceptor;

import rainbow.types.ArcObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileData {
  public Map<String, FunctionProfile> invocationProfile = new HashMap();
  public Map<String, Long> instructionProfile = new HashMap();
  public ArcObject lastInvokee;
  public long now;
}
