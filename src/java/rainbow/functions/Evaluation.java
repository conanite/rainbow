package rainbow.functions;

import rainbow.ArcError;
import rainbow.functions.eval.SSyntax;
import rainbow.functions.eval.SSExpand;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;

public class Evaluation {
  public static boolean isSpecialSyntax(ArcObject expression) {
    return expression instanceof Symbol && SSyntax.isSpecial((Symbol) expression);
  }

  public static ArcObject ssExpand(ArcObject expression) {
    return SSExpand.expand(Symbol.cast(expression, "ssexpand"));
  }

  public static class UnknownSytax extends ArcError {
    public UnknownSytax(String symbol) {
      super("Unknown syntax " + symbol);
    }
  }

  public static boolean isListListQuoted(String symbol) {
    return symbol.contains(".") || symbol.contains("!");
  }

  public static boolean isComposeComplement(String symbol) {
    return isCompose(symbol) || isComplement(symbol);
  }

  public static boolean isAndf(String symbol) {
    if (symbol.length() == 0) {
      return false;
    } else if (symbol.charAt(0) == '+') {
      return isAndf(symbol.substring(1));
    } else if (symbol.charAt(symbol.length() - 1) == '+') {
      return isAndf(symbol.substring(0, symbol.length() - 1));
    } else {
      return symbol.indexOf("+") != -1;
    }
  }

  private static boolean isComplement(String symbol) {
    return symbol.startsWith("~");
  }

  private static boolean isCompose(String symbol) {
    return symbol.contains(":");
  }

}
