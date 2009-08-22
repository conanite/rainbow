package rainbow.vm;

import rainbow.ArcError;
import rainbow.Console;
import rainbow.LexicalClosure;
import rainbow.Nil;
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
  private static long threadCount = 0;

  private final long threadId;
  {
    synchronized(VM.class) {
      threadId = threadCount++;
    }
  }

  public ArcObject[] args = new ArcObject[100];
  public LexicalClosure[] lcs = new LexicalClosure[100];
  public Pair[] ins = new Pair[100];
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
      throw new ArcError("Unhandled exception on thread#" + threadId + ": " + ae.getOriginal().getMessage(), ae.getOriginal());
    }
    interceptor.end(this);
    return popA();
  }

  private void loop() {
    while (ip >= ipThreshold) {
      currentLc = peekL();
      try {
        step();
        interceptor.check(this);
      } catch (Throwable e) {
        handleError(e);
      }
    }
  }

  private void step() {
    if (ip < 0) {
      throw new Error("step: ip can't possibly be below 0!");
    }
    Pair instructions = peekI();
    Instruction i = (Instruction) instructions.car();

    Pair rest = (Pair) instructions.cdr();
    if (rest instanceof Nil) {
      popFrame();
    } else {
      pokeI(rest);
    }

    try {
      i.operate(this);
    } catch (ArcError e) {
      throw e;
    } catch (Exception e) {
      String msg = "failed to execute instruction " + i.toString(currentLc) +
              "\nremaining instructions in this frame: " + rest +
              "\nlast arg: " + (ap > -1 ? peekA() : null) +
              "\nLC: " + currentLc +
              "\nmessage: " + e.getMessage();
      System.out.println(msg);
      throw new ArcError(msg, e);
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

  public void show() {
    System.out.println("Thread Dump for thread#" + threadId);
    System.out.println("" + (ap + 1) + " args");
    showArgs();
    System.out.println();
    System.out.println("" + (ip + 1) + " instruction frames");
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
    try {
      ins[ip] = instructions;
      lcs[ip] = lc;
    } catch (ArrayIndexOutOfBoundsException e) {
      newInstructionArray(ins.length * 2);
      newClosureArray(lcs.length * 2);
      ip--;
      System.out.println("expanded instruction stack to " + (ins.length));
      pushFrame(lc, instructions);
    }
  }

  public void pokeI(Pair instructions) {
    if (ip < 0) {
      throw new Error("pokeI: ip can't possibly be below 0!");
    }
    ins[ip] = instructions;
  }

  public Pair peekI() {
    return ins[ip];
  }

  public LexicalClosure peekL() {
    return lcs[ip];
  }

  public void pushA(ArcObject arg) {
    ++ap;
    try {
      args[ap] = arg;
    } catch (ArrayIndexOutOfBoundsException e) {
      newArgArray(args.length * 2);
      ap--;
      System.out.println("expanded arg stack to " + (args.length));
      pushA(arg);
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
    int end = (ip > 4) ? (ip - 4) : 0;
    for (int i = ip; i >= end; i--) {
      showFrame(i);
    }
  }

  private void showFrame(int frame) {
    Pair instructions = ins[frame];
    LexicalClosure lc = lcs[frame];
    System.out.print("\nInstruction Frame " + frame + ":");
    while (!(instructions instanceof Nil)) {
      Instruction i = (Instruction) instructions.car();
      instructions = (Pair) instructions.cdr();
      System.out.print(i.toString(lc));
      System.out.print(" ");
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
    compact();
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

  private void compact() {
    if (ins.length > (ip * 2)) {

    }
  }

  public boolean dead() {
    return dead;
  }

  public String toString() {
    return "[thread#" + threadId + ": instruction stack " + ip + "; arg stack " + ap + "]";
  }

  private void newArgArray(int newLength) {
    ArcObject[] newArgs = new ArcObject[newLength];
    copy(args, newArgs);
    this.args = newArgs;
  }

  private void newClosureArray(int newLength) {
    LexicalClosure[] newL = new LexicalClosure[newLength];
    copy(lcs, newL);
    this.lcs = newL;
  }

  private void newInstructionArray(int newLength) {
    Pair[] newI = new Pair[newLength];
    copy(ins, newI);
    this.ins = newI;
  }

  private void copy(Object[] src, Object[] dest) {
    System.arraycopy(src, 0, dest, 0, src.length);
  }


}

