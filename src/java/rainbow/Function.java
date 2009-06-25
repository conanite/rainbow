package rainbow;

import rainbow.types.Pair;
import rainbow.vm.Continuation;

public interface Function {
  void invoke(LexicalClosure lc, Continuation caller, Pair args);
}
