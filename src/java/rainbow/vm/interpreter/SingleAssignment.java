package rainbow.vm.interpreter;

import rainbow.ArcError;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.instructions.assign.bound.Assign_Lex;
import rainbow.vm.instructions.assign.free.Assign_Free;
import rainbow.vm.interpreter.visitor.Visitor;

import java.util.List;

public class SingleAssignment extends ArcObject {
  protected ArcObject name;
  protected ArcObject expression;
  private SingleAssignment next;

  public SingleAssignment() {
  }

  public SingleAssignment(SingleAssignment next) {
    this.next = next;
  }

  public void take(ArcObject o) {
    if (name == null) {
      if ((o instanceof Symbol) || (o instanceof BoundSymbol)) {
        name = o;
      } else {
        throw new ArcError("assign: can't assign to " + o);
      }
    } else if (expression == null) {
      expression = o;
    } else {
      next.take(o);
    }
  }

  public String toString() {
    return name + " " + expression + "\n  ";
  }

  public void addInstructions(List i) {
    if (name instanceof BoundSymbol) {
      Assign_Lex.addInstructions(i, (BoundSymbol) name, expression, false);
    } else if (name instanceof Symbol) {
      Assign_Free.addInstructions(i, (Symbol) name, expression, false);
    }
    next.addInstructions(i);
  }

  public ArcObject type() {
    return Symbol.mkSym("assignment");
  }

  public int countReferences(int refs, BoundSymbol p) {
    refs = name.countReferences(refs, p);
    refs = expression.countReferences(refs, p);
    return next.countReferences(refs, p);
  }

  public int highestLexicalScopeReference() {
    int n = name.highestLexicalScopeReference();
    int me = expression.highestLexicalScopeReference();
    int other = next.highestLexicalScopeReference();
    return Math.max(n, Math.max(me, other));
  }

  public boolean assigns(int nesting) {
    return true;
  }

  public boolean hasClosures() {
    if (expression instanceof InterpretedFunction) {
      return ((InterpretedFunction) expression).requiresClosure() || next.hasClosures();
    }
    return expression.hasClosures() || next.hasClosures();
  }

  public SingleAssignment inline(BoundSymbol p, ArcObject arg, boolean unnest, int nesting, int paramIndex) {
    SingleAssignment sa = new SingleAssignment();
    if (name instanceof BoundSymbol && p.isSameBoundSymbol((BoundSymbol) name)) {
      throw new ArcError("Can't inline " + p + " -> " + arg + "; assignment");
    }
    sa.name = this.name;
    sa.expression = this.expression.inline(p, arg, unnest, nesting, paramIndex);
    sa.next = this.next.inline(p, arg, unnest, nesting, paramIndex);
    return sa;
  }

  public SingleAssignment nest(int threshold) {
    SingleAssignment sa = new SingleAssignment();
    sa.name = this.name.nest(threshold);
    sa.expression = this.expression.nest(threshold);
    sa.next = this.next.nest(threshold);
    return sa;
  }

  public void collectReferences(BoundSymbol b, List bs) {
    name.collectReferences(b, bs);
    expression.collectReferences(b, bs);
    next.collectReferences(b, bs);
  }

  public void visit(Visitor v) {
    v.accept(this);
    name.visit(v);
    expression.visit(v);
    next.visit(v);
    v.end(this);
  }
}
