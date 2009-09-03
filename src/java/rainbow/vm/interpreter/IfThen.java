package rainbow.vm.interpreter;

import rainbow.Nil;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.instructions.cond.Cond;
import rainbow.vm.instructions.cond.Cond_Lex;
import rainbow.vm.interpreter.visitor.Visitor;

import java.util.List;

public class IfThen extends ArcObject implements Conditional {
  public ArcObject ifExpression;
  public ArcObject thenExpression;
  public Conditional next;

  public ArcObject type() {
    return Symbol.mkSym("if-then-clause");
  }

  public void add(Conditional c) {
    if (next != null) {
      next.add(c);
    } else {
      next = c;
    }
  }

  public void take(ArcObject expression) {
    if (ifExpression == null) {
      ifExpression = expression;
    } else if (thenExpression == null) {
      thenExpression = expression;
    } else {
      next.take(expression);
    }
  }

  public void addInstructions(List i) {
    if (ifExpression instanceof BoundSymbol) {
      Cond_Lex.addInstructions(i, (BoundSymbol) ifExpression, thenExpression, next);
    } else {
      ifExpression.addInstructions(i);
      i.add(new Cond(thenExpression, (ArcObject) next));
    }
  }

  public ArcObject reduce() {
    next = (Conditional)next.reduce();
    if (ifExpression instanceof Nil) {
      return (ArcObject) next;
    } else if (reduceToIfExpr()) {
      Else e = new Else();
      e.take(ifExpression);
      return e;
    } else {
      return this;
    }
  }

  private boolean reduceToIfExpr() {
    if (!(ifExpression instanceof BoundSymbol) || !(thenExpression instanceof BoundSymbol) || !(next instanceof Else)) {
      return false;
    } else {
      BoundSymbol b1 = (BoundSymbol) ifExpression;
      BoundSymbol b2 = (BoundSymbol) thenExpression;
      Else e = (Else) next;
      return b1.isSameBoundSymbol(b2) && (e.ifExpression instanceof Nil);
    }
  }

  public String toString() {
    return ifExpression + " " + thenExpression + " " + next;
  }

  public int countReferences(int refs, BoundSymbol p) {
    refs = ifExpression.countReferences(refs, p);
    refs = thenExpression.countReferences(refs, p);
    return next.countReferences(refs, p);
  }

  public int highestLexicalScopeReference() {
    int hif = ifExpression.highestLexicalScopeReference();
    int hthen = thenExpression.highestLexicalScopeReference();
    int helse = next.highestLexicalScopeReference();
    return Math.max(Math.max(hif, hthen), helse);
  }

  public boolean assigns(int nesting) {
    return ifExpression.assigns(nesting) || thenExpression.assigns(nesting) || next.assigns(nesting);
  }

  public boolean hasClosures() {
    if (ifExpression instanceof InterpretedFunction) {
      if (((InterpretedFunction) ifExpression).requiresClosure()) {
        return true;
      }
    }
    if (thenExpression instanceof InterpretedFunction) {
      if (((InterpretedFunction) thenExpression).requiresClosure()) {
        return true;
      }
    }
    return ifExpression.hasClosures() || thenExpression.hasClosures() || next.hasClosures();
  }

  public ArcObject inline(BoundSymbol p, ArcObject arg, boolean unnest, int nesting, int paramIndex) {
    IfThen other = new IfThen();
    other.ifExpression = this.ifExpression.inline(p, arg, unnest, nesting, paramIndex);
    other.thenExpression = this.thenExpression.inline(p, arg, unnest, nesting, paramIndex);
    other.next = (Conditional) this.next.inline(p, arg, unnest, nesting, paramIndex);
    return other;
  }

  public ArcObject nest(int threshold) {
    IfThen other = new IfThen();
    other.ifExpression = this.ifExpression.nest(threshold);
    other.thenExpression = this.thenExpression.nest(threshold);
    other.next = (Conditional) this.next.nest(threshold);
    return other;
  }

  public void visit(Visitor v) {
    v.accept(this);
    ifExpression.visit(v);
    thenExpression.visit(v);
    next.visit(v);
    v.end(this);
  }
}
