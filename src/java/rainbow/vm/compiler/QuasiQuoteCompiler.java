package rainbow.vm.compiler;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.continuations.QuasiQuoteContinuation;

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

    if (QuasiQuoteContinuation.isUnQuote(expression)) {
      ArcObject compileMe = expression.cdr().car();
      ArcObject compiled;
      if (nesting == 1) {
        compiled = Compiler.compile(vm, compileMe, lexicalBindings);
      } else {
        compiled = compile(vm, compileMe, lexicalBindings, nesting - 1);
      }
      return Pair.buildFrom(UNQUOTE, compiled);

    } else if (QuasiQuoteContinuation.isUnQuoteSplicing(expression)) {
      if (nesting == 1) {
        return Pair.buildFrom(UNQUOTE_SPLICING, Compiler.compile(vm, expression.cdr().car(), lexicalBindings));
      } else {
        return Pair.buildFrom(UNQUOTE_SPLICING, compile(vm, expression.cdr().car(), lexicalBindings, nesting - 1));
      }

    } else if (QuasiQuoteContinuation.isQuasiQuote(expression)) {
      return Pair.buildFrom(QUASIQUOTE, compile(vm, expression.cdr().car(), lexicalBindings, 2));

    } else {
      LinkedList result = new LinkedList();

      while (!expression.isNotPair()) {
        ArcObject next = expression.car();
        expression = expression.cdr();
        if (next.isNotPair()) {
          result.add(next);
        } else if (QuasiQuoteContinuation.isUnQuote(next)) {
          result.add(compile(vm, next, lexicalBindings, nesting));

        } else if (QuasiQuoteContinuation.isUnQuoteSplicing(next)) {
          result.add(compile(vm, next, lexicalBindings, nesting));

        } else if (QuasiQuoteContinuation.isQuasiQuote(next)) {
          result.add(compile(vm, next, lexicalBindings, nesting + 1));

        } else {
          result.add(compile(vm, next, lexicalBindings, nesting));
        }
      }
      return Pair.buildFrom(result, compile(vm, expression, lexicalBindings, nesting));
    }
  }

}
