package rainbow.functions.lists;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.ArcError;

public class Car extends Builtin {
  public Car() {
    super("car");
  }

  public ArcObject invoke(Pair args) {
    try {
      args.cdr().mustBeNil();
    } catch (NotNil notNil) {
      throw new ArcError("car: expects only one argument: got " + args);
    }
    return args.car().car();
  }
}
