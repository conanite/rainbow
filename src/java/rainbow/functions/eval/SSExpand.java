package rainbow.functions.eval;

import rainbow.functions.Builtin;
import rainbow.functions.Evaluation;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.ArcError;
import rainbow.parser.ArcParser;
import rainbow.parser.ParseException;

import java.util.*;

public class SSExpand extends Builtin {
  public static final String ANDF_INTRASYM = "&";
  public static final char ANDF_INTRASYM_CHAR = '&';

  public SSExpand() {
    super("ssexpand");
  }

  public ArcObject invoke(Pair args) {
    return expand(Symbol.cast(args.car(), this));
  }

  public static ArcObject expand(Symbol s) {
    String symbol = s.name();
    if (Evaluation.isComposeComplement(symbol)) {
      return expandCompose(symbol);
    } else if (Evaluation.isAndf(symbol)) {
      return expandAndf(symbol);
    } else if (Evaluation.isListListQuoted(symbol)) {
      return expandExpression(symbol);
    } else {
      throw new ArcError("Unknown syntax " + symbol);
    }
  }

  private static ArcObject expandAndf(String symbol) {
    List toks = new ArrayList();
    toks.add(Symbol.mkSym("andf"));
    String[] tokenised = andToks(symbol);
    for (String s : tokenised) {
      toks.add(readValue((Symbol) Symbol.make(s)));
    }
    return Pair.buildFrom(toks);
  }

  private static String[] andToks(String symbol) {
    if (symbol.length() == 0) {
      return new String[] { "" };
    } else if (symbol.charAt(0) == ANDF_INTRASYM_CHAR) {
      String[] result = andToks(symbol.substring(1));
      result[0] = ANDF_INTRASYM + result[0];
      return result;
    } else if (symbol.charAt(symbol.length() - 1) == ANDF_INTRASYM_CHAR) {
      String[] result = andToks(symbol.substring(0, symbol.length() - 1));
      result[result.length - 1] = result[result.length - 1] + ANDF_INTRASYM;
      return result;
    } else {
      return symbol.split(ANDF_INTRASYM);
    }
  }

  private static ArcObject expandToks(Iterator list) {
    ArcObject s = readValue((Symbol) list.next());
    Symbol sep = null;
    if (list.hasNext()) {
      sep = (Symbol) list.next();
    }

    ArcObject next = sep == Symbol.BANG ? Pair.buildFrom(Symbol.mkSym("quote"), s) : s;
    if (list.hasNext()) {
      return Pair.buildFrom(expandToks(list), next);
    } else if (sep != null) {
      return Pair.buildFrom(Symbol.mkSym("get"), next);
    } else {
      return next;
    }
  }

  private static ArcObject readValue(String s) {
    try {
      return new ArcParser(s).expression();
    } catch (ParseException e) {
      throw new ArcError("Couldn't read value of symbol: " + s, e);
    }
  }

  private static ArcObject readValue(Symbol symbol) {
    return readValue(symbol.name());
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
      list.add(0, Symbol.mkSym("#<eof>"));
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
    list.add(Symbol.mkSym("compose"));
    for (String element : elements) {
      list.add(possiblyComplement(element));
    }
    return Pair.buildFrom(list, NIL);
  }

  private static ArcObject possiblyComplement(String element) {
    return element.startsWith("~") ? Pair.buildFrom(Symbol.mkSym("complement"), readValue(element.substring(1))) : readValue(element);
  }
}
