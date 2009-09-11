package rainbow.functions.rainbow;

import rainbow.ArcError;
import rainbow.functions.Builtin;
import rainbow.types.*;
import rainbow.vm.VM;
import rainbow.vm.interceptor.VMInterceptor;
import rainbow.vm.interceptor.FunctionProfile;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class RainbowProfileReport extends Builtin {
  public RainbowProfileReport() {
    super("rainbow-profile-report");
  }

  public void invokef(VM vm) {
    vm.pushA(report(vm));
  }

  public void invokef(VM vm, ArcObject arg) {
    vm.pushA(report((VM) arg));
  }

  public ArcObject report(VM target) {
    target.setInterceptor(VMInterceptor.NULL);
    return createReport(target);
  }

  private ArcObject createReport(VM target) {
    Hash report = new Hash();
    report.sref(createInvocationReport(target.profileData.invocationProfile), Symbol.mkSym("invocation-profile"));
    report.sref(createReport(target.profileData.instructionProfile), Symbol.mkSym("instruction-profile"));
    return report;
  }

  private Pair createReport(Map<String, Long> profile) {
    Pair result = NIL;
    if (profile != null) {
      Comparator<Entry<String, Long>> c = new Comparator<Entry<String, Long>>() {
        public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
          int comparison = o1.getValue().compareTo(o2.getValue());
          if (comparison == 0) {
            comparison = o2.getKey().compareTo(o1.getKey());
          }
          return comparison;
        }
      };
      Set<Entry<String, Long>> sorted = new TreeSet(c);
      sorted.addAll(profile.entrySet());
      for (Entry<String, Long> e : sorted) {
        result = new Pair(new Pair(Rational.make(e.getValue()), ArcString.make(e.getKey())), result);
      }
    }
    return result;
  }

  private Pair createInvocationReport(Map<String, FunctionProfile> profile) {
    Pair result = NIL;
    if (profile != null) {
      Comparator<FunctionProfile> c = new Comparator<FunctionProfile>() {
        public int compare(FunctionProfile o1, FunctionProfile o2) {
          int c = o1.totalNanoTime < o2.totalNanoTime ? -1 : o1.totalNanoTime == o2.totalNanoTime ? 0 : 1;
          if (c == 0) {
            c = o1.nanoTime < o2.nanoTime ? -1 : o1.nanoTime == o2.nanoTime ? 0 : 1;
          }
          if (c == 0) {
            c = o1.name.compareTo(o2.name);
          }
          if (c == 0) {
            throw new ArcError("can't compare " + o1.name + " with " + o2.name);
          }
          return c;
        }
      };
      Set<FunctionProfile> sorted = new TreeSet(c);
      for (FunctionProfile fp : profile.values()) {
        if (fp.parent == null) {
          sorted.add(fp);
        }
      }
      for (FunctionProfile e : sorted) {
        result = new Pair(e.toPair(), result);
      }
    }
    return result;
  }
}
