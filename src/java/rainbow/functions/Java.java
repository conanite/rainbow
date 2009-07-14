package rainbow.functions;

import rainbow.ArcError;
import rainbow.Environment;
import rainbow.LexicalClosure;
import rainbow.types.*;
import rainbow.vm.Continuation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class Java {
  public static void collect(Environment top) {
    top.add(new Builtin[] {
        new Builtin("java-new") {
          protected ArcObject invoke(Pair args) {
            Pair constructArgs = (Pair) args.cdr();
            String name = null;
            Pair types = null;
            if (args.car() instanceof ArcString) {
              name = ((ArcString)args.car()).value();
            } else if (args.car() instanceof Symbol) {
              name = ((Symbol)args.car()).name();
            } else {
              Pair nameTypes = Pair.cast(args.car(), this);
              types = (Pair) nameTypes.cdr();
              if (nameTypes.car() instanceof ArcString) {
                name = ((ArcString)nameTypes.car()).value();
              } else if (nameTypes.car() instanceof Symbol) {
                name = ((Symbol)nameTypes.car()).name();
              }
            }
            return JavaObject.instantiate(name, types, constructArgs);              
          }
        }, new Builtin("java-class") {
          protected ArcObject invoke(Pair args) {
            String className = ArcString.cast(args.car(), this).value();
            return JavaObject.getClassInstance(className);
          }
        }, new Builtin("java-invoke") {
          protected ArcObject invoke(Pair args) {
            JavaObject target = JavaObject.cast(args.car(), this);
            String methodName = Symbol.cast(args.cdr().car(), this).name();
            return wrap(target.invoke(methodName, (Pair) args.cdr().cdr().car()));
          }
        }, new Builtin("java-static-invoke") {
          protected ArcObject invoke(Pair args) {
            String target = ArcString.cast(args.car(), this).value();
            String methodName = Symbol.cast(args.cdr().car(), this).name();
            return wrap(JavaObject.staticInvoke(target, methodName, (Pair) args.cdr().cdr()));
          }
        }, new Builtin("java-static-field") {
          protected ArcObject invoke(Pair args) {
            try {
              String target = ArcString.cast(args.car(), this).value();
              String fieldName = Symbol.cast(args.cdr().car(), this).name();
              return wrap(JavaObject.getStaticFieldValue(target, fieldName));
            } catch (Throwable e) {
              throw new ArcError("could not static-invoke " + args, e);
            }
          }
        }, new Builtin("java-debug") {
          protected ArcObject invoke(Pair args) {
            Environment.debugJava = (args.car() == T);
            return args.car();
          }
        }, new Builtin("java-implement") {
          public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
            ArcObject interfaces = args.car();
            if (interfaces instanceof ArcString) {
              interfaces = Pair.buildFrom(interfaces);
            }
            ArcObject strict = args.cdr().car();
            Hash functions = Hash.cast(args.cdr().cdr().car(), this);
            caller.receive(JavaProxy.create(Pair.cast(interfaces, this), functions, strict));
          }
        }
    });
  }

  public static ArcObject wrap(Object o) {
    if (o == null) {
      return ArcObject.NIL;
    } else if (o instanceof ArcObject) {
      return (ArcObject) o;
    } else if (o instanceof Integer || o instanceof Long) {
      return Rational.make(((Number)o).longValue());
    } else if (o instanceof Float || o instanceof Double) {
      return Real.make(((Number)o).doubleValue());
    } else if (o instanceof String) {
      return ArcString.make(o.toString());
    } else if (o instanceof Character) {
      return ArcCharacter.make((Character)o);
    } else if (o.getClass().isArray()) {
      return wrapList((Object[])o);
    } else if (o instanceof List) {
      return wrapList((List)o);
    } else if (o instanceof Map) {
      return wrapMap((Map)o);
    } else if (o instanceof Boolean) {
      if ((Boolean) o) {
        return ArcObject.T;
      } else {
        return ArcObject.NIL;
      }
    } else {
      return new JavaObject(o);
    }
  }

  private static ArcObject wrapList(Object[] objects) {
    List result = new ArrayList(objects.length);
    for (int i = 0; i < objects.length; i++) {
      result.add(wrap(objects[i]));
    }
    return Pair.buildFrom(result);
  }

  private static ArcObject wrapList(List list) {
    List result = new ArrayList(list.size());
    for (Iterator it = list.iterator(); it.hasNext();) {
      result.add(wrap(it.next()));
    }
    return Pair.buildFrom(result);
  }

  private static ArcObject wrapMap(Map map) {
    Hash hash = new Hash();
    for (Iterator it = map.keySet().iterator(); it.hasNext();) {
      Object key = it.next();
      Object value = map.get(key);
      hash.sref(wrap(key), wrap(value));
    }
    return hash;
  }

}
