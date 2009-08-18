package rainbow;

import rainbow.functions.*;
import rainbow.functions.system.*;
import rainbow.functions.typing.*;
import rainbow.functions.io.*;
import rainbow.functions.errors.*;
import rainbow.functions.network.OpenSocket;
import rainbow.functions.network.SocketAccept;
import rainbow.functions.network.ClientIp;
import rainbow.functions.strings.OutString;
import rainbow.functions.strings.Inside;
import rainbow.functions.strings.InString;
import rainbow.functions.threads.*;
import rainbow.functions.java.*;
import rainbow.functions.predicates.*;
import rainbow.functions.tables.Table;
import rainbow.functions.tables.MapTable;
import rainbow.functions.tables.Sref;
import rainbow.functions.lists.*;
import rainbow.functions.eval.Apply;
import rainbow.functions.eval.Eval;
import rainbow.functions.eval.SSyntax;
import rainbow.functions.eval.SSExpand;
import rainbow.functions.fs.*;
import rainbow.types.*;

public class Environment {

  public Environment() {
    new MSec();
    new Seconds();
    new DateTime();
    new CurrentProcessMilliseconds();
    new CurrentGcMilliseconds();
    new PipeFrom();
    new ShellInvoke();
    new WhichOS();
    new Declare();
    new Quit();

    Maths.collect(this);
    ThreadLocals.collect(this);
    Maths.extra(this);

    new Type();
    new Annotate();
    new Rep();
    new Coerce();
    new Ref();

    new JavaNew();
    new JavaClass();
    new JavaInvoke();
    new JavaStaticInvoke();
    new JavaStaticField();
    new JavaDebug();
    new JavaImplement();
    new RainbowDebug();

    new NewThread();
    new KillThread();
    new Sleep();
    new Dead();
    new AtomicInvoke();
    new CCC();

    new Uniq();
    new Macex();

    /* errors */
    new Protect();
    new Err();
    new OnErr();
    new Details();

    /* lists */
    new NewString();
    new Car();
    new Cdr();
    new Scar();
    new Scdr();
    new Cons();
    new Len();

    /* predicates */
    new Bound();
    new LessThan();
    new GreaterThan();
    new Exact();
    new Is();

    /* evaluation */
    new Apply();
    new Eval();
    new SSExpand();
    new SSyntax();

    /* tables */
    new Table();
    new MapTable();
    new Sref();
    new Hash();

    /* IO */
    new CallWStdIn();
    new CallWStdOut();
    new StdIn();
    new StdOut();
    new StdErr();
    new Disp();
    new Write();
    new Sread();
    new WriteB();
    new WriteC();
    new ReadB();
    new ReadC();
    new FlushOut();
    new Close();

    new OpenSocket();
    new ClientIp();
    new SocketAccept();

    new OutFile();
    new InFile();

    new DirExists();
    new FileExists();
    new Dir();
    new RmFile();
    new MvFile();
    new MakeDirectory();
    new MakeDirectories();

    new InString();
    new OutString();
    new Inside();
  }

  private void addBuiltin(String name, ArcObject o) {
    ((Symbol) Symbol.make(name)).setValue(o);
  }

  public void addToNamespace(Symbol s, ArcObject o) {
    s.setValue(o);
  }

  public ArcObject lookup(Symbol s) {
    if (!s.bound()) {
      return null;
    }
    return s.value();
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

  public void add(Builtin[] builtins) {
    for (int i = 0; i < builtins.length; i++) {
      addBuiltin(builtins[i].name(), builtins[i]);
    }
  }
}
