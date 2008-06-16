package rainbow;

import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.vm.continuations.TopLevelContinuation;
import rainbow.types.*;
import rainbow.parser.ParseException;
import rainbow.parser.ArcParser;
import rainbow.util.Argv;

import java.io.*;
import java.util.*;

public class Console {
  public static boolean ARC2_COMPATIBILITY = false;
  public static boolean ANARKI_COMPATIBILITY = true;

  public static void main(String args[]) throws ParseException, IOException {
    Object o = ArcObject.NIL;
    String[] path = getArcPath();
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
    environment.addToNamespace((Symbol) Symbol.make("*argv*"), Pair.buildFrom(programArgs));
    environment.addToNamespace((Symbol) Symbol.make("*env*"), getEnvironment());
    
    if (!argv.present("--no-libs")) {
      loadFile(environment, path, "arc.arc");
      loadFile(environment, path, "strings.arc");
      loadFile(environment, path, "rainbow/rainbow.arc");
      loadFile(environment, path, "rainbow/rainbow-libs.arc");
    }
    
    loadAll(environment, path, argv.multi("-f"));

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
            "\n   --no-libs    don't load any arc libraries                 " +
            "\n");
  }

  private static List parseAll(List list) throws ParseException {
    List result = new ArrayList();
    for (Iterator it = list.iterator(); it.hasNext();) {
      result.add(new ArcParser((String) it.next()).parseOneLine());
    }
    return result;
  }

  private static void loadAll(Environment environment, String[] arcPath, List files) throws ParseException, IOException {
    for (Iterator i = files.iterator(); i.hasNext();) {
      String f = (String) i.next();
      loadFile(environment, arcPath, f);
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

  private static void repl(Environment environment) throws ParseException {
    ArcParser parser = new ArcParser(System.in);
    while (true) {
      System.out.print("arc> ");
      ArcObject expression = parser.parseOneLine();
      interpret(environment, expression);
    }
  }

  private static void interpret(Environment environment, ArcObject expression) {
    try {
      System.out.println(compileAndEval(environment, expression));
    } catch (ArcError e) {
      System.out.println("Message    : " + e.getMessage());
      System.out.println("Arc stack  : " + e.getStacktrace());
      System.out.print("Java stack : ");
      e.printStackTrace(System.out);
    }
  }

  public static File find(String[] arcPath, String filePath) throws IOException {
    for (String s : arcPath) {
      File f = new File(s + "/" + filePath);
      if (f.exists() && !f.isDirectory()) {
        return f.getCanonicalFile();
      }
    }
    throw new FileNotFoundException("Could not find " + filePath + " under " + toList(arcPath));
  }

  private static List toList(String[] arcPath) {
    return new ArrayList(Arrays.asList(arcPath));
  }

  public static void loadFile(Environment arc, String[] arcPath, String path) throws ParseException, IOException {
    load(arc, path, new FileInputStream(find(arcPath, path)));
  }

  private static void load(Environment arc, String name, InputStream stream) throws ParseException {
    ArcParser parser = new ArcParser(stream);
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

  public static Hash getEnvironment() throws ParseException {
    Hash env = new Hash();
    Map<String, String> s = System.getenv();
    for (String k : s.keySet()) {
      env.sref(ArcString.make(k), ArcString.make(s.get(k)));
    }
    return env;
  }

  public static String[] getArcPath() {
    String arcPath = System.getenv("ARC_PATH");
    if (arcPath == null || arcPath.trim().length() == 0) {
      return new String[] {"."};
    }

    String[] strings = arcPath.split(":");
    List elements = Arrays.asList(strings);
    String cwd = ".";
    for (Object element : elements) {
      if (cwd.equals(element)) {
        return strings;
      }
    }
    
    List result = new ArrayList();
    result.add(cwd);
    result.addAll(elements);
    
    return (String[]) result.toArray(new String[result.size()]);
  }
}
