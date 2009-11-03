package rainbow.vm.interceptor;

import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.ArcString;
import rainbow.types.Pair;
import rainbow.types.Rational;

import java.util.Map;

public class InvocationCounter {
  public ArcObject target;
  public long count;
  public String key;

  public InvocationCounter(ArcObject target) {
    this.target = target;
    this.key = target.profileName();
  }

  public void inc() {
    count++;
  }

  public Pair toPair() {
    String n = key;
    if (target instanceof InterpretedFunction) {
      n = ((InterpretedFunction)target).localProfileName();
    }
    return new Pair(ArcString.make(n), new Pair(Rational.make(count), ArcObject.NIL));
  }

  public static InvocationCounter get(Map<String, InvocationCounter> map, ArcObject o) {
    InvocationCounter ic = map.get(o.profileName());
    if (ic == null) {
      ic = new InvocationCounter(o);
      map.put(ic.key, ic);
    }
    return ic;
  }
}
