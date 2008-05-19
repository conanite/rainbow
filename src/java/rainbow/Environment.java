package rainbow;

import rainbow.functions.*;
import rainbow.types.*;

import java.util.*;
import java.io.File;

public class Environment {
  private java.util.Set defaults;
  protected Map namespace = new HashMap();

  public Environment() { // todo: macex1, pipe-from, complex numbers, threading
    Java.collect(this);
    IO.collect(this);
    SystemFunctions.collect(this);
    Maths.collect(this);
    Typing.collect(this);
    ThreadLocals.collect(this);

    addBuiltin("t", ArcObject.T);
    addBuiltin("nil", ArcObject.NIL);
    addBuiltin("uniq", new Uniq());
    addBuiltin("newstring", new Lists.NewString());
    addBuiltin("ccc", new Threads.CCC());
    addBuiltin("macex", new Macex());

    /* threading */
    addBuiltin("atomic-invoke", new Threads.AtomicInvoke());
    addBuiltin("new-thread", new Threads.NewThread());
    addBuiltin("dead", new Threads.Dead());
    addBuiltin("sleep", new Threads.Sleep());

    /* errors */
    addBuiltin("protect", new Errors.Protect());
    addBuiltin("err", new Errors.Err());
    addBuiltin("on-err", new Errors.OnErr());
    addBuiltin("details", new Errors.Details());
    
    /* lists */
    addBuiltin("car", new Lists.Car());
    addBuiltin("cdr", new Lists.Cdr());
    addBuiltin("scar", new Lists.Scar());
    addBuiltin("scdr", new Lists.Scdr());
    addBuiltin("cons", new Lists.Cons());
    addBuiltin("len", new Lists.Len());

    /* predicates */
    addBuiltin("bound", new Predicates.Bound());
    addBuiltin("<", new Predicates.LessThan());
    addBuiltin(">", new Predicates.GreaterThan());
    addBuiltin("exact", new Predicates.Exact());
    addBuiltin("is", new Predicates.Is());

    /* special */
    addBuiltin("set", new Specials.Set());
    addBuiltin("quote", new Specials.Quote());
    addBuiltin("quasiquote", new Specials.QuasiQuote());
    addBuiltin("if", new Specials.If());

    /* evaluation */
    addBuiltin("apply", new Evaluation.Apply());
    addBuiltin("eval", new Evaluation.Eval());
    addBuiltin("ssexpand", new Evaluation.SSExpand());
    addBuiltin("ssyntax", new Evaluation.SSyntax());

    /* tables */
    addBuiltin("table", new Tables.Table());
    addBuiltin("maptable", new Tables.MapTable());
    addBuiltin("sref", new Tables.Sref());
    addBuiltin("sig", new Hash());

    /* IO */
    addBuiltin("instring", new StringIO.InString());
    addBuiltin("outstring", new StringIO.OutString());
    addBuiltin("inside", new StringIO.Inside());
    addBuiltin("infile", new FileSystem.InFile());
    addBuiltin("outfile", new FileSystem.OutFile());
    addBuiltin("dir", new FileSystem.Dir());
    addBuiltin("dir-exists", new FileSystem.DirExists());
    addBuiltin("file-exists", new FileSystem.FileExists());
    addBuiltin("rmfile", new FileSystem.RmFile());
    addBuiltin("open-socket", new Network.OpenSocket());
    addBuiltin("socket-accept", new Network.SocketAccept());

    if (Console.ANARKI_COMPATIBILITY) {
      addBuiltin("seval", new Evaluation.Seval());
    }

    defaults = new HashSet(namespace.keySet());
  }

  private void addBuiltin(String name, ArcObject o) {
    namespace.put(Symbol.make(name), o);
  }

  public void addToNamespace(Symbol s, ArcObject o) {
    namespace.put(s, o);
  }

  public ArcObject lookup(Symbol s) {
    return (ArcObject) namespace.get(s);
  }

  public Output stdOut() {
    return IO.STD_OUT;
  }

  public Input stdIn() {
    return IO.STD_IN;
  }

  public String fullNamespace() {
    return "Namespace\n" + toString();
  }

  public String toString() {
    return toString(namespace);
  }

  protected static String toString(Map objectMap) {
    StringBuffer sb = new StringBuffer();
    for (Iterator i = objectMap.keySet().iterator(); i.hasNext();) {
      Object key = i.next();
      sb.append(key).append("\t:\t").append(objectMap.get(key)).append("\n");
    }
    return sb.toString();
  }

  
  protected synchronized void collectKeys(java.util.Set s) {
    java.util.Set top = new HashSet(namespace.keySet());
    top.removeAll(defaults);
    s.addAll(top);
  }

  public void add(Builtin[] builtins) {
    for (int i = 0; i < builtins.length; i++) {
      addBuiltin(builtins[i].name(), builtins[i]);
    }
  }
}
