package rainbow.functions;

import rainbow.*;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.Interpreter;
import rainbow.vm.continuations.FunctionDispatcher;
import rainbow.types.*;

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
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
      ArcObject fn = args.car();
      args = constructApplyArgs((Pair) args.cdr());
      if (fn instanceof Function) {
        ((Function) fn).invoke(thread, lc, caller, args);
      } else if (Console.ARC2_COMPATIBILITY) {
        arc2CompatibleTypeDispatch(thread, lc, caller, args, fn);
      } else if (Console.ANARKI_COMPATIBILITY) {
        anarkiCompatibleTypeDispatch(thread, lc, caller, args, fn);
      } else {
        throw new ArcError("Expected a function : got " + fn + " with args " + args);
      }
    }

    private void anarkiCompatibleTypeDispatch(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args, ArcObject fn) {
      Hash dispatchers = (Hash) thread.environment().lookup(FunctionDispatcher.TYPE_DISPATCHER_TABLE);
      try {
        ArcObject targetObject = Tagged.rep(fn);
        Function function = Builtin.cast(dispatchers.value(fn.type()), this);
        function.invoke(thread, lc, caller, new Pair(targetObject, args));
      } catch (NullPointerException e) {
        if (dispatchers == null) {
          throw new ArcError("call* table not found in environment!");
        } else {
          throw e;
        }
      }
    }

    private void arc2CompatibleTypeDispatch(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args, ArcObject fn) {
      if (fn instanceof Pair) {
        Pair.REF.invoke(thread, lc, caller, new Pair(fn, args));
      } else if (fn instanceof ArcString) {
        ArcString.REF.invoke(thread, lc, caller, new Pair(fn, args));
      } else if (fn instanceof Hash) {
        Hash.REF.invoke(thread, lc, caller, new Pair(fn, args));
      } else {
        throw new ArcError("Expected a function, cons, hash, or string : got " + fn + " with args " + args);
      }
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
