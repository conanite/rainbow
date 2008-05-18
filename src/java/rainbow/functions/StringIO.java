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
      StringOutputPort sop = StringOutputPort.cast(args.car(), this);
      return sop.value();
    }
  }

  public static class InString extends Builtin {
    public ArcObject invoke(Pair args) {
      return new StringInputPort(ArcString.cast(args.car(), this).value());
    }
  }
}
