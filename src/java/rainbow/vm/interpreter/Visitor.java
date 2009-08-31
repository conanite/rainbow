package rainbow.vm.interpreter;

import rainbow.types.ArcObject;

public interface Visitor {
  ArcObject accept(ArcObject arcObject);
}
