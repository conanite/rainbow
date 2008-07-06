package rainbow.types;

import rainbow.ArcError;
import rainbow.Environment;
import rainbow.Function;
import rainbow.functions.Java;
import rainbow.vm.ArcThread;
import rainbow.vm.continuations.TopLevelContinuation;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JavaProxy implements InvocationHandler {
  private Environment environment;
  private Hash functions;
  private boolean strict;
  private Pair interfaces;

  public JavaProxy(Environment environment, boolean strict, Hash functions, Pair interfaces) {
    this.environment = environment;
    this.functions = functions;
    this.strict = strict;
    this.interfaces = interfaces;
  }

  public Object invoke(Object target, Method method, Object[] arguments) throws Throwable {
    ArcThread thread = new ArcThread(environment);
    TopLevelContinuation topLevel = new TopLevelContinuation(thread);
    ArcObject methodImplementation = functions.value(Symbol.make(method.getName()));
    if (methodImplementation.isNil()) {
      if (method.getName().equals("toString")) {
        return "Arc implementation of " + interfaces + " : " + functions.toString();
      } else {
        if (strict) {
          throw new ArcError("No implementation provided for " + method + "; implementations include " + functions);
        } else {
          return null;
        }
      }
    }
    Function f = (Function) methodImplementation;
    Pair args = (Pair) Java.wrap(arguments);
    f.invoke(thread, null, topLevel, args);
    thread.run();
    return JavaObject.unwrap(thread.finalValue(), method.getReturnType());
  }

  public static ArcObject create(Environment environment, Pair interfaceNames, Hash functions, ArcObject strict) {
    Class[] targets = new Class[interfaceNames.size()];
    getClasses(interfaceNames, targets, 0);
    return new JavaObject(Proxy.newProxyInstance(JavaProxy.class.getClassLoader(), targets, new JavaProxy(environment, !strict.isNil(), functions, interfaceNames)));
  }

  private static void getClasses(Pair interfaceNames, Class[] classes, int i) {
    ArcString className = ArcString.cast(interfaceNames.car(), JavaProxy.class);
    try {
      classes[i] = Class.forName(className.value());
    } catch (ClassNotFoundException e) {
      throw new ArcError("Class " + className.value() + " not found", e);
    }
    if ((++i) < classes.length) {
      getClasses((Pair) interfaceNames.cdr(), classes, i);
    }
  }
}
