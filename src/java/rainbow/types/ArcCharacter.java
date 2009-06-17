package rainbow.types;

import rainbow.ArcError;

public class ArcCharacter extends ArcObject {
  public static final Symbol TYPE = (Symbol) Symbol.make("char");
  private char value;

  public ArcCharacter(char value) {
    this.value = value;
  }

  public static final ArcCharacter NULL = new ArcCharacter((char) 0) {
    public String toString() {
      return "#\\null";
    }
  };

  private static final ArcCharacter[] named = {
          NULL,
          new ArcCharacter('\n') {
            public String toString() {
              return "#\\newline";
            }
          },
          new ArcCharacter('\t') {
            public String toString() {
              return "#\\tab";
            }
          },
          new ArcCharacter('\r') {
            public String toString() {
              return "#\\return";
            }
          },
          new ArcCharacter(' ') {
            public String toString() {
              return "#\\space";
            }
          },
  };


  public static ArcCharacter make(Character ch) {
    return new ArcCharacter(ch.charValue());
  }

  public static ArcCharacter make(String representation) {
    if (representation.length() == 3) {
      return new ArcCharacter(representation.charAt(2));
    }

    if (representation.startsWith("#\\U") || representation.startsWith("#\\u")) {
      return new ArcCharacter((char) Integer.parseInt(representation.substring(3), 16));
    }

    for (int i = 0; i < named.length; i++) {
      if (named[i].is(representation)) {
        return named[i];
      }
    }

    try {
      int intValue = parseInt(representation);
      return new ArcCharacter((char) intValue);
    } catch (Exception e) {
      throw new ArcError("Can't make character from " + representation);
    }
  }

  private static int parseInt(String representation) {
    return Integer.parseInt(representation.substring(2), 8);
  }

  public boolean literal() {
    return true;
  }

  public String toString() {
    return "#\\" + value;
  }

  private boolean is(String representation) {
    return toString().equals(representation);
  }

  public int compareTo(ArcObject right) {
    ArcCharacter other = (ArcCharacter) right;
    return value - other.value;
  }

  public ArcObject type() {
    return TYPE;
  }

  public Object unwrap() {
    return value();
  }

  public char value() {
    return value;
  }

  public String stringValue() {
    return String.valueOf(value);
  }

  public int hashCode() {
    return new Character(value).hashCode();
  }

  public boolean equals(Object other) {
    return this == other || (other instanceof ArcCharacter && ((ArcCharacter) other).value == this.value);
  }

  public boolean isSame(ArcObject other) {
    return equals(other);
  }

  public static ArcCharacter cast(ArcObject argument, Object caller) {
    try {
      return (ArcCharacter) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a character, got " + argument);
    }
  }
}
