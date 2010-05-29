package rainbow.types;

import rainbow.ArcError;
import rainbow.Console;
import rainbow.Nil;
import rainbow.vm.VM;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;

public class JavaProxy implements InvocationHandler {
  private Hash functions;
  private boolean strict;
  private Pair interfaces;
  private static final ArcObject WILDCARD = Symbol.mkSym("*");

  public JavaProxy(boolean strict, Hash functions, Pair interfaces) {
    this.functions = functions;
    this.strict = strict;
    this.interfaces = interfaces;
  }

  public Object invoke(Object target, Method method, Object[] arguments) throws Throwable {
    if (Console.debugJava) {
      if (arguments != null) {
        System.out.println("JavaProxy: invoking " + method + " with " + new ArrayList(Arrays.asList(arguments)));
      } else {
        System.out.println("JavaProxy: invoking " + method + " with no args");
      }
    }
    try {
      Object o = invoke2(target, method, arguments);
      if (Console.debugJava) {
        System.out.println("return value for " + method + " is " + o);
      }
      return o;
    } catch (Throwable throwable) {
      throw new ArcError("Failed to invoke " + method, throwable);
    }
  }

  public Object invoke2(Object target, Method method, Object[] arguments) throws Throwable {
    ArcObject methodImplementation = functions.value(Symbol.make(method.getName()));
    if ((methodImplementation instanceof Nil)) {
      methodImplementation = functions.value(WILDCARD);
    }

    if ((methodImplementation instanceof Nil)) {
      if (Console.debugJava) {
        System.out.println("no implementation found for " + method);
      }
      if (method.getName().equals("toString")) {
        return "Arc implementation of " + interfaces + " : " + functions.toString();
      } else if (method.getName().equals("hashCode")) {
        return this.hashCode();
      } else {
        if (strict) {
          throw new ArcError("No implementation provided for " + method + "; implementations include " + functions);
        } else {
          return null;
        }
      }
    }
    ArcObject f = methodImplementation;
    Pair args = (Pair) JavaObject.wrap(arguments);
    VM vm = new VM();
    f.invoke(vm, args);
    return JavaObject.unwrap(vm.thread(), method.getReturnType());
  }

  public static ArcObject create(Pair interfaceNames, ArcObject functions, ArcObject strict) {
    Class[] targets = new Class[interfaceNames.size()];
    getClasses(interfaceNames, targets, 0);
    if (!(functions instanceof Hash)) {
      Hash h = new Hash();
      h.sref(functions, WILDCARD);
      functions = h;
    }
    return new JavaObject(Proxy.newProxyInstance(JavaProxy.class.getClassLoader(), targets, new JavaProxy(!(strict instanceof Nil), (Hash)functions, interfaceNames)));
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
