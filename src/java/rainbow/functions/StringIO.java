package rainbow.functions;

import rainbow.types.*;

public class StringIO {
  public static class OutString extends Builtin {
    public ArcObject invoke(Pair args) {
      return new StringOutputPort();
    }
  }

  public static class Inside extends Builtin {
    public ArcObject invoke(Pair args) {
      StringOutputPort sop = cast(args.car(), StringOutputPort.class);
      return sop.value();
    }
  }

  public static class InString extends Builtin {
    public ArcObject invoke(Pair args) {
      return new StringInputPort(cast(args.car(), ArcString.class).value());
    }
  }
}
