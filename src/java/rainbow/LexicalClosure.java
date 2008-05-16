package rainbow;

import rainbow.types.ArcObject;
import rainbow.types.Pair;

import java.util.Map;

public class LexicalClosure {
  private ArcObject[] bindings;
  private Map indices;
  private int count;
  private LexicalClosure parent;

  public LexicalClosure(Map lexicalBindings, LexicalClosure parent) {
    this.parent = parent;
    bindings = new ArcObject[lexicalBindings.size()];
    this.indices = lexicalBindings;
  }

  public void add(ArcObject value) {
    if (count >= bindings.length) {
      throw new Error("Can't add " + value + " to bindings: already full (" + count + ") " + Pair.buildFrom(bindings) + " " + indices);
    }
    bindings[count] = value;
    count++;
  }
  
  public LexicalClosure nth(int n) {
    return (n == 0) ? this : parent.nth(n - 1);
  }

  public String toString() {
    StringBuilder b = new StringBuilder();
    for (Object o : indices.keySet()) {
      int i = (Integer) indices.get(o);
      b.append(o).append(" : ").append(bindings[i]).append("\n");
    }
    return b.toString();
  }

  public ArcObject at(int index) {
    return bindings[index];
  }

  public void set(int index, ArcObject o) {
    bindings[index] = o;
  }
}
