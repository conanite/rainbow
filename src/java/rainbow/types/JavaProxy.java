package rainbow.types;

import rainbow.ArcError;
import rainbow.Environment;
import rainbow.Function;
import rainbow.functions.Java;
import rainbow.vm.ArcThread;
import rainbow.vm.continuations.TopLevelContinuation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;

public class JavaProxy implements InvocationHandler {
  private Hash functions;
  private boolean strict;
  private Pair interfaces;
  private static final ArcObject WILDCARD = Symbol.make("*");

  public JavaProxy(boolean strict, Hash functions, Pair interfaces) {
    this.functions = functions;
    this.strict = strict;
    this.interfaces = interfaces;
  }

  public Object invoke(Object target, Method method, Object[] arguments) throws Throwable {
    if (Environment.debugJava) {
      if (arguments != null) {
        System.out.println("JavaProxy: invoking " + method + " with " + new ArrayList(Arrays.asList(arguments)));
      } else {
        System.out.println("JavaProxy: invoking " + method + " with no args");
      }
    }
    try {
      Object o = invoke2(target, method, arguments);
      if (Environment.debugJava) {
        System.out.println("return value for " + method + " is " + o);
      }
      return o;
    } catch (Throwable throwable) {
      throw new ArcError("Failed to invoke " + method, throwable);
    }
  }

  public Object invoke2(Object target, Method method, Object[] arguments) throws Throwable {
    ArcThread thread = new ArcThread();
    TopLevelContinuation topLevel = new TopLevelContinuation(thread);
    ArcObject methodImplementation = functions.value(Symbol.make(method.getName()));
    if (methodImplementation.isNil()) {
      methodImplementation = functions.value(WILDCARD);
    }

    if (methodImplementation.isNil()) {
      if (Environment.debugJava) {
        System.out.println("no implementation found for " + method);
      }
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
    Function f = methodImplementation;
    Pair args = (Pair) Java.wrap(arguments);
    f.invoke(null, topLevel, args);
    thread.run();
    return JavaObject.unwrap(thread.finalValue(), method.getReturnType());
  }

  public static ArcObject create(Pair interfaceNames, ArcObject functions, ArcObject strict) {
    Class[] targets = new Class[interfaceNames.size()];
    getClasses(interfaceNames, targets, 0);
    if (!(functions instanceof Hash)) {
      Hash h = new Hash();
      h.sref(WILDCARD, functions);
      functions = h;
    }
    return new JavaObject(Proxy.newProxyInstance(JavaProxy.class.getClassLoader(), targets, new JavaProxy(!strict.isNil(), (Hash)functions, interfaceNames)));
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
