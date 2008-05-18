package rainbow.functions;

import rainbow.*;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.Interpreter;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.types.Hash;

import java.util.List;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class Evaluation {
  public static boolean isSpecialSyntax(ArcObject expression) {
    return expression instanceof Symbol && SSyntax.isSpecial((Symbol) expression);
  }

  public static ArcObject ssExpand(ArcObject expression) {
    return SSExpand.expand(Symbol.cast(expression, "ssexpand"));
  }

  public static class Apply extends Builtin {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation whatToDo, Pair args) {
      Function f = (Function) args.car();
      Pair applyArgs = constructApplyArgs((Pair) args.cdr());
      f.invoke(thread, lc, whatToDo, applyArgs);
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
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
      Interpreter.compileAndEval(thread, lc, caller, args.car());
    }
  }
  
  public static class Seval extends Builtin {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
      caller.receive(new Hash());
    }
  }

  public static class SSyntax extends Builtin {
    public ArcObject invoke(Pair args) {
      return (args.car() instanceof Symbol && isSpecial((Symbol) args.car())) ? T : NIL;
    }

    public static boolean isSpecial(Symbol symbol) {
      return isComposeComplement(symbol.name()) || isListListQuoted(symbol.name());
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
    return symbol.contains(":") || symbol.contains("~");
  }

  public static class SSExpand extends Builtin {
    public ArcObject invoke(Pair args) {
      return expand(Symbol.cast(args.car(), this));
    }

    public static ArcObject expand(Symbol s) {
      String symbol = s.name();
      if (isComposeComplement(symbol)) {
        return expandCompose(symbol);
      } else if (isListListQuoted(symbol)) {
        return expandExpression(symbol);
      } else {
        throw new ArcError("Unknown syntax " + symbol);
      }
    }

    private static ArcObject expandExpression(String symbol) {
      StringTokenizer tokens = new StringTokenizer(symbol, ".!", true);
      List list = new LinkedList();
      String delim = ".";
      String sym = tokens.nextToken();
      list.add(possiblyQuote(sym, delim));
      while (tokens.hasMoreTokens()) {
        delim = tokens.nextToken();
        sym = tokens.nextToken();
        list.add(possiblyQuote(sym, delim));
      }
      return Pair.buildFrom(list, NIL);
    }

    private static ArcObject possiblyQuote(String sym, String delim) {
      return ".".equals(delim) ? Symbol.make(sym) : Pair.buildFrom(Symbol.make("quote"), Symbol.make(sym));
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
