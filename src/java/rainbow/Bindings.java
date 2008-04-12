package rainbow;

import rainbow.types.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;

public class Bindings {
  private static long idSequence = 0;
  private Bindings parent;
  protected Map<String, ArcObject> namespace = new HashMap<String, ArcObject>();
  protected long id;

  public Bindings() {
    this(new TopBindings());
  }

  public Bindings(Bindings parent) {
    this.parent = parent;
    synchronized (Bindings.class) {
      this.id = ++idSequence;
    }
  }

  public void addToNamespace(String s, ArcObject o) {
    if (namespace.containsKey(s)) {
      namespace.put(s, o);
    } else {
      parent.addToNamespace(s, o);
    }
  }

  public void addToLocalNamespace(String s, ArcObject o) {
    namespace.put(s, o);
  }

  public ArcObject lookup(String s) {
    ArcObject result = namespace.get(s);
    return result != null ? result : parent.lookup(s);
  }

  public Bindings getTop() {
    return parent.getTop();
  }

  public String toString() {
    return toString(namespace);
  }

  protected static String toString(Map<String, ArcObject> objectMap) {
    StringBuffer sb = new StringBuffer();
    for (Iterator i = objectMap.keySet().iterator(); i.hasNext();) {
      Object key = i.next();
      sb.append(key).append("\t:\t").append(objectMap.get(key)).append("\n");
    }
    return sb.toString();
  }

  public String fullNamespace() {
    return "Namespace " + id + "\n" + toString() + "=======under=======\n" + parent.fullNamespace();
  }

  public java.util.Set keys() {
    java.util.Set s = new HashSet();
    collectKeys(s);
    return s;
  }

  protected void collectKeys(java.util.Set s) {
    s.addAll(namespace.keySet());
    parent.collectKeys(s);
  }
}
