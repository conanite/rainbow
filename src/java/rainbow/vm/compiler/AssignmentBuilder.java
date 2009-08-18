package rainbow.vm.compiler;

import rainbow.ArcError;
import rainbow.types.ArcObject;
import rainbow.types.Pair.NotPair;
import rainbow.vm.VM;
import static rainbow.vm.compiler.Compiler.compile;
import rainbow.vm.interpreter.Assignment;

import java.util.Map;

public class AssignmentBuilder {
  public static ArcObject build(VM vm, ArcObject body, Map[] lexicalBindings) {
    Assignment assignment = new Assignment();
    assignment.prepare((int) body.len());
    while (!body.isNil()) {
      try {
        body.mustBePairOrNil();
      } catch (NotPair notPair) {
        throw new ArcError("assign: unexpected: " + body);
      }
      assignment.add(compile(vm, body.car(), lexicalBindings));
      body = body.cdr();
    }
    return assignment;
  }
}
