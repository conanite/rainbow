package rainbow.cheat;

import javax.swing.*;

public class NoWrapTextPane extends JTextPane {
  public boolean getScrollableTracksViewportWidth() {
    return false;
  }
}

