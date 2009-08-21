package rainbow.functions;

import rainbow.functions.errors.Details;
import rainbow.functions.errors.Err;
import rainbow.functions.errors.OnErr;
import rainbow.functions.errors.Protect;
import rainbow.functions.eval.Apply;
import rainbow.functions.eval.Eval;
import rainbow.functions.eval.SSExpand;
import rainbow.functions.eval.SSyntax;
import rainbow.functions.fs.*;
import rainbow.functions.io.*;
import rainbow.functions.java.*;
import rainbow.functions.lists.*;
import rainbow.functions.maths.*;
import rainbow.functions.network.ClientIp;
import rainbow.functions.network.OpenSocket;
import rainbow.functions.network.SocketAccept;
import rainbow.functions.predicates.*;
import rainbow.functions.strings.InString;
import rainbow.functions.strings.Inside;
import rainbow.functions.strings.OutString;
import rainbow.functions.system.*;
import rainbow.functions.tables.MapTable;
import rainbow.functions.tables.Sref;
import rainbow.functions.tables.Table;
import rainbow.functions.threads.*;
import rainbow.functions.typing.*;
import rainbow.types.Hash;
import rainbow.types.Real;
import rainbow.types.Symbol;

public class Environment {
  public static void init() {
    /* system */
    new MSec();
    new Seconds();
    new TimeDate();
    new CurrentProcessMilliseconds();
    new CurrentGcMilliseconds();
    new PipeFrom();
    new ShellInvoke();
    new WhichOS();
    new Declare();
    new SetUID();
    new Memory();
    new Quit();

    /* maths */
    (Symbol.mkSym("pi")).setValue(new Real(Math.PI));
    (Symbol.mkSym("e")).setValue(new Real(Math.E));
    new Trunc();
    new Expt();
    new Rand();
    new Sqrt();
    new Quotient();
    new Mod();
    new Add();
    new Subtract();
    new Multiply();
    new Divide();
    new Sine();
    new Cosine();
    new Tangent();
    new Logarithm();
    new ComplexParts();
    new MakeComplex();
    new PolarCoordinates();

    /* typing */
    new Type();
    new Annotate();
    new Rep();
    new Coerce();

    /* java integration */
    new JavaNew();
    new JavaClass();
    new JavaInvoke();
    new JavaStaticInvoke();
    new JavaStaticField();
    new JavaDebug();
    new JavaImplement();
    new RainbowDebug();

    /* threading */
    new NewThread();
    new KillThread();
    new BreakThread(); // todo just duplicates kill-thread, should do something else
    new Sleep();
    new Dead();
    new AtomicInvoke();
    new CCC();
    new NewThreadLocal();
    new ThreadLocalGet();
    new ThreadLocalSet();

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
    new ForceClose();

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
}
