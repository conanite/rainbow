package rainbow.functions;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.EvaluatorContinuation;

import java.util.*;

public class Evaluation {
  public static boolean isSpecialSyntax(ArcObject expression) {
    return expression instanceof Symbol && SSyntax.isSpecial((Symbol) expression);
  }

  public static ArcObject ssExpand(ArcObject expression) {
    return SSExpand.expand(Symbol.cast(expression, "ssexpand"));
  }

  public static class Apply extends Builtin {
    public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
      args.car().invoke(lc, caller, constructApplyArgs((Pair) args.cdr()));
    }

    private Pair constructApplyArgs(Pair args) {
      if (args.cdr().isNil()) {
        return Pair.cast(args.car(), this);
      } else {
        return new Pair(args.car(), constructApplyArgs(Pair.cast(args.cdr(), this)));
      }
    }
  }

  public static class Eval extends Builtin {
    public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
      EvaluatorContinuation.compileAndEval(lc, caller, args.car());
    }
  }

  public static class SSyntax extends Builtin {
    public ArcObject invoke(Pair args) {
      return (args.car() instanceof Symbol && isSpecial((Symbol) args.car())) ? T : NIL;
    }

    public static boolean isSpecial(Symbol symbol) {
      return isComposeComplement(symbol.name()) || isAndf(symbol.name()) || isListListQuoted(symbol.name());
    }
  }

  public static class UnknownSytax extends ArcError {
    public UnknownSytax(String symbol) {
      super("Unknown syntax " + symbol);
    }
  }

  private static boolean isListListQuoted(String symbol) {
    return symbol.contains(".") || symbol.contains("!");
  }

  private static boolean isComposeComplement(String symbol) {
    return isCompose(symbol) || isComplement(symbol);
  }

  private static boolean isAndf(String symbol) {
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

  public static class SSExpand extends Builtin {
    public ArcObject invoke(Pair args) {
      return expand(Symbol.cast(args.car(), this));
    }

    public static ArcObject expand(Symbol s) {
      String symbol = s.name();
      if (isComposeComplement(symbol)) {
        return expandCompose(symbol);
      } else if (isAndf(symbol)) {
        return expandAndf(symbol);
      } else if (isListListQuoted(symbol)) {
        return expandExpression(symbol);
      } else {
        throw new ArcError("Unknown syntax " + symbol);
      }
    }

    private static ArcObject expandAndf(String symbol) {
      List toks = new ArrayList();
      toks.add(Symbol.make("andf"));
      String[] tokenised = andToks(symbol);
      for (String s : tokenised) {
        toks.add(Symbol.make(s));
      }
      return Pair.buildFrom(toks);
    }

    private static String[] andToks(String symbol) {
      if (symbol.length() == 0) {
        return new String[] { "" };
      } else if (symbol.charAt(0) == '+') {
        String[] result = andToks(symbol.substring(1));
        result[0] = "+" + result[0];
        return result;
      } else if (symbol.charAt(symbol.length() - 1) == '+') {
        String[] result = andToks(symbol.substring(0, symbol.length() - 1));
        result[result.length - 1] = result[result.length - 1] + "+";
        return result;
      } else {
        return symbol.split("\\+");
      }
    }

    private static ArcObject expandToks(Iterator list) {
      Symbol s = (Symbol) list.next();
      Symbol sep = null;
      if (list.hasNext()) {
        sep = (Symbol) list.next();
      }

      ArcObject next = sep == Symbol.BANG ? Pair.buildFrom(Symbol.make("quote"), s) : s;
      if (list.hasNext()) {
        return Pair.buildFrom(expandToks(list), next);
      } else if (sep != null) {
        return Pair.buildFrom(Symbol.make("get"), next);
      } else {
        return next;
      }
    }

    private static ArcObject expandExpression(String symbol) {
      StringTokenizer tokens = new StringTokenizer(symbol, ".!", true);
      List list = new LinkedList();
      boolean wasSep = false;

      while (tokens.hasMoreTokens()) {
        ArcObject sym = Symbol.make(tokens.nextToken());
        if (isSpecialListSyntax(sym)) {
          if (wasSep) {
            throw new ArcError("Bad syntax " + symbol);
          } else {
            wasSep = true;
          }
        } else {
          wasSep = false;
        }
        list.add(0, sym);
      }

      if (wasSep) {
        list.add(0, Symbol.make("#<eof>"));
      }

      return expandToks(list.iterator());
    }

    private static boolean isSpecialListSyntax(ArcObject sym) {
      return sym == Symbol.DOT || sym == Symbol.BANG;
    }

    private static ArcObject expandCompose(String symbol) {
      String[] elements = symbol.split(":");
      if (elements.length == 1) {
        return possiblyComplement(elements[0]);
      }
      List list = new LinkedList();
      list.add(Symbol.make("compose"));
      for (String element : elements) {
        list.add(possiblyComplement(element));
      }
      return Pair.buildFrom(list, NIL);
    }

    private static ArcObject possiblyComplement(String element) {
      return element.startsWith("~") ? Pair.buildFrom(Symbol.make("complement"), Symbol.make(element.substring(1))) : Symbol.make(element);
    }
  }
}
