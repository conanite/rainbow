package rainbow.functions.system;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Rational;
import rainbow.Nil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

public class DateTime extends Builtin {
  public DateTime() {
    super("datetime");
  }

  public ArcObject invoke(Pair args) {
    Calendar c = new GregorianCalendar();
    if (!(args instanceof Nil)) {
      c.setTime(new Date(1000 * Rational.cast(args.car(), this).toInt()));
    }
    return Pair.buildFrom(
            Rational.make(c.get(Calendar.SECOND)),
            Rational.make(c.get(Calendar.MINUTE)),
            Rational.make(c.get(Calendar.HOUR_OF_DAY)),
            Rational.make(c.get(Calendar.DAY_OF_MONTH)),
            Rational.make(c.get(Calendar.MONTH)),
            Rational.make(c.get(Calendar.YEAR))
    );
  }
}
