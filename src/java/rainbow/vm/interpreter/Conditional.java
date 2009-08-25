package rainbow.vm.interpreter;

import rainbow.types.ArcObject;

import java.util.List;

public interface Conditional {
  void add(Conditional c);
  void take(ArcObject expression);
  void addInstructions(List i);
  ArcObject reduce();

  int highestLexicalScopeReference();

  boolean assigns(BoundSymbol p);

  boolean hasClosures();

  ArcObject inline(BoundSymbol p, ArcObject arg, boolean unnest);
}
