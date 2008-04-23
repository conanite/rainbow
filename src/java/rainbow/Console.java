package rainbow;

import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.vm.continuations.TopLevelContinuation;
import rainbow.vm.continuations.ContinuationSupport;
import rainbow.types.ArcObject;
import rainbow.parser.ParseException;
import rainbow.parser.ArcParser;

import java.io.*;

public class Console {
  public static void main(String args[]) throws ParseException, IOException {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        int total = 0;
        for (Object o : ContinuationSupport.instances.keySet()) {
          Integer count = (Integer) ContinuationSupport.instances.get(o);
          total += count;
          System.out.println(o + "\t\t: " + count);
        }
        System.out.println("total : " + total);
      }
    });

    Bindings arc = new Bindings();
    loadResource(arc, "/arc/arc.arc");
    loadResource(arc, "/arc/strings.arc");
    loadResource(arc, "/arc/pprint.arc");
    loadResource(arc, "/arc/code.arc");
    loadResource(arc, "/arc/html.arc");
    loadResource(arc, "/arc/srv.arc");
    loadResource(arc, "/arc/app.arc");
    loadResource(arc, "/arc/blog.arc");
    loadResource(arc, "/arc/prompt.arc");
    loadResource(arc, "/arc/news.arc");
    Interpreter.debug = true;

    if (args.length > 0) {
      interpret(arc, args);
    } else {
      repl(arc);
    }
  }

  private static void interpret(Bindings arc, String[] args) throws ParseException {
    StringBuffer sb = new StringBuffer();
    for (String arg : args) {
      sb.append(arg).append(" ");
    }
    ArcParser parser = new ArcParser(sb.toString());
    ArcObject expression = parser.parseOneLine();
    while (expression != null) {
      interpret(arc, expression);
      expression = parser.parseOneLine();
    }
  }

  private static void repl(Bindings arc) throws ParseException {
    ArcParser parser = new ArcParser("<Console>", System.in);
    while (true) {
      System.out.print("arc> ");
      ArcObject expression = parser.parseOneLine();
      interpret(arc, expression);
    }
  }

  private static void interpret(Bindings arc, ArcObject expression) {
//    ArcThread thread = new ArcThread();
//    TopLevelContinuation top = new TopLevelContinuation(thread);
//    Interpreter.interpret(thread, arc, top, expression);
    try {
      System.out.println(compileAndEval(arc, expression));
//      thread.run();
//      System.out.println(thread.finalValue());
    } catch (ArcError e) {
      System.out.println(e.getMessage());
      System.out.println(e.getStacktrace());
      e.printStackTrace(System.out);
    }
  }

  public static void loadResource(Bindings arc, String resource) throws ParseException {
    System.out.print("loading " + resource + " ");

    ArcParser parser = new ArcParser(resource, Console.class.getResourceAsStream(resource));
    ArcObject expression = parser.parseOneLine();
    while (expression != null) {
      System.out.print(".");
      compileAndEval(arc, expression);
      expression = parser.parseOneLine();
    }
    System.out.println();
  }

  private static ArcObject compileAndEval(Bindings arc, ArcObject expression) {
    ArcThread thread = new ArcThread();
    TopLevelContinuation topLevel = new TopLevelContinuation(thread);
    Interpreter.compileAndEval(thread, arc, topLevel, expression);
    thread.run();
    return thread.finalValue();
  }

}
