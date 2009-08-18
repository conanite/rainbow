package rainbow.functions.eval;

import rainbow.functions.Builtin;
import rainbow.functions.Evaluation;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;

public class SSyntax extends Builtin {
  public SSyntax() {
    super("ssyntax");
  }

  public ArcObject invoke(Pair args) {
    return (args.car() instanceof Symbol && isSpecial((Symbol) args.car())) ? T : NIL;
  }

  public static boolean isSpecial(Symbol symbol) {
    return Evaluation.isComposeComplement(symbol.name()) || Evaluation.isAndf(symbol.name()) || Evaluation.isListListQuoted(symbol.name());
  }
}
