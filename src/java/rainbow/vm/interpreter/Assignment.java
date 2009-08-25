package rainbow.vm.interpreter;

import rainbow.ArcError;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;

import java.util.List;

public class Assignment extends ArcObject {
  private SingleAssignment assignment;

  public ArcObject type() {
    return Symbol.mkSym("assignment");
  }

  public void add(ArcObject o) {
    assignment.take(o);
  }

  public void addInstructions(List i) {
    assignment.addInstructions(i);
  }

  public void prepare(int size) {
    if (size % 2 != 0) {
      throw new ArcError("assign: requires even number of arguments");
    }

    size = (size / 2) - 1;
    if (size < 0) {
      throw new ArcError("assign: nothing to assign");
    }

    assignment = new LastAssignment();
    while (size > 0) {
      assignment = new SingleAssignment(assignment);
      size--;
    }
  }

  public String toString() {
    return "(assign " + assignment + ")";
  }

  public int highestLexicalScopeReference() {
    return assignment.highestLexicalScopeReference();
  }

  public boolean assigns(BoundSymbol p) {
    return assignment.assigns(p);
  }

  public boolean hasClosures() {
    return assignment.hasClosures();
  }

  public ArcObject inline(BoundSymbol p, ArcObject arg, boolean unnest) {
    Assignment a = new Assignment();
    a.assignment = this.assignment.inline(p, arg, unnest);
    return a;
  }
}
