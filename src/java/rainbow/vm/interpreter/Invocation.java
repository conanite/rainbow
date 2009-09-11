package rainbow.vm.interpreter;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.functions.interpreted.optimise.Bind;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.util.Decompiler;
import rainbow.vm.Instruction;
import rainbow.vm.instructions.invoke.*;
import rainbow.vm.interpreter.visitor.Visitor;

import java.lang.reflect.Constructor;
import java.util.*;

public class Invocation extends ArcObject {
  public final Pair parts;

  public Invocation(Pair parts) {
    this.parts = parts;
  }

  public ArcObject type() {
    return Symbol.mkSym("function-invocation");
  }

  public String toString() {
    return Decompiler.decompile(this).toString();
  }

  public void addInstructions(List i) {
    // java stupidly insists on assigning the result to a variable
    boolean v = inlineDoForm(i) || addOptimisedHandler(i) || defaultAddInstructions(i);
  }

  public ArcObject reduce() {
    if (parts.longerThan(1)) {
      if (parts.car() instanceof InterpretedFunction) {
        InterpretedFunction fn = (InterpretedFunction) parts.car();
        ArcObject plist = fn.parameterList();

        if (plist.isNotPair() || !(plist.car() instanceof Symbol)) {
          return this;
        }

        Symbol param = (Symbol) plist.car();
        ArcObject arg = parts.cdr().car();
        if (fn.canInline(param, arg)) {
          ArcObject newfn = null;
          try {
            newfn = fn.curry(param, arg, true);
          } catch (Exception e) {
            throw new ArcError("couldn't curry " + param + "->" + arg + " for " + fn + " in expression " + this + ": " + e, e);
          }
          return new Invocation(new Pair(newfn, parts.cdr().cdr())).reduce();
        }
      }
    }
    return this;
  }

  private boolean inlineDoForm(List i) {
    if (parts.len() == 1L && parts.car() instanceof Bind) {
      InterpretedFunction fn = (InterpretedFunction) parts.car();
      fn.buildInstructions(i);
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

  public static String sig(ArcObject o) {
    if (o instanceof BoundSymbol) {
      return "bound";
    } else if (o instanceof Symbol) {
      return "free";
    } else if (o instanceof StackSymbol) {
      return "stack";
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

  public boolean assigns(int nesting) {
    Pair pt = this.parts;
    while (!(pt instanceof Nil)) {
      if (pt.car().assigns(nesting)) {
        return true;
      }
      pt = (Pair) pt.cdr();
    }
    return false;
  }

  public int countReferences(int refs, BoundSymbol p) {
    Pair pt = this.parts;
    while (!(pt instanceof Nil)) {
      refs = pt.car().countReferences(refs, p);
      pt = (Pair) pt.cdr();
    }
    return refs;
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

  public ArcObject inline(BoundSymbol p, ArcObject arg, boolean unnest, int nesting, int paramIndex) {
    Pair pt = this.parts;
    List inlined = new ArrayList();
    while (!(pt instanceof Nil)) {
      try {
        inlined.add(pt.car().inline(p, arg, unnest, nesting, paramIndex));
      } catch (Exception e) {
        throw new ArcError("couldn't inline " + p + "->" + arg + "(unnest:" + unnest + ";nesting:" + nesting + ") in " + this + " : " + e, e);
      }
      pt = (Pair) pt.cdr();
    }
    return new Invocation(Pair.buildFrom(inlined));
  }

  public ArcObject inline(StackSymbol p, ArcObject arg, int paramIndex) {
    Pair pt = this.parts;
    List inlined = new ArrayList();
    while (!(pt instanceof Nil)) {
      try {
        inlined.add(pt.car().inline(p, arg, paramIndex));
      } catch (Exception e) {
        throw new ArcError("couldn't inline " + p + "->" + arg + ") in " + this + " : " + e, e);
      }
      pt = (Pair) pt.cdr();
    }
    return new Invocation(Pair.buildFrom(inlined));
  }

  public ArcObject nest(int threshold) {
    Pair pt = this.parts;
    List inlined = new ArrayList();
    while (!(pt instanceof Nil)) {
      inlined.add(pt.car().nest(threshold));
      pt = (Pair) pt.cdr();
    }
    return new Invocation(Pair.buildFrom(inlined));
  }

  public ArcObject replaceBoundSymbols(Map<Symbol, Integer> lexicalBindings) {
    Pair pt = this.parts;
    List inlined = new ArrayList();
    while (!(pt instanceof Nil)) {
      inlined.add(pt.car().replaceBoundSymbols(lexicalBindings));
      pt = (Pair) pt.cdr();
    }
    return new Invocation(Pair.buildFrom(inlined));
  }

  public void visit(Visitor v) {
    v.accept(this);
    Pair pt = this.parts;
    while (!(pt instanceof Nil)) {
      pt.car().visit(v);
      pt = (Pair) pt.cdr();
    }
    v.end(this);
  }
}
