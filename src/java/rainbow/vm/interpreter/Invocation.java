package rainbow.vm.interpreter;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.functions.interpreted.optimise.Bind;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.Instruction;
import rainbow.vm.instructions.invoke.*;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Invocation extends ArcObject {
  private final Pair parts;

  public Invocation(Pair parts) {
    this.parts = parts;
  }

  public ArcObject type() {
    return Symbol.mkSym("function-invocation");
  }

  public String toString() {
    return parts.toString();
  }

  public void addInstructions(List i) {
    // java stupidly insists on assigning the result to a variable
    boolean v = inlineDoForm(i) || addOptimisedHandler(i) || defaultAddInstructions(i);
  }

  // reduce ( (fn (x) x) y) to just y
  public ArcObject reduce() {
    if (parts.hasLen(2)) {
      if (parts.car() instanceof InterpretedFunction) {
        if (((InterpretedFunction) parts.car()).isIdFn()) {
          return parts.cdr().car();
        }
      }
    }
    return this;
  }

  private boolean inlineDoForm(List i) {
    if (parts.len() == 1L && parts.car() instanceof Bind) {
      ((InterpretedFunction) parts.car()).instructions().copyTo(i);
      return true;
    }
    return false;
  }

  private Map<String, Constructor<Instruction>> constructors = new HashMap();

  private boolean addOptimisedHandler(List i) {
    String cname = "rainbow.vm.instructions.invoke.optimise.Invoke" + sig();
    if (constructors.containsKey(cname)) {
      Constructor<Instruction> c = constructors.get(cname);
      if (c == null) {
        return false;
      }
      try {
        addOptimisedInstructions(i, c.newInstance(parts));
        return true;
      } catch (Exception e) {
        throw new ArcError("couldn't create optimiser " + cname, e);
      }
    } else {
      try {
        Class iClass = Class.forName(cname);
        Constructor<Instruction> co = iClass.getConstructor(Pair.class);
        constructors.put(cname, co);
        return addOptimisedHandler(i);
      } catch (ClassNotFoundException e) {
        constructors.put(cname, null);
        return false;
      } catch (NoSuchMethodException e) {
        throw new ArcError("Can't find constructor for " + cname, e);
      }
    }
  }

  private void addOptimisedInstructions(List i, Instruction in) {
    Pair p = (Pair) this.parts.cdr();
    while (!(p instanceof Nil)) {
      if ("other".equals(sig(p.car()))) {
        p.car().addInstructions(i);
      }
      p = (Pair) p.cdr();
    }

    if ("other".equals(sig(this.parts.car()))) {
      this.parts.car().addInstructions(i);
    }
    i.add(in);
  }

  private String sig() {
    String s = "";
    Pair p = this.parts;
    while (!(p instanceof Nil)) {
      s += "_";
      s += sig(p.car());
      p = (Pair) p.cdr();
    }
    return s;
  }

  private String sig(ArcObject o) {
    if (o instanceof BoundSymbol) {
      return "bound";
    } else if (o instanceof Symbol) {
      return "free";
    } else if (o.literal()) {
      return "literal";
    } else if (o instanceof Quotation) {
      return "quote";
    } else {
      return "other";
    }
  }

  private boolean defaultAddInstructions(List i) {
    switch ((int) parts.len()) {
      case 1:
        Invoke_0.addInstructions(i,
                parts.car());
        break;
      case 2:
        Invoke_1.addInstructions(i,
                parts.car(),
                parts.cdr().car()
        );
        break;
      case 3:
        Invoke_2.addInstructions(i,
                parts.car(),
                parts.cdr().car(),
                parts.cdr().cdr().car()
        );
        break;
      case 4:
        Invoke_3.addInstructions(i,
                parts.car(),
                parts.cdr().car(),
                parts.cdr().cdr().car(),
                parts.cdr().cdr().cdr().car()
        );
        break;
      default:
        Invoke_N.addInstructions(i,
                parts.car(),
                (Pair) parts.cdr());
    }
    return true;
  }
}
