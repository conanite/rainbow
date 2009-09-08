package rainbow.vm.interpreter;

import rainbow.ArcError;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.interpreter.visitor.Visitor;

import java.util.List;
import java.util.Map;

public class LastAssignment extends SingleAssignment {

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
      throw new ArcError("assign: error: unexpected " + o);
    }
  }

  public String toString() {
    return name + " " + expression;
  }

  public void addInstructions(List i) {
    addMyInstructions(i, true);
  }

  public int countReferences(int refs, BoundSymbol p) {
    refs = name.countReferences(refs, p);
    return expression.countReferences(refs, p);
  }

  public int highestLexicalScopeReference() {
    int n = name.highestLexicalScopeReference();
    int e = expression.highestLexicalScopeReference();
    return e > n ? e : n;
  }

  public boolean hasClosures() {
    if (expression instanceof InterpretedFunction) {
      return ((InterpretedFunction) expression).requiresClosure();
    }
    return expression.hasClosures();
  }

  public SingleAssignment inline(BoundSymbol p, ArcObject arg, boolean unnest, int nesting, int paramIndex) {
    LastAssignment sa = new LastAssignment();
    if (name instanceof BoundSymbol && p.isSameBoundSymbol((BoundSymbol) name)) {
      throw new ArcError("Can't inline " + p + " -> " + arg + "; assignment");
    }
    sa.name = this.name.inline(p, arg, unnest, nesting, paramIndex);
    sa.expression = this.expression.inline(p, arg, unnest, nesting, paramIndex);
    return sa;
  }

  public SingleAssignment inline(StackSymbol p, ArcObject arg, int paramIndex) {
    LastAssignment sa = new LastAssignment();
    if (name instanceof StackSymbol && p.isSameStackSymbol((StackSymbol) name)) {
      throw new ArcError("Can't inline " + p + " -> " + arg + "; assignment");
    }
    sa.name = this.name.inline(p, arg, paramIndex);
    sa.expression = this.expression.inline(p, arg, paramIndex);
    return sa;
  }

  public LastAssignment nest(int threshold) {
    LastAssignment sa = new LastAssignment();
    sa.name = this.name.nest(threshold);
    sa.expression = this.expression.nest(threshold);
    return sa;
  }

  public ArcObject replaceBoundSymbols(Map<Symbol, Integer> lexicalBindings) {
    LastAssignment sa = new LastAssignment();
    sa.name = this.name.replaceBoundSymbols(lexicalBindings);
    sa.expression = this.expression.replaceBoundSymbols(lexicalBindings);
    return sa;
  }

  public void collectReferences(BoundSymbol b, List bs) {
    name.collectReferences(b, bs);
    expression.collectReferences(b, bs);
  }

  public void visit(Visitor v) {
    v.accept(this);
    name.visit(v);
    expression.visit(v);
    v.end(this);
  }
}
