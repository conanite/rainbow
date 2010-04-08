package rainbow;

import rainbow.functions.Environment;
import rainbow.parser.ArcParser;
import rainbow.parser.ParseException;
import rainbow.types.*;
import rainbow.util.Argv;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.interpreter.visitor.Visitor;

import java.io.*;
import java.util.*;

public class Console {
  public static boolean debugJava = false;
  public static boolean stackfunctions = true;

  public static void main(String args[]) throws ParseException, IOException {
    long started = System.currentTimeMillis();
    Object o = ArcObject.NIL;
    String[] path = getArcPath();
    Argv argv = new Argv(args);
    List programArgs = parseAll(argv.terminal("-args"));

    if (argv.present("--nosf")) {
      stackfunctions = false;
    }

    if (argv.present("--help")) {
      showHelp();
      System.exit(0);
    }

    Environment.init();
    VM vm = new VM();
//    vm.setInterceptor(VMInterceptor.DEBUG);

    (Symbol.mkSym("*argv*")).setValue(Pair.buildFrom(programArgs));
    (Symbol.mkSym("*env*")).setValue(getEnvironment());
    (Symbol.mkSym("call*")).setValue(new Hash());
    (Symbol.mkSym("sig")).setValue(new Hash());

    if (!argv.present("--no-libs")) {
      loadFile(vm, path, "arc");
      loadFile(vm, path, "strings");
      loadFile(vm, path, "lib/bag-of-tricks");
      loadFile(vm, path, "rainbow/rainbow");
      loadFile(vm, path, "rainbow/rainbow-libs");
    }

    loadAll(vm, path, argv.multi("-f"));

    interpretAll(vm, argv.multi("-e"));

    if (!argv.present("-q")) {
      long ready = System.currentTimeMillis();
      System.out.println("repl in " + (ready - started) + "ms");
      repl(vm);
    }
  }

  private static void showHelp() {
    System.out.print("" +
            "Launch the Rainbow Arc Interpreter                                  " +
            "\n                                                                  " +
            "\n rainbow [options]                                                " +
            "\n                                                                  " +
            "\n where options include                                            " +
            "\n   --help       show this help                                    " +
            "\n   -f file ...  load and interpret file                           " +
            "\n   -e expr ...  evaluate expr                                     " +
            "\n   -q           don't enter the REPL (quit if no threads started) " +
            "\n   --no-libs    don't load any arc libraries                      " +
            "\n   -args xyz    (if specified must be last) set symbol *argv* to xyz" +
            "\n");
  }

  private static List parseAll(List list) throws ParseException {
    List result = new ArrayList();
    for (Iterator it = list.iterator(); it.hasNext();) {
      result.add(new ArcParser((String) it.next()).parseOneLine());
    }
    return result;
  }

  private static void loadAll(VM vm, String[] arcPath, List files) throws ParseException, IOException {
    for (Iterator i = files.iterator(); i.hasNext();) {
      String f = (String) i.next();
      loadFile(vm, arcPath, f);
    }
  }

  private static void interpretAll(VM vm, List expressionsToEval) throws ParseException {
    StringBuffer sb = new StringBuffer();
    for (Iterator it = expressionsToEval.iterator(); it.hasNext();) {
      String arg = (String) it.next();
      sb.append(arg).append(" ");
    }
    ArcParser parser = new ArcParser(sb.toString());
    ArcObject expression = parser.parseOneLine();
    while (expression != null) {
      interpret(vm, expression);
      expression = parser.parseOneLine();
    }
  }

  private static void repl(VM vm) throws ParseException {
    ArcParser parser = new ArcParser(System.in);
    while (true) {
      System.out.print("arc> ");
      try {
        ArcObject expression = parser.parseOneLine();
        if (expression == null) {
          System.exit(0);
        }
        interpret(vm, expression);
      } catch (ParseException e) {
        e.printStackTrace();
        parser.ReInit(System.in);
      }
    }
  }

  private static void interpret(VM vm, ArcObject expression) {
    try {
      System.out.println(compileAndEval(vm, expression));
    } catch (ArcError e) {
      System.out.println("Message    : " + e.getMessage());
      System.out.print("Java stack : ");
      e.printStackTrace(System.out);
    }
  }

  public static File find(String[] arcPath, String filePath) throws IOException {
    for (String dirName : arcPath) {
      String absFilename = dirName + File.separator + filePath;
      File f = new File(absFilename);

      if (isValidSourceFile(f)) {
        return f.getCanonicalFile();
      } else {
        f = new File(absFilename + ".arc");
        if (isValidSourceFile(f)) {
          return f.getCanonicalFile();
        }
      }
    }
    throw new FileNotFoundException("Could not find " + filePath + " under " + toList(arcPath));
  }

  private static List toList(String[] arcPath) {
    return new ArrayList(Arrays.asList(arcPath));
  }

  public static void loadFile(VM vm, String[] arcPath, String path) throws ParseException, IOException {
    File f = new File(path);

    if (f.getCanonicalPath().equals(path)) {  /* Absolute filename */
      if (!isValidSourceFile(f))
        throw new FileNotFoundException("Could not find " + path);
    } else {
      f = find(arcPath, path);
    }

    load(vm, new FileInputStream(f));
  }

  public static void load(VM vm, InputStream stream) throws ParseException {
    ArcParser parser = new ArcParser(stream);
    ArcObject expression = parser.parseOneLine();
    while (expression != null) {
      compileAndEval(vm, expression);
      expression = parser.parseOneLine();
    }
  }

  private static Visitor mkVisitor(final ArcObject owner) {
    return new Visitor() {
      public void accept(Instruction o) {
        o.belongsTo(owner);
      }
    };
  }

  private static ArcObject compileAndEval(VM vm, ArcObject expression) {
    expression = rainbow.vm.compiler.Compiler.compile(vm, expression, new Map[0]).reduce();
    List i = new ArrayList();
    expression.addInstructions(i);
    Pair instructions = Pair.buildFrom(i);
    instructions.visit(mkVisitor(expression));
    vm.pushInvocation(null, instructions);
    return vm.thread();
  }

  public static Hash getEnvironment() throws ParseException {
    Hash env = new Hash();
    Map<String, String> s = System.getenv();
    for (String k : s.keySet()) {
      env.sref(ArcString.make(s.get(k)), ArcString.make(k));
    }
    return env;
  }

  public static String[] getArcPath() {
    String arcPath = System.getenv("ARC_PATH");
    if (arcPath == null || arcPath.trim().length() == 0) {
      return new String[]{"."};
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

  private static boolean isValidSourceFile(File f) {
    return f.exists() && f.isFile();
  }
}
