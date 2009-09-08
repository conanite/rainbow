package rainbow;

import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class LexicalClosure {
  private ArcObject[] bindings;
  private int count;
  public final LexicalClosure parent;

  public LexicalClosure(int length, LexicalClosure parent) {
    this.parent = parent;
    bindings = new ArcObject[length];
  }

  public void add(ArcObject value) {
    if (count >= bindings.length) {
      throw new Error("Can't add " + value + " to bindings: already full (" + count + ") " + Pair.buildFrom(bindings));
    }
    bindings[count] = value;
    count++;
  }

  public LexicalClosure nth(int n) {
    switch (n) {
      case 0: return this;
      default: return parent.nth(n - 1);
    }
  }

  public String toString() {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < bindings.length; i++) {
      b.append(i).append(" : ").append(bindings[i]).append("\n");
    }
    return b.toString() + "\nparent: " + (parent == null ? "<none>" : parent.toString());
  }

  public ArcObject at(int index) {
    return bindings[index];
  }

  public ArcObject set(int index, ArcObject o) {
    return bindings[index] = o;
  }

  public boolean finished() {
    return count == bindings.length;
  }

  public int count() {
    return count;
  }

  public int size() {
    return bindings.length;
  }
}
