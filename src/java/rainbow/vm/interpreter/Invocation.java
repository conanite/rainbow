package rainbow.vm.interpreter;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.functions.interpreted.optimise.Bind;
import rainbow.types.ArcObject;
import rainbow.types.LiteralObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.Instruction;
import rainbow.vm.instructions.invoke.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
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
        InterpretedFunction fn = (InterpretedFunction) parts.car();
        if (fn.isIdFn()) {
          return parts.cdr().car();
        } else if (fn.canInline() && inlineableArg(parts.cdr().car())) {
          Pair reduced = Pair.buildFrom(fn.curry((Symbol) fn.parameterList().car(), parts.cdr().car()).reduce());
//          System.out.println("inlined " + this + " to " + reduced);
          return new Invocation(reduced);
        }
      }
    }
    return this;
  }

  private boolean inlineableArg(ArcObject arg) {
    return (arg instanceof LiteralObject) || (arg instanceof Quotation) || (arg instanceof Symbol) || (arg instanceof BoundSymbol);
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

  public int highestLexicalScopeReference() {
    return parts.highestLexicalScopeReference();
  }

  public boolean assigns(BoundSymbol p) {
    Pair pt = this.parts;
    while (!(pt instanceof Nil)) {
      if (pt.car().assigns(p)) {
        return true;
      }
      pt = (Pair) pt.cdr();
    }
    return false;
  }

  public boolean hasClosures() {
    Pair pt = this.parts;
    while (!(pt instanceof Nil)) {
      if (pt.car() instanceof InterpretedFunction) {
        if (((InterpretedFunction)pt.car()).requiresClosure()) {
          return true;
        }
      } else if (pt.car().hasClosures()) {
        return true;
      }
      pt = (Pair) pt.cdr();
    }
    return false;
  }

  public ArcObject inline(BoundSymbol p, ArcObject arg, boolean unnest) {
    Pair pt = this.parts;
    List inlined = new ArrayList();
    while (!(pt instanceof Nil)) {
      inlined.add(pt.car().inline(p, arg, unnest));
      pt = (Pair) pt.cdr();
    }
    return new Invocation(Pair.buildFrom(inlined));
  }
}
