package rainbow;

import rainbow.functions.*;
import rainbow.types.ArcObject;
import rainbow.types.Hash;
import rainbow.types.Output;
import rainbow.types.Input;

import java.util.*;

public class TopBindings extends Bindings {
  private java.util.Set defaults;

  public TopBindings() { // todo: macex1, pipe-from, complex numbers, file-exists, rmfile, threading
    super(null);
    IO.collect(this);
    SystemFunctions.collect(this);
    Maths.collect(this);

    namespace.put("t", ArcObject.T);
    namespace.put("nil", ArcObject.NIL);
    namespace.put("uniq", new Uniq());
    namespace.put("newstring", new Lists.NewString());
    namespace.put("ccc", new Threads.CCC());
    namespace.put("macex", new Macex());

    /* threading */
    namespace.put("atomic-invoke", new Threads.AtomicInvoke());
    namespace.put("new-thread", new Threads.NewThread());
    namespace.put("dead", new Threads.Dead());
    namespace.put("sleep", new Threads.Sleep());

    /* errors */
    namespace.put("protect", new Errors.Protect());
    namespace.put("err", new Errors.Err());
    namespace.put("on-err", new Errors.OnErr());
    namespace.put("details", new Errors.Details());

    /* typing */
    namespace.put("type", new Typing.Type());
    namespace.put("coerce", new Typing.Coerce());
    namespace.put("annotate", new Typing.Annotate());
    namespace.put("rep", new Typing.Rep());

    /* lists */
    namespace.put("car", new Lists.Car());
    namespace.put("cdr", new Lists.Cdr());
    namespace.put("scar", new Lists.Scar());
    namespace.put("scdr", new Lists.Scdr());
    namespace.put("cons", new Lists.Cons());
    namespace.put("len", new Lists.Len());

    /* predicates */
    namespace.put("bound", new Predicates.Bound());
    namespace.put("<", new Predicates.LessThan());
    namespace.put(">", new Predicates.GreaterThan());
    namespace.put("exact", new Predicates.Exact());
    namespace.put("is", new Predicates.Is());

    /* special */
    namespace.put("fn", new Specials.Fn());
    namespace.put("set", new Specials.Set());
    namespace.put("quote", new Specials.Quote());
    namespace.put("quasiquote", new Specials.QuasiQuote());
    namespace.put("if", new Specials.If());

    /* evaluation */
    namespace.put("apply", new Evaluation.Apply());
    namespace.put("eval", new Evaluation.Eval());
    namespace.put("ssexpand", new Evaluation.SSExpand());
    namespace.put("ssyntax", new Evaluation.SSyntax());

    /* tables */
    namespace.put("table", new Tables.Table());
    namespace.put("maptable", new Tables.MapTable());
    namespace.put("sref", new Tables.Sref());
    namespace.put("sig", new Hash());

    /* IO */
    namespace.put("instring", new StringIO.InString());
    namespace.put("outstring", new StringIO.OutString());
    namespace.put("inside", new StringIO.Inside());
    namespace.put("infile", new FileSystem.InFile());
    namespace.put("outfile", new FileSystem.OutFile());
    namespace.put("dir", new FileSystem.Dir());
    namespace.put("dir-exists", new FileSystem.DirExists());
    namespace.put("file-exists", new FileSystem.FileExists());
    namespace.put("rmfile", new FileSystem.RmFile());
    namespace.put("open-socket", new Network.OpenSocket());
    namespace.put("socket-accept", new Network.SocketAccept());

    defaults = new HashSet(namespace.keySet());
  }

  public void addToNamespace(String s, ArcObject o) {
    namespace.put(s, o);
  }

  public ArcObject lookup(String s) {
    return namespace.get(s);
  }

  public Output stdOut() {
    return IO.STD_OUT;
  }

  public Input stdIn() {
    return IO.STD_IN;
  }

  public Bindings getTop() {
    return this;
  }

  public String toString() {
    Map m = new HashMap(namespace);
    m.keySet().removeAll(defaults);
    return toString(m);
  }

  public String fullNamespace() {
    return "Namespace " + id + "\n" + toString();
  }

  protected synchronized void collectKeys(java.util.Set s) {
    java.util.Set top = new HashSet(namespace.keySet());
    top.removeAll(defaults);
    s.addAll(top);
  }

  public void add(Builtin[] builtins) {
    for (int i = 0; i < builtins.length; i++) {
      namespace.put(builtins[i].name(), builtins[i]);
    }
  }
}
