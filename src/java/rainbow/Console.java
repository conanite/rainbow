package rainbow;

import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.vm.continuations.TopLevelContinuation;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.types.Pair;
import rainbow.parser.ParseException;
import rainbow.parser.ArcParser;
import rainbow.util.Argv;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Console {
  public static boolean ARC2_COMPATIBILITY = false;
  public static boolean ANARKI_COMPATIBILITY = true;

  public static void main(String args[]) throws ParseException, IOException {
    Object o = ArcObject.NIL;
    Argv argv = new Argv(args);
    List programArgs = parseAll(argv.terminal("-args"));

    if (argv.present("--strict-arc")) {
      ANARKI_COMPATIBILITY = false;
      ARC2_COMPATIBILITY = true;
    }

    if (argv.present("--help")) {
      showHelp();
      System.exit(0);
    }

    Environment environment = new Environment();
    if (!argv.present("--no-arc")) {
      loadFile(environment, "arc.arc");
      loadFile(environment, "libs.arc");
    }

    environment.addToNamespace((Symbol) Symbol.make("*argv*"), Pair.buildFrom(programArgs));

    loadAll(environment, argv.multi("-f"));

    interpretAll(environment, argv.multi("-e"));

    if (!argv.present("-q")) {
      repl(environment);
    }
  }

  private static void showHelp() {
    System.out.print("" +
            "Launch the Rainbow Arc Interpreter                             " +
            "\n                                                             " +
            "\n rainbow [options]                                           " +
            "\n                                                             " +
            "\n where options include                                       " +
            "\n   --help       show this help                               " +
            "\n   -f file ...  load and interpret file                      " +
            "\n   -e expr ...  evaluate expr                                " +
            "\n   -q           quit after -e or -f, don't enter the REPL    " +
            "\n   --no-arc     don't load the base Arc libraries            " +
            "\n");
  }

  private static List parseAll(List list) throws ParseException {
    List result = new ArrayList();
    for (Iterator it = list.iterator(); it.hasNext();) {
      String arg = (String) it.next();
      result.add(new ArcParser(arg).parseOneLine());
    }
    return result;
  }

  private static void loadAll(Environment environment, List files) throws ParseException, IOException {
    for (Iterator i = files.iterator(); i.hasNext();) {
      String f = (String) i.next();
      loadFile(environment, f);
    }
  }

  private static void interpretAll(Environment environment, List expressionsToEval) throws ParseException {
    StringBuffer sb = new StringBuffer();
    for (Iterator it = expressionsToEval.iterator(); it.hasNext();) {
      String arg = (String) it.next();
      sb.append(arg).append(" ");
    }
    ArcParser parser = new ArcParser(sb.toString());
    ArcObject expression = parser.parseOneLine();
    while (expression != null) {
      interpret(environment, expression);
      expression = parser.parseOneLine();
    }
  }

  private static void repl(Environment environemt) throws ParseException {
    ArcParser parser = new ArcParser("<Console>", System.in);
    while (true) {
      System.out.print("arc> ");
      ArcObject expression = parser.parseOneLine();
      interpret(environemt, expression);
    }
  }

  private static void interpret(Environment environment, ArcObject expression) {
    try {
      System.out.println(compileAndEval(environment, expression));
    } catch (ArcError e) {
      System.out.println(e.getMessage());
      System.out.println(e.getStacktrace());
      e.printStackTrace(System.out);
    }
  }

  public static void loadResource(Environment arc, String resource) throws ParseException {
    load(arc, resource, Console.class.getResourceAsStream(resource));
  }

  public static void loadFile(Environment arc, String path) throws ParseException, IOException {
    load(arc, path, new FileInputStream(new File(path).getCanonicalFile()));
  }

  private static void load(Environment arc, String name, InputStream stream) throws ParseException {
    ArcParser parser = new ArcParser(name, stream);
    ArcObject expression = parser.parseOneLine();
    while (expression != null) {
      compileAndEval(arc, expression);
      expression = parser.parseOneLine();
    }
  }

  private static ArcObject compileAndEval(Environment environment, ArcObject expression) {
    ArcThread thread = new ArcThread(environment);
    TopLevelContinuation topLevel = new TopLevelContinuation(thread);
    Interpreter.compileAndEval(thread, null, topLevel, expression);
    thread.run();
    return thread.finalValue();
  }
}
