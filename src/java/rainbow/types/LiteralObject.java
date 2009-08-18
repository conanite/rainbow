package rainbow.types;

import rainbow.vm.instructions.Literal;

import java.util.List;

public abstract class LiteralObject extends ArcObject {
  public boolean literal() {
    return true;
  }

  public void addInstructions(List i) {
    i.add(new Literal(this));
  }
}
