package rainbow.vm.interpreter;

import rainbow.types.ArcObject;

import java.util.List;

public interface Conditional {
  void add(Conditional c);
  void take(ArcObject expression);
  void addInstructions(List i);
}
