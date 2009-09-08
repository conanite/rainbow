package rainbow.functions.interpreted;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.compiler.FunctionBodyBuilder;
import rainbow.vm.compiler.FunctionParameterListBuilder;
import rainbow.vm.interpreter.Quotation;
import rainbow.vm.interpreter.StackSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class StackFunctionSupport extends InterpretedFunction {
  protected StackFunctionSupport(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
  }

  public boolean canInline(Symbol param, ArcObject arg) {
    return  inlineableArg(param, arg)
            && !assigns(0);
  }

  private boolean inlineableArg(Symbol param, ArcObject arg) {
    Integer paramIndex = lexicalBindings.get(param);
    StackSymbol p = new StackSymbol(param, paramIndex);
    return (arg.literal())
            || (arg instanceof Quotation)
            || (arg instanceof Symbol)
            || (body.length == 1 && body[0] instanceof StackSymbol && p.isSameStackSymbol((StackSymbol) body[0]));
  }

  public ArcObject curry(Symbol param, ArcObject arg, boolean requiresNesting) {
    Integer paramIndex = lexicalBindings.get(param);
    StackSymbol p = new StackSymbol(param, paramIndex);
    ArcObject newParams = FunctionParameterListBuilder.curry(parameterList, p, arg, paramIndex);
    Map lexicalBindings = new HashMap();
    FunctionParameterListBuilder.index(newParams, lexicalBindings, new int[] {0}, false);

    List newBody = new ArrayList();
    for (int i = 0; i < body.length; i++) {
      newBody.add(body[i].inline(p, arg, paramIndex).reduce());
    }
    Pair nb = Pair.buildFrom(newBody);
    ArcObject complexParams = FunctionParameterListBuilder.isComplex(newParams);
    return FunctionBodyBuilder.buildStackFunctionBody(newParams, lexicalBindings, nb, complexParams);
  }

  public static Pair convert(Map lexicalBindings, ArcObject[] body) {
    ArcObject[] nb = new ArcObject[body.length];
    for (int i = 0; i < body.length; i++) {
      nb[i] = convert(lexicalBindings, body[i]);

    }
    return Pair.buildFrom(nb);
  }

  public static ArcObject convert(Map<Symbol, Integer> lexicalBindings, ArcObject item) {
    return item.replaceBoundSymbols(lexicalBindings);
  }
}
