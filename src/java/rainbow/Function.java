package rainbow;

import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public interface Function {
  void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args);

  String code();
}
