package rainbow;

import java.util.LinkedList;
import java.util.List;

public class ArcError extends RuntimeException {
  private List arcStack = new LinkedList();

  public ArcError(String message) {
    super(message);
  }

  public ArcError(String message, Throwable e) {
    super(message, e);
  }

  public ArcError(Exception e) {
    super(e);
  }

  public String getMessage() {
    return super.getMessage();
  }
}
