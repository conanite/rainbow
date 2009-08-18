package rainbow.vm;

import rainbow.ArcError;
import rainbow.Console;
import rainbow.LexicalClosure;
import rainbow.types.ArcException;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.instructions.Catch;
import rainbow.vm.instructions.Finally;

import java.util.ArrayList;
import java.util.List;

public class VM extends ArcObject {
  public static final Symbol TYPE = Symbol.mkSym("thread");

  public ArcObject[] args = new ArcObject[1000];
  public LexicalClosure[] lcs = new LexicalClosure[1000];
  public Pair[] ins = new Pair[1000];
  public int ap = -1;
  public int ip = -1;
  private LexicalClosure currentLc;
  private ArcException error;
  private VMInterceptor interceptor = VMInterceptor.NULL;
  private boolean dead = false;
  private int ipThreshold;

  public ArcObject thread(LexicalClosure lc, Pair instructions) {
    pushFrame(lc, instructions);
    return thread();
  }

  public ArcObject thread() {
    int oldThreshold = ipThreshold;
    ipThreshold = ip;
    try {
      loop();
    } finally {
      ipThreshold = oldThreshold;
    }
    if (error != null) {
      ArcException ae = error;
      error = null;
      throw new ArcError("Unhandled exception: " + ae.getOriginal().getMessage(), ae.getOriginal());
    }
    interceptor.end(this);
    return popA();
  }

  private void loop() {
    Pair instructions;
    while (ip >= ipThreshold) {
      instructions = peekI();
      currentLc = peekL();

      if (instructions.isNil()) {
        popFrame();
      } else {
        try {
          step(instructions);
        } catch (Throwable e) {
          handleError(e);
        }
      }
    }
  }

  private void handleError(Throwable e) {
    List instructions = new ArrayList();
    List lexClosures = new ArrayList();

    while (ip >= ipThreshold && (!(peekI().car() instanceof Catch))) {
      if (peekI().car() instanceof Finally) {
        instructions.add(peekI());
        lexClosures.add(peekL());
      }
      popFrame();
    }

    this.error = new ArcException(e);

    if (Console.debugJava) {
      System.out.println("\n\n------------------ ERROR -------------------");
      System.out.println("handling error " + e);
      System.out.println("finally clauses: " + instructions);
      System.out.println("catch clause: " + (ip >= 0));
      e.printStackTrace(System.out);
      System.out.println("------------------ ERROR -------------------\n\n");
    }

    for (int i = instructions.size() - 1; i >= 0; i--) {
      pushFrame((LexicalClosure) lexClosures.get(i), (Pair) instructions.get(i));
    }
  }

  public void die() {
    dead = true;
    List instructions = new ArrayList();
    List lexClosures = new ArrayList();

    while (ip >= 0) {
      if (peekI().car() instanceof Finally) {
        instructions.add(peekI());
        lexClosures.add(peekL());
      }
      popFrame();
    }

    for (int i = instructions.size() - 1; i >= 0; i--) {
      pushFrame((LexicalClosure) lexClosures.get(i), (Pair) instructions.get(i));
    }
  }

  public ArcException error() {
    return error;
  }

  private void step(Pair instructions) {
    interceptor.check(this);
    Instruction i = (Instruction) instructions.car();

    Pair rest = (Pair) instructions.cdr();
    if (rest.isNil()) {
      popFrame();
    } else {
      pokeI(rest);
    }

    try {
//      System.out.println("VM.step: " + i);
      i.operate(this);
    } catch (ArcError e) {
      throw e;
    } catch (Exception e) {
      String msg = "failed to execute instruction " + i +
              "\nremaining instructions in this frame: " + rest +
              "\nlast arg: " + (ap > -1 ? peekA() : null) +
              "\nLC: " + currentLc +
              "\nmessage: " + e.getMessage();
      System.out.println(msg);
      throw new ArcError(msg, e);
    }
  }

  public void show() {
    System.out.println("args: " + ap);
    showArgs();
    System.out.println("instructions: " + ip);
    showInstructions();
  }

  private void popFrame() {
    ip--;
  }

  public void pushFrame(Instruction i) {
    pushFrame(null, Pair.buildFrom(i));
  }

  public void pushFrame(LexicalClosure lc, Pair instructions) {
    ++ip;
    ins[ip] = instructions;
    lcs[ip] = lc;
  }

  public void pokeI(Pair instructions) {
    ins[ip] = instructions;
  }

  public Pair peekI() {
    return ins[ip];
  }

  public LexicalClosure peekL() {
    return lcs[ip];
  }

  public void pushA(ArcObject arg) {
    try {
      args[++ap] = arg;
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
  }

  public ArcObject peekA() {
    return args[ap];
  }

  public ArcObject popA() {
    if (ap < 0) {
      throw new Error("no more args on arg stack!");
    }
    return args[ap--];
  }

  public Pair popArgs(int argCount) {
    Pair result = ArcObject.EMPTY_LIST;
    for (int i = 0; i < argCount; i++) {
      ArcObject arg = args[ap - i];
      result = new Pair(arg, result);
    }
    ap -= argCount;
    return result;
  }

  private void showArgs() {
    int start = (ap > 5) ? (ap - 5) : 0;
    for (int i = start; i < ap + 1; i++) {
      System.out.println(i + ". " + args[i]);
    }
  }

  private void showInstructions() {
    int start = (ip > 3) ? (ip - 3) : 0;
    for (int i = start; i < ip + 1; i++) {
      showFrame(i);
    }
  }

  private void showFrame(int frame) {
    Pair instructions = ins[frame];
    LexicalClosure lc = lcs[frame];
    System.out.println("Instruction Frame " + frame + ".");
    while (!instructions.isNil()) {
      Instruction i = (Instruction) instructions.car();
      instructions = (Pair) instructions.cdr();
      System.out.print(i.toString(lc));
    }
    System.out.println();
  }

  public LexicalClosure lc() {
    return currentLc;
  }

  public void clearError() {
    this.error = null;
  }

  public int ap() {
    return ap;
  }

  public void ap(int ap) {
    this.ap = ap;
  }

  public void setInterceptor(VMInterceptor interceptor) {
    this.interceptor = interceptor;
  }

  public ArcObject type() {
    return TYPE;
  }

  public VM copy() {
    VM vm = new VM();
    copyTo(vm);
    return vm;
  }

  public void copyTo(VM vm) {
    vm.ap = this.ap;
    vm.ip = this.ip;
    vm.ins = this.ins.clone();
    vm.args = this.args.clone();
    vm.lcs = this.lcs.clone();
    vm.currentLc = this.currentLc;
    vm.error = this.error;
    vm.interceptor = this.interceptor;
    vm.dead = this.dead;
    vm.ipThreshold = this.ipThreshold;
  }

  public boolean dead() {
    return dead;
  }

  public String toString() {
    return "[thread: instruction stack " + ip + "; arg stack " + ap + "]";
  }


  public static class ListBuilder extends ArcObject {
    private final List list = new ArrayList();
    private ArcObject last = NIL;

    public void append(ArcObject o) {
      list.add(o);
    }

    public void appendAll(Pair p) {
      p.copyTo(list);
    }

    public void last(ArcObject o) {
      last = o;
    }

    public Pair list() {
      return Pair.buildFrom(list, last);
    }

    public ArcObject type() {
      return Symbol.mkSym("list-builder");
    }

    public String toString() {
      return "ListBuilder:" + list().toString();
    }
  }
}

