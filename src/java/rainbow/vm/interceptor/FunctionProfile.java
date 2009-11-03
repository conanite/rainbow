package rainbow.vm.interceptor;

import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionProfile {
  public ArcObject target;
  public String name;
  public long invocationCount;
  public long nanoTime;
  public long totalNanoTime;
  public List<FunctionProfile> children = new ArrayList();
  public FunctionProfile parent;
  public Map<String, InvocationCounter> callerCounts = new HashMap();

  public void addNanoTime(long nanos) {
    this.nanoTime += nanos;
    this.totalNanoTime += nanos;
    FunctionProfile p = parent;
    while (p != null) {
      p.totalNanoTime += nanos;
      p = p.parent;
    }
  }

  public ArcObject toPair() {
    ArcObject fn = ArcString.make(target.profileName());
    if (target instanceof InterpretedFunction) {
      fn = ArcString.make(((InterpretedFunction)target).localProfileName());
    }
    Real nanos = Real.make(nanoTime / 1000000.0);
    Real totalNanos = Real.make(totalNanoTime / 1000000.0);
    Rational invs = Rational.make(invocationCount);
    Pair kidz = ArcObject.NIL;
    for (FunctionProfile child : children) {
      kidz = new Pair(child.toPair(), kidz);
    }
    Pair callers = ArcObject.NIL;
    for (InvocationCounter ic : callerCounts.values()) {
      callers = new Pair(ic.toPair(), callers);
    }
    return Pair.buildFrom(totalNanos, nanos, invs, fn, kidz, callers);
  }

  static FunctionProfile get(Map<String, FunctionProfile> map, ArcObject function) {
    String name = function.profileName();
    FunctionProfile fp = map.get(name);
    if (fp == null) {
      fp = new FunctionProfile();
      fp.target = function;
      fp.name = name;
      map.put(name, fp);
      if (function instanceof InterpretedFunction) {
        InterpretedFunction parent = ((InterpretedFunction)function).lexicalOwner();
        if (parent != null) {
          FunctionProfile pfp = get(map, parent);
          pfp.children.add(fp);
          fp.parent = pfp;
        }
      }
    }

    return fp;
  }

  public void addAncestor(ArcObject invocation) {
    if (invocation == null) {
      return;
    }

    InvocationCounter.get(callerCounts, invocation).count++;
  }
}
