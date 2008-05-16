package rainbow.vm;

import rainbow.types.ArcObject;
import rainbow.ArcError;

public interface Continuation {
  void receive(ArcObject o);

  void error(ArcError error);

  Continuation cloneFor(ArcThread thread);

  void stop();
}
