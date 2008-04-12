package rainbow;

import rainbow.parser.ArcParser;
import rainbow.parser.ParseException;
import rainbow.types.ArcObject;
import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.vm.continuations.TopLevelContinuation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestRunner {
  public static void main(String args[]) throws ParseException, IOException {
    Bindings arc = new Bindings();
    Console.loadResource(arc, "/arc/arc.arc");
    loadFile(arc, "tests/unit.arc");
  }

  private static void loadFile(Bindings arc, String resource) throws ParseException, FileNotFoundException {
    System.out.println("loading " + resource);
    ArcParser parser = new ArcParser(new File(resource));
    ArcObject expression = parser.parseOneLine();
    while (expression != null) {
      ArcThread thread = new ArcThread();
      System.out.print(".");
      TopLevelContinuation topLevel = new TopLevelContinuation(thread);
      Interpreter.interpret(thread, arc, topLevel, expression);
      thread.run();
      thread.finalValue();
      expression = parser.parseOneLine();
    }
    System.out.println();
  }
}
