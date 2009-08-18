package rainbow.functions.java;

import rainbow.functions.Builtin;
import rainbow.types.*;

public class JavaImplement extends Builtin {
  public JavaImplement() {
    super("java-implement");
  }

  public ArcObject invoke(Pair args) {
    ArcObject interfaces = args.car();
    if (interfaces instanceof ArcString) {
      interfaces = Pair.buildFrom(interfaces);
    }
    ArcObject strict = args.cdr().car();
    Hash functions = Hash.cast(args.cdr().cdr().car(), this);
    return JavaProxy.create(Pair.cast(interfaces, this), functions, strict);
  }
}
