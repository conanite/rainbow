package rainbow.vm;

import rainbow.ArcError;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Input;
import rainbow.types.Output;
import rainbow.types.Symbol;
import rainbow.vm.continuations.InvocationContinuation;

public class ArcThread extends ArcObject implements Runnable {
  private Symbol TYPE = (Symbol) Symbol.make("thread");
  private InvocationContinuation task;
  private ArcObject result;
  private ArcError error;
  private boolean stopped;
  protected Output stdOut = IO.STD_OUT;
  protected Input stdIn = IO.STD_IN;

  public ArcThread() {
  }

  public void run() {
    while (!stopped) {
      task.start();
    }
    stopped = true;
  }

  public void stop() {
    stopped = true;
  }

  public boolean isDead() {
    return stopped;
  }

  public int compareTo(ArcObject right) {
    return 0;
  }

  public ArcObject type() {
    return TYPE;
  }

  public void continueWith(InvocationContinuation nextTask) {
    if (stopped) {
      return;
    }
    this.task = nextTask;
  }

  public void finalValue(ArcObject o) {
    this.result = o;
    stop();
  }

  public ArcObject finalValue() {
    if (error != null) {
      throw error;
    }
    return result;
  }

  public void error(ArcError error) {
    this.error = error;
    stop();
  }

  public Output stdOut() {
    return stdOut;
  }

  public Input stdIn() {
    return stdIn;
  }

  public Input swapStdIn(Input input) {
    Input swap = stdIn;
    stdIn = input;
    return swap;
  }

  public Output swapStdOut(Output output) {
    Output swap = stdOut;
    stdOut = output;
    return swap;
  }

  public static ArcThread cast(ArcObject argument, ArcObject caller) {
    try {
      return (ArcThread) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a thread, got " + argument);
    }
  }
}
