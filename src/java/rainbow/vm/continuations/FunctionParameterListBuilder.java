package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FunctionParameterListBuilder extends ContinuationSupport {
  private static final Symbol O = (Symbol) Symbol.make("o");
  private ArcObject parameters;
  private Map[] lexicalBindings;
  List result = new LinkedList();
  Map indexed = new HashMap();
  private ArcObject optionalParamName;

  public FunctionParameterListBuilder(ArcThread thread, LexicalClosure lc, FunctionBodyBuilder caller, ArcObject parameters, Map[] lexicalBindings) {
    super(thread, lc, caller);
    this.parameters = parameters;
    this.lexicalBindings = lexicalBindings;
    index(parameters, lexicalBindings[0], new int[]{0}, true);
  }

  public void start() {
    if (parameters.isNil() || !(parameters instanceof Pair)) {
      if (result.size() == 0) {
        caller.receive(parameters);
      } else {
        Pair compiledParams = Pair.buildFrom(result, parameters);
        caller.receive(compiledParams);
      }
      return;
    }

    ArcObject first = parameters.car();
    parameters = parameters.cdr();
    if (!(first instanceof Pair)) {
      continueWith(first);
    } else {
      Pair maybeOptional = (Pair) first;
      if (NamespaceBuilder.optional(maybeOptional)) {
        optionalParamName = maybeOptional.cdr().car();
        Compiler.compile(thread, lc, this, maybeOptional.cdr().cdr().car(), lexicalBindings);
      } else {
        continueWith(first);
      }
    }
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
