package rainbow.vm.continuations;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import static rainbow.vm.compiler.QuasiQuoteCompiler.*;
import rainbow.vm.instructions.*;

import java.util.List;

public class QuasiQuoteContinuation {

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

    if (!expr.isNil()) {
      i.add(new Literal(expr));
      i.add(new AppendDot());
    }

    i.add(new FinishList());
  }

  public static boolean isQQPair(ArcObject expression, Symbol car) {
    return (expression instanceof Pair) && expression.isCar(car) && expression.cdr().cdr().isNil();
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
}
