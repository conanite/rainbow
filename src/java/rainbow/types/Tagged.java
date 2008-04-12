package rainbow.types;

public class Tagged extends ArcObject {
  private ArcObject type;
  private ArcObject rep;

  public Tagged(ArcObject type, ArcObject rep) {
    this.type = type;
    this.rep = rep;
  }

  public ArcObject getType() {
    return type;
  }

  public ArcObject getRep() {
    return rep;
  }

  public int compareTo(ArcObject right) {
    return 0;
  }

  public ArcObject type() {
    return type;
  }

  public static boolean hasTag(ArcObject o, String s) {
    return o instanceof Tagged && ((Tagged)o).getType().toString().equals(s);
  }

  public static ArcObject ifTagged(ArcObject o, String tag) {
    if (hasTag(o, tag)) {
      return ((Tagged)o).getRep();
    }
    return null;
  }

  public String toString() {
    return "#<tagged " + type + " " + rep + ">";
  }
}
