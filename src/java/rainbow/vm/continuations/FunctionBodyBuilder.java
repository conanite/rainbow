package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.functions.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FunctionBodyBuilder extends ContinuationSupport {
  private static final Object EXPAND_BODY = new Object();
  private static final Object BUILD_BODY = new Object();
  private ArcObject parameters;
  private Pair body;
  private List result = new LinkedList();
  private Object state = EXPAND_BODY;
  private ArcObject parameterList;
  private Map[] lexicalBindings;
  private Map myParams;

  public FunctionBodyBuilder(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args, Map[] lexicalBindings) {
    super(thread, lc, caller);
    if (lexicalBindings == null) {
      throw new IllegalArgumentException("can't have null lexical bindings!");
    }
    this.lexicalBindings = lexicalBindings;
    this.parameters = args.car();
    this.body = ArcObject.cast(args.cdr(), Pair.class);
  }

  public void start() {
    myParams = new HashMap();
    if (parameters.isNil()) {
      onReceive(parameters);
    } else {
      this.lexicalBindings = concat(myParams, lexicalBindings);
      new FunctionParameterListBuilder(thread, lc, this, parameters, lexicalBindings).start();
    }
  }

  protected void onReceive(ArcObject returned) {
    if (state == EXPAND_BODY) {
      state = BUILD_BODY;
      this.parameterList = returned;
      Continuation macex = new MacExpander(thread, lc, this, false);
      new PairExpander(thread, lc, macex, body, lexicalBindings).start();
    } else {
      caller.receive(buildFunctionBody(parameterList, myParams, (Pair) returned));
    }
  }

  private Map[] concat(Map map, Map[] lexicalBindings) {
    Map[] result = new Map[lexicalBindings.length + 1];
    result[0] = map;
    System.arraycopy(lexicalBindings, 0, result, 1, lexicalBindings.length);
    return result;
  }

  public Continuation cloneFor(ArcThread thread) {
    FunctionBodyBuilder e = (FunctionBodyBuilder) super.cloneFor(thread);
    e.body = this.body.copy();
    e.result = new LinkedList(result);
    return e;
  }
  
  private ArcObject buildFunctionBody(ArcObject compiledParameters, Map lexicalBindings, Pair expandedBody) {
    return new InterpretedFunction(compiledParameters, lexicalBindings, expandedBody);
  }
}
