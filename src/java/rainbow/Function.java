package rainbow;

import rainbow.types.ArcObject;
import rainbow.types.Pair;

public interface Function {
  ArcObject invoke(LexicalClosure lc, Pair args);
}
