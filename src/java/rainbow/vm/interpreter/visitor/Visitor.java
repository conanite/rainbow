package rainbow.vm.interpreter.visitor;

import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.vm.interpreter.*;

public class Visitor {

  public void acceptObject(ArcObject o) {

  }

  public void endObject(ArcObject o) {

  }

  public void accept(InterpretedFunction o) {
    acceptObject(o);
  }

  public void accept(Assignment o) {
    acceptObject(o);
  }

  public void accept(BoundSymbol o) {
    acceptObject(o);
  }

  public void accept(Else o) {
    acceptObject(o);
  }

  public void accept(IfClause o) {
    acceptObject(o);
  }

  public void accept(IfThen o) {
    acceptObject(o);
  }

  public void accept(Invocation o) {
    acceptObject(o);
  }

  public void accept(LastAssignment o) {
    acceptObject(o);
  }

  public void accept(LastIfThen o) {
    acceptObject(o);
  }

  public void accept(QuasiQuotation o) {
    acceptObject(o);
  }

  public void accept(Quotation o) {
    acceptObject(o);
  }

  public void accept(SingleAssignment o) {
    acceptObject(o);
  }

  public void end(InterpretedFunction o) {
    endObject(o);
  }

  public void end(Assignment o) {
    endObject(o);
  }

  public void end(Else o) {
    endObject(o);
  }

  public void end(IfClause o) {
    endObject(o);
  }

  public void end(IfThen o) {
    endObject(o);
  }

  public void end(Invocation o) {
    endObject(o);
  }

  public void end(LastAssignment o) {
    endObject(o);
  }

  public void end(LastIfThen o) {
    endObject(o);
  }

  public void end(QuasiQuotation o) {
    endObject(o);
  }

  public void end(Quotation o) {
    endObject(o);
  }

  public void end(SingleAssignment o) {
    endObject(o);
  }
}
