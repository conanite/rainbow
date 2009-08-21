package rainbow.functions.maths;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.functions.Builtin;
import rainbow.functions.typing.Typing;
import rainbow.types.ArcNumber;
import rainbow.types.ArcObject;
import rainbow.types.ArcString;
import rainbow.types.Pair;

import java.util.LinkedList;
import java.util.List;

public class Add extends Builtin {
  public Add() {
    super("+");
  }

  public ArcObject invoke(Pair args) {
    if (args.car() instanceof ArcNumber) {
      return sum(args);
    } else if (args instanceof Nil) {
      return sum(args);
    } else if (args.car() instanceof ArcString) {
      try {
        return concat(args);
      } catch (Exception e) {
        throw new ArcError("Adding " + args, e);
      }
    } else if (args.car() instanceof Pair) {
      return joinLists(args);
    } else {
      throw new ArcError("Cannot sum " + args);
    }
  }

  public static ArcNumber sum(Pair args) {
    return Maths.precision(args).sum(args);
  }

  private static ArcString concat(Pair args) {
    return ArcString.make(concatStrings(args));
  }

  private static String concatStrings(Pair args) {
    return (args instanceof Nil) ? "" : coerceString(args.car()) + concatStrings((Pair) args.cdr());
  }

  private static String coerceString(ArcObject o) {
    if (o instanceof ArcString) {
      return ((ArcString) o).value();
    } else {
      return ((ArcString) Typing.coerce(o, ArcString.TYPE, null)).value();
    }
  }

  private static Pair joinLists(Pair args) {
    List list = new LinkedList();
    copyAllTo(args, list);
    return Pair.buildFrom(list, ArcObject.NIL);
  }

  private static void copyAllTo(Pair args, List list) {
    if (args instanceof Nil) {
      return;
    }
    args.car().copyTo(list);
    copyAllTo((Pair) args.cdr(), list);
  }

}
