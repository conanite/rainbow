package rainbow.vm.interceptor;

import rainbow.vm.interceptor.FunctionProfile;
import rainbow.types.ArcObject;

import java.util.Map;
import java.util.HashMap;

public class ProfileData {
  public Map<String, FunctionProfile> invocationProfile = new HashMap();
  public Map<String, Long> instructionProfile = new HashMap();
  public ArcObject lastInvokee;
  public long now;
}
