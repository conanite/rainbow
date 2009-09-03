package rainbow.vm.interpreter;

import rainbow.types.ArcObject;
import rainbow.vm.interpreter.visitor.Visitor;

import java.util.List;

public interface Conditional {
  void add(Conditional c);
  void take(ArcObject expression);
  void addInstructions(List i);
  ArcObject reduce();

  int highestLexicalScopeReference();

  boolean assigns(int nesting);

  boolean hasClosures();

  ArcObject inline(BoundSymbol p, ArcObject arg, boolean unnest, int nesting, int paramIndex);

  int countReferences(int refs, BoundSymbol p);

  ArcObject nest(int threshold);

  void collectReferences(BoundSymbol b, List bs);

  void visit(Visitor v);
}
