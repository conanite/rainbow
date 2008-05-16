package rainbow.util;

import java.util.*;

public class Argv {
  private List args;
  private final Map parsed;
  
  public Argv(String[] args) {
    this.args = new ArrayList(Arrays.asList(args));
    parsed = new HashMap();
  }
  
  public List terminal(String option) {
    List result = new ArrayList();
    int index = args.indexOf(option);
    if (index > -1) {
      result.addAll(args.subList(index + 1, args.size()));
      args = args.subList(0, index);
    }
    parsed.put(option, result);
    return result;
  }
  
  public List multi(String option) {
    boolean add = false;
    List result = new ArrayList();
    for (Iterator it = args.iterator(); it.hasNext();) {
      String arg = (String) it.next();
      if (arg.equals(option)) {
        add = true;
      } else if (arg.startsWith("-")) {
        add = false;
      } else if (add) {
        result.add(arg);
      }
    }
    parsed.put(option, result);
    return result;
  }
  
  public boolean present(String option) {
    for (int i = 0; i < args.size(); i++) {
      String arg = (String) args.get(i);
      if (arg.equals(option)) {
        return true;
      }
    }
    return false;
  }
}
