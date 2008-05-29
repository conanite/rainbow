package rainbow.types;

import rainbow.ArcError;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
          return constructor;
        }
      }
    }
    throw new ArcError("no constructor found matching " + args + " on " + c);
  }

  public Object invoke(String methodName, Pair args) {
    return invokeMethod(object, object.getClass(), methodName, args);
  }

  public static Object staticInvoke(String className, String methodName, Pair args) {
    return invokeMethod(null, toClass(className), methodName, args);
  }

  public ArcObject type() {
    return TYPE;
  }

  public Object unwrap() {
    return object;
  }

  public String toString() {
    return TYPE + ":" + object;
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
    try {
      Method method = findMethod(aClass, methodName, args.size());
      Object[] javaArgs = unwrapList(args, method.getParameterTypes());
      return method.invoke(target, javaArgs);
    } catch (IllegalAccessException e) {
      throw new ArcError("Method " + methodName + " is not accessible on " + aClass, e);
    } catch (InvocationTargetException e) {
      throw new ArcError("Invoking " + methodName + " on " + target + " with args " + args + " : " + e.getCause().getMessage(), e);
    }
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
    } else {
      return convert(arcObject.unwrap(), javaType);
    }
  }

  private static Object convert(Object o, Class javaType) {
    if (javaType == Integer.class || javaType == Integer.TYPE) {
      return ((Long) o).intValue();
    } else if (javaType == Long.class || javaType == Long.TYPE || javaType == Double.class || javaType == Double.TYPE) {
      return o;
    } else if (javaType == Float.class || javaType == Float.TYPE) {
      return ((Double) o).floatValue();
    } else if (javaType == Boolean.class) {
      return o != Boolean.FALSE;
    } else if (javaType == Void.class) {
      return o == Boolean.FALSE ? null : o;
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

  private static boolean match(Class[] parameterTypes, Pair args, int i) {
    return i == parameterTypes.length && args.isNil() || match(parameterTypes[i], args.car()) && match(parameterTypes, (Pair) args.cdr(), i + 1);
  }

  private static boolean match(Class parameterType, ArcObject arcObject) {
    if (parameterType == Boolean.class) {
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
    } else if (parameterType.isAssignableFrom(arcObject.unwrap().getClass())) {
      return true;
    }
    return false;
  }

  private static boolean isPrimitiveNumber(Class p) {
    return p == Integer.TYPE || p == Long.TYPE || p == Double.TYPE || p == Float.TYPE;
  }
  
  public static void main(String[] args) {
    JFrame jf = new JFrame();
    jf.setBounds(200, 200, 400, 400);
    jf.setTitle("testing");
    JTextArea ta = new JTextArea(text(), 80, 40);
    JButton eval = new JButton("eval");
    eval.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        System.out.println("eval button");
      }
    });

    jf.getContentPane().setLayout(new BoxLayout(jf.getContentPane(), BoxLayout.Y_AXIS));
    jf.add(fileControl());
    jf.add(new JScrollPane(ta));
    jf.add(eval);
    jf.show();
  }
  
  private static Box fileControl() {
    Box box = Box.createHorizontalBox();
    box.add(new JTextField());
    JButton jButton = new JButton("Open");
    box.add(jButton);
    box.add(new JButton("Save"));
    return box;
  }

  private static String text() {
    String foo = "vd lfvgs fjlvkergel kjvflkjelrkj\n erfelgk lkrg elrkgj k\n erglwekrg lmvlkemrlkwemrlkgmwl ml\n";
    for (int i = 0; i < 8; i++) {
      foo += foo;
    }
    return foo;
  }
}
