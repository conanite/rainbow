package rainbow.vm.interpreter;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.instructions.cond.Cond;
import rainbow.vm.interpreter.visitor.Visitor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  static Map<String, Method> handlers = new HashMap();

  public void addInstructions(List i) {
    String sig = sig();
    String prefix = "rainbow.vm.instructions.cond.optimise.If";
    String classname = prefix + sig;
    try {
      if (!handlers.containsKey(classname)) {
        loadHandler(classname);
        addInstructions(i);
      } else {
        Method m = handlers.get(classname);
        if (m == null) {
          defaultAddInstructions(i, sig);
        } else {
          m.invoke(null, i, ifExpression, thenExpression, next);
        }
      }
    } catch (Exception e) {
      throw new ArcError("Couldn't instantiate " + classname + ": " + e, e);
    }
  }

  static void loadHandler(String classname) {
    try {
      Class c = Class.forName(classname);
      Method m = c.getMethod("addInstructions", List.class, ArcObject.class, ArcObject.class, ArcObject.class);
      handlers.put(classname, m);
    } catch (ClassNotFoundException e) {
      handlers.put(classname, null);
    } catch (NoSuchMethodException e) {
      throw new ArcError("couldn't find handler method 'addInstructions(List,ArcObject,ArcObject,ArcObject) on " + classname + ": " + e, e);
    }
  }

  private void defaultAddInstructions(List i, String sig) {
    ifExpression.addInstructions(i);
    i.add(new Cond(thenExpression, (ArcObject) next, sig));
  }

  public ArcObject reduce() {
    next = (Conditional)next.reduce();
    if (ifExpression instanceof Nil) {
      return (ArcObject) next;
    } else if (reduceToIfBound() || reduceToIfStack()) {
      Else e = new Else();
      e.take(ifExpression);
      return e;
    } else {
      return this;
    }
  }

  private boolean reduceToIfBound() {
    if (!(ifExpression instanceof BoundSymbol) || !(thenExpression instanceof BoundSymbol) || !(next instanceof Else)) {
      return false;
    } else {
      BoundSymbol b1 = (BoundSymbol) ifExpression;
      BoundSymbol b2 = (BoundSymbol) thenExpression;
      Else e = (Else) next;
      return b1.isSameBoundSymbol(b2) && (e.ifExpression instanceof Nil);
    }
  }

  private boolean reduceToIfStack() {
    if (!(ifExpression instanceof StackSymbol) || !(thenExpression instanceof StackSymbol) || !(next instanceof Else)) {
      return false;
    } else {
      StackSymbol b1 = (StackSymbol) ifExpression;
      StackSymbol b2 = (StackSymbol) thenExpression;
      Else e = (Else) next;
      return b1.isSameStackSymbol(b2) && (e.ifExpression instanceof Nil);
    }
  }

  public String toString() {
    return "if:" + ifExpression + " then:" + thenExpression + " else:" + next;
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

  public ArcObject inline(StackSymbol p, ArcObject arg, int paramIndex) {
    IfThen other = new IfThen();
    other.ifExpression = this.ifExpression.inline(p, arg, paramIndex);
    other.thenExpression = this.thenExpression.inline(p, arg, paramIndex);
    other.next = (Conditional) this.next.inline(p, arg, paramIndex);
    return other;
  }

  public ArcObject nest(int threshold) {
    IfThen other = new IfThen();
    other.ifExpression = this.ifExpression.nest(threshold);
    other.thenExpression = this.thenExpression.nest(threshold);
    other.next = (Conditional) this.next.nest(threshold);
    return other;
  }

  public ArcObject replaceBoundSymbols(Map<Symbol, Integer> lexicalBindings) {
    IfThen other = new IfThen();
    other.ifExpression = this.ifExpression.replaceBoundSymbols(lexicalBindings);
    other.thenExpression = this.thenExpression.replaceBoundSymbols(lexicalBindings);
    other.next = (Conditional) this.next.replaceBoundSymbols(lexicalBindings);
    return other;
  }

  public void visit(Visitor v) {
    v.accept(this);
    ifExpression.visit(v);
    thenExpression.visit(v);
    next.visit(v);
    v.end(this);
  }

  public String sig() {
    String s = "";
    s += "_";
    s += Invocation.sig(ifExpression);
    s += "_";
    s += Invocation.sig(thenExpression);
    if (next instanceof Else) {
      s += "_";
      s += Invocation.sig(((Else)next).ifExpression);
    } else {
      s += "_other";
    }
    return s;
  }
}
