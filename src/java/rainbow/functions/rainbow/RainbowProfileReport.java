package rainbow.functions.rainbow;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.ArcString;
import rainbow.types.Pair;
import rainbow.types.Rational;
import rainbow.vm.VM;
import rainbow.vm.VMInterceptor;

import java.util.Comparator;
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

  public Pair report(VM target) {
    target.setInterceptor(VMInterceptor.NULL);
    return createReport(target);
  }

  private Pair createReport(VM target) {
    Pair result = NIL;
    if (target.profileData != null) {
      Comparator<Entry<String, Integer>> c = new Comparator<Entry<String, Integer>>() {
        public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
          return o1.getValue().compareTo(o2.getValue());
        }
      };
      Set<Entry<String, Integer>> sorted = new TreeSet(c);
      sorted.addAll(target.profileData.entrySet());
      for (Entry<String, Integer> e : sorted) {
        result = new Pair(new Pair(Rational.make(e.getValue()), ArcString.make(e.getKey())), result);
      }
    }
    return result;
  }
}
