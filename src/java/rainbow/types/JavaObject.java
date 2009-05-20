package rainbow.types;

import rainbow.ArcError;
import rainbow.Environment;
import rainbow.Function;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class JavaObject extends ArcObject {
  public static final Symbol TYPE = (Symbol) Symbol.make("java-object");
  private Object object;

  public JavaObject(Object object) {
    this.object = object;
  }

  public static ArcObject getClassInstance(String className) {
    try {
      return new JavaObject(Class.forName(className));
    } catch (ClassNotFoundException e) {
      throw new ArcError("Can't find class " + className + " : " + e.getMessage(), e);
    }
  }

  public static JavaObject instantiate(String className, Pair args) {
    try {
      Class target = Class.forName(className);
      if (args.isNil()) {
        return new JavaObject(target.newInstance());
      } else {
        Constructor c = findConstructor(target, args);
        Object[] javaArgs = unwrapList(args, c.getParameterTypes());
        return new JavaObject(c.newInstance(javaArgs));
      }
    } catch (Exception e) {
      throw new ArcError("Can't instantiate class " + className + " : " + e.getMessage(), e);
    }
  }

  private static Constructor findConstructor(Class c, Pair args) {
    int parameterCount = args.size();
    for (int i = 0; i < c.getConstructors().length; i++) {
      Constructor constructor = c.getConstructors()[i];
      if (constructor.getParameterTypes().length == parameterCount) {
        if (match(constructor.getParameterTypes(), args, 0)) {
          return constructor; } } }
    throw new ArcError("no constructor found matching " + args + " on " + c);
  }

  public Object invoke(String methodName, Pair args) {
    return invokeMethod(object, object.getClass(), methodName, args);
  }

  public static Object staticInvoke(String className, String methodName, Pair args) {
    if (Environment.debugJava) {
      System.out.println("java: static-invoke " + methodName + " on " + className + " with args " + truncateString(args));
    }
    Class c = toClass(className);
    try {
      return invokeMethod(null, c, methodName, args);
    } catch (Exception e) {
      try {
        Field field = c.getField(methodName);
        if (!args.isNil()) {
          field.set(null, unwrap(args.car(), field.getType()));
          return null;
        } else {
          return field.get(null);
        }
      } catch (Exception e1) {
        throw new ArcError("Unable to access method or field " + methodName + " on " + className, e);
      }
    }
  }

  private static String truncateString(Object o) {
    String s = o.toString();
    if (s.length() > 100) {
      s = s.substring(0, 100);
    }
    return s;
  }

  public ArcObject type() {
    return TYPE;
  }

  public Object unwrap() {
    return object;
  }

  public String toString() {
    return object.toString();
  }

  private static Class toClass(String className) {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new ArcError("Class not found: " + className + "(" + e + ")", e);
    }
  }

  public static JavaObject cast(ArcObject argument, Object caller) {
    try {
      return (JavaObject) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a java-object, got " + argument);
    }
  }

  public static Object getStaticFieldValue(String className, String fieldName) {
    Class c = toClass(className);
    try {
      Field f = c.getField(fieldName);
      return f.get(null);
    } catch (NoSuchFieldException e) {
      throw new ArcError("No field " + fieldName + " exists on " + c, e);
    } catch (IllegalAccessException e) {
      throw new ArcError("Field " + fieldName + " is not accessible on " + c, e);
    }
  }

  private static Object invokeMethod(Object target, Class aClass, String methodName, Pair args) {
    Object[] javaArgs = new Object[0];
    Method method = null;
    try {
      method = findMethod(aClass, methodName, args);
      method.setAccessible(true);
      javaArgs = unwrapList(args, method.getParameterTypes());
      return method.invoke(target, javaArgs);
    } catch (IllegalArgumentException e) {
      System.out.println("arc args: " + args);
      System.out.println("java args: " + new ArrayList(Arrays.asList(javaArgs)));
      System.out.println("java arg types: " + javaTypes(javaArgs));
      System.out.println("method: " + method);
      System.out.println("on class: " + aClass);
      throw e;
    } catch (IllegalAccessException e) {
      throw new ArcError("Method " + methodName + " is not accessible on " + aClass, e);
    } catch (InvocationTargetException e) {
      throw new ArcError("Invoking " + methodName + " on " + target + " with args " + args + " : " + e.getCause().getMessage(), e);
    }
  }

  private static List javaTypes(Object[] javaArgs) {
    List result = new ArrayList(javaArgs.length);
    for (Object javaArg : javaArgs) {
      result.add(javaArg.getClass());
    }
    return result;
  }

  private static Object[] unwrapList(Pair args, Class[] parameterTypes) {
    Object[] result = new Object[parameterTypes.length];
    if (result.length > 0) {
      unwrapList(result, parameterTypes, args, 0);
    }
    return result;
  }

  private static void unwrapList(Object[] result, Class<?>[] parameterTypes, Pair args, int i) {
    result[i] = unwrap(args.car(), parameterTypes[i]);
    if (!args.cdr().isNil()) {
      unwrapList(result, parameterTypes, (Pair) args.cdr(), i + 1);
    }
  }

  public static Object unwrap(ArcObject arcObject, Class javaType) {
    if (javaType != Object.class && javaType.isAssignableFrom(arcObject.getClass())) {
      return arcObject;
    } else if (javaType.isInterface() && (arcObject instanceof Hash || arcObject instanceof Function)) {
      return JavaProxy.create(Pair.buildFrom(ArcString.make(javaType.getName())), arcObject, NIL).unwrap();
    } else {
      try {
        return convert(arcObject.unwrap(), javaType);
      } catch (ClassCastException e) {
        throw new ArcError("Can't convert " + typeOf(arcObject) + " - " + arcObject + " ( a " + arcObject.type() + ") to " + javaType, e);
      }
    }
  }

  private static Object convert(Object o, Class javaType) {
    if (o == Boolean.FALSE && !(javaType == Boolean.class || javaType == Boolean.TYPE)) {
      return null;
    } else if (javaType == Integer.class || javaType == Integer.TYPE) {
      return ((Long) o).intValue();
    } else if (javaType == Long.class || javaType == Long.TYPE || javaType == Double.class || javaType == Double.TYPE) {
      return o;
    } else if (javaType == Float.class || javaType == Float.TYPE) {
      return ((Number) o).floatValue();
    } else if (javaType == Boolean.class || javaType == Boolean.TYPE) {
      return o != Boolean.FALSE;
    } else if (javaType == Void.class) {
      return o == Boolean.FALSE ? null : o;
    } else {
      return o;
    }
  }

  private static final Map methodCache = new HashMap();

  private static Method findMethod(Class c, String methodName, Pair args) {
    Method m = findMethodIfPresent(c, methodName, args);
    if (m == null) {
      throw new ArcError("no method " + methodName + " found on " + c + " to accept " + args);
    } else {
      return m;
    }
  }

  private static Method findMethodIfPresent(Class c, String methodName, Pair args) {
    String key = c.getName() + methodName + types(args);
    if (methodCache.containsKey(key)) {
      return (Method) methodCache.get(key);
    }
    int argCount = args.size();
    for (int i = 0; i < c.getMethods().length; i++) {
      Method method = c.getMethods()[i];
      if (method.getName().equals(methodName) && method.getParameterTypes().length == argCount && match(method.getParameterTypes(), args, 0)) {
        methodCache.put(key, method);
        return method;
      }
    }
    return null;
  }

  private static String types(ArcObject args) {
    if (args.isNil()) {
      return "";
    } else if (args instanceof Pair) {
      return typeOf(args.car()) + types(args.cdr());
    } else {
      return typeOf(args);
    }
  }

  private static String typeOf(ArcObject obj) {
    if (obj instanceof JavaObject) {
      return obj.unwrap().getClass().getName();
    } else {
      return obj.getClass().getName();
    }
  }

  private static boolean match(Class[] parameterTypes, Pair args, int i) {
    return i == parameterTypes.length && args.isNil() || match(parameterTypes[i], args.car()) && match(parameterTypes, (Pair) args.cdr(), i + 1);
  }

  private static boolean match(Class parameterType, ArcObject arcObject) {
    if (parameterType == Boolean.class || parameterType == Boolean.TYPE) {
      return true;
    } else if (isPrimitiveNumber(parameterType) && arcObject instanceof ArcNumber) {
      return true;
    } else if (Number.class.isAssignableFrom(parameterType) && arcObject instanceof ArcNumber) {
      return true;
    } else if (Character.class.isAssignableFrom(parameterType) && arcObject instanceof ArcCharacter) {
      return true;
    } else if (parameterType == String.class && (arcObject instanceof ArcString || arcObject instanceof Symbol)) {
      return true;
    } else if (parameterType == List.class && arcObject instanceof Pair) {
      return true;
    } else if (parameterType == Map.class && arcObject instanceof Hash) {
      return true;
    } else if (!parameterType.isPrimitive() && arcObject.isNil()) {
      return true;
    } else if (parameterType.isInterface() && (arcObject instanceof Hash || arcObject instanceof Function)) {
      return true;
    } else if (parameterType.isAssignableFrom(arcObject.unwrap().getClass())) {
      return true;
    }
    return false;
  }

  private static boolean isPrimitiveNumber(Class p) {
    return p == Integer.TYPE || p == Long.TYPE || p == Double.TYPE || p == Float.TYPE;
  }
}
