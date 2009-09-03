package rainbow.vm.interpreter;

import rainbow.ArcError;
import rainbow.vm.interpreter.visitor.Visitor;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;

import java.util.List;

public class Else extends ArcObject implements Conditional {
  public ArcObject ifExpression;

  public ArcObject type() {
    return Symbol.mkSym("else-clause");
  }

  public void add(Conditional c) {
    throw new ArcError("Internal error: if clause: unexpected extra condition: " + c);
  }

  public void take(ArcObject expression) {
    if (ifExpression == null) {
      ifExpression = expression;
    } else {
      throw new ArcError("Internal error: if clause: unexpected: " + expression);
    }
  }

  public void addInstructions(List i) {
    ifExpression.addInstructions(i);
  }

  public ArcObject reduce() {
    return this;
  }

  public String inspect() {
    return "#<else " + ifExpression + ">";
  }

  public String toString() {
    return ifExpression.toString();
  }

  public int countReferences(int refs, BoundSymbol p) {
    return ifExpression.countReferences(refs, p);
  }

  public int highestLexicalScopeReference() {
    return ifExpression.highestLexicalScopeReference();
  }

  public boolean assigns(int nesting) {
    return ifExpression.assigns(nesting);
  }

  public boolean hasClosures() {
    if (ifExpression instanceof InterpretedFunction) {
      return ((InterpretedFunction) ifExpression).requiresClosure();
    } else {
      return ifExpression.hasClosures();
    }
  }

  public ArcObject inline(BoundSymbol p, ArcObject arg, boolean unnest, int nesting, int paramIndex) {
    Else e = new Else();
    e.ifExpression = this.ifExpression.inline(p, arg, unnest, nesting, paramIndex).reduce();
    return e;
  }

  public ArcObject nest(int threshold) {
    Else e = new Else();
    e.ifExpression = this.ifExpression.nest(threshold);
    return e;
  }

  public void visit(Visitor v) {
    v.accept(this);
    ifExpression.visit(v);
    v.end(this);
  }
}
