package rainbow.vm.compiler;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;
import rainbow.vm.continuations.ContinuationSupport;
import rainbow.vm.continuations.NamespaceBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FunctionParameterListBuilder extends ContinuationSupport {
  private static final Symbol O = (Symbol) Symbol.make("o");
  private ArcObject parameters;
  private Map[] lexicalBindings;
  List result = new LinkedList();
  private ArcObject optionalParamName;
  ArcObject complexParams = ArcObject.NIL;

  public FunctionParameterListBuilder(ArcThread thread, LexicalClosure lc, FunctionBodyBuilder caller, ArcObject parameters, Map[] lexicalBindings) {
    super(thread, lc, caller);
    this.parameters = parameters;
    this.lexicalBindings = lexicalBindings;
    index(parameters, lexicalBindings[0], new int[]{0}, false);
  }

  public void start() {
    if (parameters.isNotPair()) {
      if (result.size() == 0) {
        returnParams(parameters);
      } else {
        returnParams(Pair.buildFrom(result, parameters));
      }
      return;
    }

    ArcObject first = parameters.car();
    parameters = parameters.cdr();
    if (!(first instanceof Pair)) {
      continueWith(first);
    } else {
      complexParams = ArcObject.T;
      Pair maybeOptional = (Pair) first;
      if (NamespaceBuilder.optional(maybeOptional)) {
        optionalParamName = maybeOptional.cdr().car();
        rainbow.vm.compiler.Compiler.compile(thread, lc, this, maybeOptional.cdr().cdr().car(), lexicalBindings);
      } else {
        continueWith(first);
      }
    }
  }

  private void returnParams(ArcObject params) {
    caller.receive(new Pair(complexParams, params));
  }

  private void continueWith(ArcObject first) {
    result.add(first);
    start();
  }

  protected void onReceive(ArcObject compiledOptionalExpression) {
    Pair expr = Pair.buildFrom(O, optionalParamName, compiledOptionalExpression);
    continueWith(expr);
  }

  private static void index(ArcObject parameterList, Map map, int[] i, boolean optionable) {
    if (parameterList.isNil()) {
      return;
    }

    if (parameterList instanceof Pair) {
      if (optionable && NamespaceBuilder.optional(parameterList)) {
        index(parameterList.cdr().car(), map, i, true);
      } else {
        index(parameterList.car(), map, i, true);
        index(parameterList.cdr(), map, i, false);
      }
    } else {
      map.put(parameterList, i[0]);
      i[0]++;
    }
  }
}
