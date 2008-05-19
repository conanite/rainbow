package rainbow.types;

import rainbow.ArcError;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Arrays;

public class JavaObject extends ArcObject {
  public static final Symbol TYPE = (Symbol) Symbol.make("java-object");
  private Object object;

  public JavaObject(Object object) {
    this.object = object;
  }

  public static JavaObject instantiate(String className) {
    try {
      Class target = Class.forName(className);
      return new JavaObject(target.newInstance());
    } catch (Exception e) {
      throw new ArcError("Can't instantiate class " + className + " : " + e.getMessage(), e);
    }
  }

  public ArcObject type() {
    return TYPE;
  }

  public Object unwrap() {
    return object;
  }

  public static ArcObject getClassInstance(String className) {
    try {
      return new JavaObject(Class.forName(className));
    } catch (ClassNotFoundException e) {
      throw new ArcError("Can't find class " + className + " : " + e.getMessage(), e);
    }
  }

  public static JavaObject cast(ArcObject argument, Object caller) {
    try {
      return (JavaObject) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a java-object, got " + argument);
    }
  }

  public Object invoke(String methodName) {
    try {
      Method method = object.getClass().getMethod(methodName);
      return method.invoke(object);
    } catch (NoSuchMethodException e) {
      throw new ArcError("No method " + methodName + " exists on " + object.getClass(), e);
    } catch (IllegalAccessException e) {
      throw new ArcError("Method " + methodName + " is not accessible on " + object.getClass(), e);
    } catch (InvocationTargetException e) {
      throw new ArcError(e.getCause().getMessage(), e);
    }
  }

  public Object getStaticFieldValue(String fieldName) {
    Class c = (Class) object;
    try {
      Field f = c.getField(fieldName);
      return f.get(null);
    } catch (NoSuchFieldException e) {
      throw new ArcError("No field " + fieldName + " exists on " + object, e);
    } catch (IllegalAccessException e) {
      throw new ArcError("Field " + fieldName + " is not accessible on " + object, e);
    }
  }

  public Object staticInvoke(String methodName, Pair args) {
    Class c = (Class) object;
    try {
      Method m = findMethod(c, methodName, args.size());
      Object[] javaArgs = unwrapList(args, m.getParameterTypes());
      return m.invoke(object, javaArgs);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Object[] unwrapList(Pair args, Class[] parameterTypes) {
    Object[] result = new Object[parameterTypes.length];
    unwrapList(result, parameterTypes, args, 0);
    return result;
  }

  private void unwrapList(Object[] result, Class<?>[] parameterTypes, Pair args, int i) {
    result[i] = unwrap(args.car(), parameterTypes[i]);
    if (!args.cdr().isNil()) {
      unwrapList(result, parameterTypes, (Pair) args.cdr(), i + 1);
    }
  }

  private Object unwrap(ArcObject arcObject, Class javaType) {
    if (javaType.isAssignableFrom(arcObject.getClass())) {
      return arcObject;
    } else {
      return convert(arcObject.unwrap(), javaType);
    }
  }

  private Object convert(Object o, Class javaType) {
    if (javaType == Integer.class || javaType == Integer.TYPE) {
      return ((Long)o).intValue();
    } else if (javaType == Long.class || javaType == Long.TYPE || javaType == Double.class || javaType == Double.TYPE) {
      return o;
    } else if (javaType == Float.class || javaType == Float.TYPE) {
      return ((Double)o).floatValue();
    } else {
      return o;
    }
  }

  private static Method findMethod(Class c, String methodName, int argCount) {
    for (int i = 0; i < c.getMethods().length; i++) {
      Method method = c.getMethods()[i];
      if (method.getName().equals(methodName) && method.getParameterTypes().length == argCount) {
        return method;
      }
    }
    throw new ArcError("no method " + methodName + " found on " + c + " with " + argCount + " parameters");
  }
}
