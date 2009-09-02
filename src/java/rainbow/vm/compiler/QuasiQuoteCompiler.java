package rainbow.vm.compiler;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.interpreter.QuasiQuotation;

import java.util.LinkedList;
import java.util.Map;

public class QuasiQuoteCompiler {
  public static final Symbol QUASIQUOTE = Symbol.mkSym("quasiquote");
  public static final Symbol UNQUOTE = Symbol.mkSym("unquote");
  public static final Symbol UNQUOTE_SPLICING = Symbol.mkSym("unquote-splicing");

  public static ArcObject compile(VM vm, ArcObject expression, Map[] lexicalBindings, int nesting) {
    if (expression.isNotPair()) {
      return expression;
    }

    if (QuasiQuotation.isUnQuote(expression)) {
      return compileUnquote(vm, UNQUOTE, expression, nesting, lexicalBindings);

    } else if (QuasiQuotation.isUnQuoteSplicing(expression)) {
      return compileUnquote(vm, UNQUOTE_SPLICING, expression, nesting, lexicalBindings);

    } else if (QuasiQuotation.isQuasiQuote(expression)) {
      return Pair.buildFrom(QUASIQUOTE, compile(vm, expression.cdr().car(), lexicalBindings, nesting + 1));

    } else {
      LinkedList result = new LinkedList();

      while (!expression.isNotPair()) {
        ArcObject next = expression.car();
        expression = expression.cdr();
        if (next.isNotPair()) {
          result.add(next);
        } else {
          result.add(compile(vm, next, lexicalBindings, nesting));
        }
      }

      return Pair.buildFrom(result, compile(vm, expression, lexicalBindings, nesting));
    }
  }

  private static ArcObject compileUnquote(VM vm, Symbol prefix, ArcObject expression, int nesting, Map[] lexicalBindings) {
    ArcObject compileMe = expression.cdr().car();
    ArcObject compiled;
    if (nesting == 1) {
      compiled = Compiler.compile(vm, compileMe, lexicalBindings);
    } else {
      compiled = compile(vm, compileMe, lexicalBindings, nesting - 1);
    }
    return Pair.buildFrom(prefix, compiled);
  }

}
