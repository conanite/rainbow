package rainbow.types;

import rainbow.ArcError;
import rainbow.Environment;
import rainbow.Function;
import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.vm.continuations.TopLevelContinuation;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JavaProxy implements InvocationHandler {
  private Environment environment;
  private Hash functions;

  public JavaProxy(Environment environment, Hash functions) {
    this.environment = environment;
    this.functions = functions;
  }

  public Object invoke(Object target, Method method, Object[] arguments) throws Throwable {
//    Function f = (Function) functions.value(Symbol.make(method.getName()));
//    f.invoke();
//    ArcThread thread = new ArcThread(environment);
//    TopLevelContinuation topLevel = new TopLevelContinuation(thread);
//    Interpreter.compileAndEval(thread, null, topLevel, expression);
//    thread.run();
//    return thread.finalValue();
    return null;
  }

  public static ArcObject create(Environment environment, String className, Hash functions) {
    try {
      Class target = Class.forName(className);
      return new JavaObject(Proxy.newProxyInstance(JavaProxy.class.getClassLoader(), new Class[] { target }, new JavaProxy(environment, functions)));
    } catch (ClassNotFoundException e) {
      throw new ArcError("Class " + className + " not found", e);
    }
  }
}
