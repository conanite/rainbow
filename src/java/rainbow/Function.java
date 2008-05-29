package rainbow;

import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public interface Function {
  void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args);
}
