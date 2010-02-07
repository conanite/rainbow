(java-imports java
  (lang Integer Float)
  (awt Font event.KeyListener))

(java-imports javax.swing
  KeyStroke JMenuBar JLabel SwingUtilities SwingConstants
  JFileChooser BoxLayout ScrollPaneConstants JOptionPane)

(= java-int-type                Integer.TYPE
   java-float-type              Float.TYPE
   file-chooser-approve*        (JFileChooser APPROVE_OPTION)
   font-plain*                  (Font PLAIN)
   box-layout-vertical*         (BoxLayout Y_AXIS)
   horizontal_scrollbar_always* (ScrollPaneConstants HORIZONTAL_SCROLLBAR_ALWAYS))

(with (float-constructor `(java.awt.Color ,java-float-type ,java-float-type ,java-float-type)
       int-constructor   `(java.awt.Color ,java-int-type ,java-int-type ,java-int-type))
  (defmemo awt-color (r (o g) (o b))
    (if (is (type r) 'sym)      (java-static-field "java.awt.Color" r)
        (is (type r) 'string)   (apply awt-color (from-css-colour r))
        (< r 1.0)               (java-new float-constructor r g b)
                                (java-new int-constructor r g b))))

(defmemo from-css-colour (c)
  (let xfy (fn (ndx)
               (/ (coerce (cut c ndx (+ ndx 2)) 'int 16) 256.0))
    (list (xfy 1) (xfy 3) (xfy 5))))

(defmemo style-constant (name)
  (java-static-field "javax.swing.text.StyleConstants" (upcase-initial name)))

(defmemo swing-style name-value-pairs
  (alet (java-new "javax.swing.text.SimpleAttributeSet")
    (each (name value) (pair name-value-pairs)
      (it 'addAttribute (style-constant name) (if (is value t) t (awt-color value))))))

(def make-font (name size) (Font new name font-plain* size))

(def courier (font-size) (make-font "Courier" font-size))

(mac dim (x y)
  `(java-new "java.awt.Dimension" ,x ,y))

(mac button (text . action)
  (w/uniq (jb)
  `(let ,jb (java-new "javax.swing.JButton" ,text)
    (,jb 'setRequestFocusEnabled nil)
    (,jb 'addActionListener (fn (action-event) ,@action))
    ,jb)))

(def jlabel () (JLabel new))

(mac key-dispatcher bindings
   (w/uniq gkey
     `(fn (,gkey)
          (if ,@(afnwith (bb1 (pair bindings))
                      (if bb1
                        (let (key-char body) (car bb1)
                          (cons `(is ,gkey ,key-char)
                                 (cons body (self (cdr bb1)))))))))))

(mac on-key (component var . actions)
  (w/uniq (gev)
    `(,component 'addKeyListener
                 (fn (,gev)
                     (let ,var (convert-key-event ,gev) ,@actions)))))

(mac on-key-press (component . key-bindings)
  (w/uniq (gev)
    `(,component 'addKeyListener
                 (fn (,gev)
                     ((key-dispatcher ,@key-bindings) (convert-key-event ,gev))))))

(def key-event-test ()
  "run this, focus the window, hit keys on your
   keyboard, the console will show the corresponding
   rainbow key code for use with the on-key and
   on-key-press macros"
  (let f (frame 10 10 200 200)
    (on-key f k (prn k))
    (f 'show)))

(mac on-char (component fun)
  `(,component 'addKeyListener
               (obj keyTyped
                    (fn (event) (,fun event!getKeyChar)))))

(mac trivial-listener (name method)
  `(mac ,name (component (var) . body)
    `(,component ',',method (fn (,var) ,@body))))

(trivial-listener on-scroll     addAdjustmentListener)
(trivial-listener on-caret-move addCaretListener)
(trivial-listener on-edit       addUndoableEditListener)

(mac on-doc-update (doc (var) . body)
  `(,doc 'addDocumentListener (obj
      insertUpdate  (fn (,var) ,@body)
      removeUpdate  (fn (,var) ,@body))))

(def handle-keystroke (bindings actions keystroke f)
  "Looks for keystroke in bindings. If found, result is
   a key into actions. Action is a hash with key 'action.
   passes 'action value of action to f"
  (aif bindings.keystroke
    (f (actions.it 'action))))

(def frame (left top width height title)
  (alet (bean "javax.swing.JFrame"
          'bounds       (list left top width height)
          'title        title)
    (it!getContentPane 'setLayout
                       (BoxLayout new
                                  it!getContentPane
                                  (BoxLayout Y_AXIS)))))

(def panel () (bean "javax.swing.JPanel"))

(def undo-manager () (bean "javax.swing.undo.UndoManager"))

(def jtree (root-node)
  (java-new "javax.swing.JTree" root-node))

(def text-field ()
  (alet (java-new "javax.swing.JTextField")
    (with (height (it!getMinimumSize 'getHeight)
           width (it!getMaximumSize 'getWidth))
      (it 'setMaximumSize (dim width height)))))

(def text-area () (java-new "javax.swing.JTextArea"))

(def visible-text (scrolled-pane)
  (let vr (scrolled-pane!getParent 'getViewRect)
    (list (scrolled-pane 'viewToModel vr!getLocation)
          (scrolled-pane 'viewToModel (java-new "java.awt.Point" vr!getMaxX vr!getMaxY)))))

(def editor-pane ()
  (alet (table)
    (= it!pane      (java-new "rainbow.cheat.NoWrapTextPane"))
    (= it!doc       (it!pane 'getDocument))
    (= it!caret     (it!pane 'getCaret))
    (on-key it!pane k (it!handle-key k))))

(def selected-text (editor else)
  (or editor!pane!getSelectedText
      (else editor)))

(def all-text (editor)
  (editor!pane 'getText 0 editor!doc!getLength))

(def text-length (doc)
  (doc 'getLength))

(def scroll-pane (component bgcolor)
  (alet (java-new "javax.swing.JScrollPane" component)
    (it!getViewport 'setBackground bgcolor)))

(java-import "javax.swing.Box")

(def box (orientation . content)
  (alet (if (is orientation 'horizontal)
            (Box createHorizontalBox)
            (Box createVerticalBox))
    (while content
      (it 'add (pop content)))))

(def open-text-area (text)
  (let editor (text-area)
    (editor 'setText text)
    (editor!getCaret 'setDot  0)
    (editor!getCaret 'moveDot (len text))
    (let f (frame 200 200 600 480 "Arc Welder")
      (f 'add (scroll-pane editor (awt-color 'white)))
      f!show)))

(mac later body
  `(SwingUtilities invokeLater (fn () ,@body)))

(def to-swing-action (item label-fn action-fn)
  (java-implement "javax.swing.Action" nil (make-obj
    (actionPerformed (event) (action-fn item))
    (getValue        (s) (if (is s "Name") (label-fn item)))
    (isEnabled       () t))))

(def swing-menu (name label-fn action-fn items)
  (alet (java-new "javax.swing.JMenu" name)
    (each item items
      (it 'add (to-swing-action item label-fn action-fn)))))

(def swing-menubar menus
  (alet (JMenuBar new)
    (each menu menus
      (it 'add menu))))

(def new-file-chooser ()
  (java-new "javax.swing.JFileChooser"))

(mac jfilechooser (chooser file op . actions)
  `(if (is (,chooser ',op nil) file-chooser-approve*)
       (let ,file ((,chooser 'getSelectedFile) 'getCanonicalPath) ,@actions)))

(mac choose-open-file (chooser file . actions)
  `(jfilechooser ,chooser ,file showOpenDialog ,@actions))

(mac choose-save-file (chooser file . actions)
  `(jfilechooser ,chooser ,file showSaveDialog ,@actions))

(def convert-keystroke (ks)
  (sym:downcase:subst "-" " " (subst "" "pressed " ks!toString)))

(def convert-key-event (event)
  (convert-keystroke (KeyStroke getKeyStrokeForEvent event)))

(mac create-action (label help-text . body)
  `(obj label     ,label
        help-text ,help-text
        action    (fn () ,@body)))

(def help-window (frame)
  (withs (jta (bean "javax.swing.JTextPane"
                'editable nil
                'contentType "text/html")
          w   (bean "javax.swing.JFrame"
                'size               '(600 200)
                'locationRelativeTo frame
                'contentPane        (scroll-pane jta (awt-color 'white))))
    (on-key jta keystroke
      (if (is keystroke 'escape) w!hide))
    (fn (text)
        (jta 'setText text)
        w!show
        jta!grabFocus)))

(def prompt (txt)
  (JOptionPane showInputDialog txt))
