package rainbow.types;

import rainbow.ArcError;
import rainbow.Environment;
import rainbow.Function;
import rainbow.functions.Java;
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
    ArcThread thread = new ArcThread(environment);
    TopLevelContinuation topLevel = new TopLevelContinuation(thread);
    ArcObject methodImplementation = functions.value(Symbol.make(method.getName()));
    if (methodImplementation.isNil()) {
      throw new ArcError("No implementation provided for " + method + "; implementations include " + functions);
    }
    Function f = (Function) methodImplementation;
    f.invoke(thread, null, topLevel, (Pair) Java.wrap(arguments));
    thread.run();
    return JavaObject.unwrap(thread.finalValue(), method.getReturnType());
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
