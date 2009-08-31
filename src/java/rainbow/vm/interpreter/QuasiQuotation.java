package rainbow.vm.interpreter;

import rainbow.Nil;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import static rainbow.vm.compiler.QuasiQuoteCompiler.*;
import rainbow.vm.instructions.*;

import java.util.ArrayList;
import java.util.List;

public class QuasiQuotation extends ArcObject {
  private ArcObject target;

  public QuasiQuotation(ArcObject target) {
    this.target = target;
  }

  public ArcObject type() {
    return Symbol.mkSym("quasiquotation");
  }

  public void addInstructions(List i) {
    addInstructions(i, target);
  }

  public String toString() {
    return "`" + target;
  }

  public int highestLexicalScopeReference() {
    return highestLexicalScopeReference(Integer.MIN_VALUE, target);
  }

  private List<ArcObject> unquotes() {
    List l = new ArrayList();
    appendUnquotes(l, target, 1);
    return l;
  }

  private void appendUnquotes(List l, ArcObject expr, int nesting) {
    if (isUnQuote(expr)) {
      if (nesting == 1) {
        l.add(expr.cdr().car());
      } else {
        appendUnquotes(l, expr.cdr().car(), nesting - 1);
      }
      return;

    } else if (isUnQuoteSplicing(expr)) {
      if (nesting == 1) {
        l.add(expr.cdr().car());
      } else {
        appendUnquotes(l, expr.cdr().car(), nesting - 1);
      }
      return;

    } else if (isQuasiQuote(expr)) {
      appendUnquotes(l, expr.cdr().car(), nesting + 1);
      return;

    } else if (expr.isNotPair()) {
      return;
    }

    while (!expr.isNotPair()) {
      if (isUnQuote(expr) || isQuasiQuote(expr)) { // catch post-dot unquotes
        appendUnquotes(l, expr, nesting);
        expr = expr.cdr().cdr();
      } else {
        final ArcObject current = expr.car();
        expr = expr.cdr();
        if (isUnQuoteSplicing(current)) {
          appendUnquotes(l, current, nesting);
        } else if (isUnQuote(current) || isQuasiQuote(current) || isPair(current)) {
          appendUnquotes(l, current, nesting);
        }
      }
    }
  }

  private int highestLexicalScopeReference(int highest, ArcObject expr) { // todo this won't work for nested quasiquotes, shold work with unquotes() instead
    if (isUnQuote(expr)) {
      int me = expr.cdr().car().highestLexicalScopeReference();
      return me > highest ? me : highest;

    } else if (isUnQuoteSplicing(expr)) {
      int me = expr.cdr().car().highestLexicalScopeReference();
      return me > highest ? me : highest;

    }

    while (!expr.isNotPair()) {
      if (isUnQuote(expr)) { // catch post-dot unquotes
        highest = highestLexicalScopeReference(highest, expr);
        expr = expr.cdr().cdr();
      } else {
        final ArcObject current = expr.car();
        expr = expr.cdr();
        if (isUnQuoteSplicing(current)) {
          highest = highestLexicalScopeReference(highest, current);
        } else if (isUnQuote(current) || isPair(current)) {
          highest = highestLexicalScopeReference(highest, current);
        }
      }
    }

    return highest;
  }

  public static void addInstructions(List i, ArcObject target) {
    addInstructions(i, target, 1);
  }

  private static void addInstructions(List i, ArcObject expr, int nesting) {
    if (isUnQuote(expr)) {
      if (nesting == 1) {
        expr.cdr().car().addInstructions(i);
      } else {
        i.add(new Literal(UNQUOTE));
        addInstructions(i, expr.cdr().car(), nesting - 1);
        i.add(new Listify(2));
      }
      return;

    } else if (isUnQuoteSplicing(expr)) {
      if (nesting == 1) {
        expr.cdr().car().addInstructions(i);
        i.add(new AppendAll());
      } else {
        i.add(new Literal(UNQUOTE_SPLICING));
        addInstructions(i, expr.cdr().car(), nesting - 1);
        i.add(new Listify(2));
        i.add(new Append());
      }
      return;

    } else if (isQuasiQuote(expr)) {
      i.add(new Literal(QUASIQUOTE));
      addInstructions(i, expr.cdr().car(), nesting + 1);
      i.add(new Listify(2));
      return;

    } else if (expr.isNotPair()) {
      i.add(new Literal(expr));
      return;
    }

    i.add(new NewList());

    while (!expr.isNotPair()) {
      if (isUnQuote(expr) || isQuasiQuote(expr)) { // catch post-dot unquotes
        addInstructions(i, expr, nesting);
        expr = expr.cdr().cdr();
        i.add(new AppendDot());
      } else {
        final ArcObject current = expr.car();
        expr = expr.cdr();
        if (isUnQuoteSplicing(current)) {
          addInstructions(i, current, nesting);
        } else if (isUnQuote(current) || isQuasiQuote(current) || isPair(current)) {
            addInstructions(i, current, nesting);
            i.add(new Append());
        } else {
          i.add(new Literal(current));
          i.add(new Append());
        }
      }
    }

    if (!(expr instanceof Nil)) {
      i.add(new Literal(expr));
      i.add(new AppendDot());
    }

    i.add(new FinishList());
  }

  public static boolean isQQPair(ArcObject expression, Symbol car) {
    return (expression instanceof Pair) && expression.isCar(car) && (expression.cdr().cdr() instanceof Nil);
  }

  public static boolean isUnQuote(ArcObject expression) {
    return isQQPair(expression, UNQUOTE);
  }

  public static boolean isUnQuoteSplicing(ArcObject expression) {
    return isQQPair(expression, UNQUOTE_SPLICING);
  }

  public static boolean isQuasiQuote(ArcObject expression) {
    return isQQPair(expression, QUASIQUOTE);
  }

  private static boolean isPair(ArcObject expression) {
    return expression instanceof Pair;
  }

  public boolean assigns(int nesting) {
    for (ArcObject o : unquotes()) {
      if (o.assigns(nesting)) {
        return true;
      }
    }
    return false;
  }

  public boolean hasClosures() {
    for (ArcObject o : unquotes()) {
      if (o instanceof InterpretedFunction) {
        if (((InterpretedFunction)o).requiresClosure()) {
          return true;
        }
      } else if (o.hasClosures()) {
        return true;
      }
    }
    return false;
  }

  public int countReferences(int refs, BoundSymbol p) {
    for (ArcObject o : unquotes()) {
      refs = o.countReferences(refs, p);
    }
    return refs;
  }

  public ArcObject inline(BoundSymbol p, ArcObject arg, boolean unnest, int lexicalNesting, int paramIndex) {
    return new QuasiQuotation(inline(p, arg, unnest, lexicalNesting, paramIndex, target, 1));
  }

  private static ArcObject inline(BoundSymbol p, ArcObject arg, boolean unnest, int lexicalNesting, int paramIndex, ArcObject expr, int nesting) {
    if (isUnQuote(expr)) {
      if (nesting == 1) {
        return Pair.buildFrom(UNQUOTE, expr.cdr().car().inline(p, arg, unnest, lexicalNesting, paramIndex));
      } else {
        return Pair.buildFrom(UNQUOTE, inline(p, arg, unnest, lexicalNesting, paramIndex, expr.cdr().car(), nesting - 1));
      }

    } else if (isUnQuoteSplicing(expr)) {
      if (nesting == 1) {
        return Pair.buildFrom(UNQUOTE_SPLICING, expr.cdr().car().inline(p, arg, unnest, lexicalNesting, paramIndex));
      } else {
        return Pair.buildFrom(UNQUOTE_SPLICING, inline(p, arg, unnest, lexicalNesting, paramIndex, expr.cdr().car(), nesting - 1));
      }

    } else if (isQuasiQuote(expr)) {
      return Pair.buildFrom(QUASIQUOTE, inline(p, arg, unnest, lexicalNesting, paramIndex, expr.cdr().car(), nesting + 1));

    } else if (expr.isNotPair()) {
      return expr;
    }

    List list = new ArrayList();
    ArcObject last = NIL;

    while (!expr.isNotPair()) {
      if (isUnQuote(expr) || isQuasiQuote(expr)) { // catch post-dot unquotes
        last = inline(p, arg, unnest, lexicalNesting, paramIndex, expr, nesting);
        expr = expr.cdr().cdr();
      } else {
        final ArcObject current = expr.car();
        expr = expr.cdr();
        if (isUnQuoteSplicing(current)) {
          list.add(inline(p, arg, unnest, lexicalNesting, paramIndex, current, nesting));
        } else if (isUnQuote(current) || isQuasiQuote(current) || isPair(current)) {
          list.add(inline(p, arg, unnest, lexicalNesting, paramIndex, current, nesting));
        } else {
          list.add(current);
        }
      }
    }

    if (!(expr instanceof Nil)) {
      last = expr;
    }

    return Pair.buildFrom(list, last);
  }

  public ArcObject nest(int threshold) {
    return nest(threshold, target, 1);
  }

  private static ArcObject nest(int threshold, ArcObject expr, int nesting) {
    if (isUnQuote(expr)) {
      if (nesting == 1) {
        return Pair.buildFrom(UNQUOTE, expr.cdr().car().nest(threshold));
      } else {
        return Pair.buildFrom(UNQUOTE, nest(threshold, expr.cdr().car(), nesting - 1));
      }

    } else if (isUnQuoteSplicing(expr)) {
      if (nesting == 1) {
        return Pair.buildFrom(UNQUOTE_SPLICING, expr.cdr().car().nest(threshold));
      } else {
        return Pair.buildFrom(UNQUOTE_SPLICING, nest(threshold, expr.cdr().car(), nesting - 1));
      }

    } else if (isQuasiQuote(expr)) {
      return Pair.buildFrom(QUASIQUOTE, nest(threshold, expr.cdr().car(), nesting + 1));

    } else if (expr.isNotPair()) {
      return expr;
    }

    List list = new ArrayList();
    ArcObject last = NIL;

    while (!expr.isNotPair()) {
      if (isUnQuote(expr) || isQuasiQuote(expr)) { // catch post-dot unquotes
        last = nest(threshold, expr, nesting);
        expr = expr.cdr().cdr();
      } else {
        final ArcObject current = expr.car();
        expr = expr.cdr();
        if (isUnQuoteSplicing(current) || isUnQuote(current) || isQuasiQuote(current) || isPair(current)) {
          list.add(nest(threshold, current, nesting));
        } else {
          list.add(current);
        }
      }
    }

    if (!(expr instanceof Nil)) {
      last = expr;
    }

    return Pair.buildFrom(list, last);
  }

}
