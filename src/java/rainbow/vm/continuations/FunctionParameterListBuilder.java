package rainbow.vm.continuations;

import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.Bindings;
import rainbow.functions.InterpretedFunction;
import rainbow.types.Pair;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;

import java.util.List;
import java.util.LinkedList;

public class FunctionParameterListBuilder extends ContinuationSupport {
  private static final Symbol O = (Symbol) Symbol.make("o");
  private Pair expandedBody;
  private ArcObject parameters;
  List result = new LinkedList();
  private ArcObject optionalParamName;

  public FunctionParameterListBuilder(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair expandedBody, ArcObject parameters) {
    super(thread, namespace, whatToDo);
    this.expandedBody = expandedBody;
    this.parameters = parameters;
  }

  public void start() {
    if (parameters.isNil() || !(parameters instanceof Pair)) {
      if (result.size() == 0) {
        whatToDo.eat(buildFunctionBody(parameters, expandedBody));
      } else {
        whatToDo.eat(buildFunctionBody(Pair.buildFrom(result, parameters), expandedBody));
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
        Compiler.compile(thread, namespace, this, maybeOptional.cdr().cdr().car());
      } else {
        continueWith(first);
      }
    }
  }

  private void continueWith(ArcObject first) {
    result.add(first);
    start();
  }

  protected void digest(ArcObject compiledOptionalExpression) {
    continueWith(Pair.buildFrom(O, optionalParamName, compiledOptionalExpression));
  }

  private ArcObject buildFunctionBody(ArcObject compiledParameters, Pair expandedBody) {
    return new InterpretedFunction(compiledParameters, expandedBody, namespace);
  }

}
