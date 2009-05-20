(java-import "java.awt.Font")
(java-import "javax.swing.KeyStroke")
(java-import "javax.swing.JMenuBar")
(java-import "java.awt.event.KeyListener")
(java-import "javax.swing.SwingUtilities")

(set file-chooser-approve*        (java-static-field "javax.swing.JFileChooser" 'APPROVE_OPTION))
(set font-plain*                  (java-static-field "java.awt.Font" 'PLAIN))
(set box-layout-vertical*         (java-static-field "javax.swing.BoxLayout" 'Y_AXIS))
(set horizontal_scrollbar_always* (java-static-field "javax.swing.ScrollPaneConstants" 'HORIZONTAL_SCROLLBAR_ALWAYS))

(defmemo awt-color (r (o g) (o b))
  (if (is (type r) 'sym)      (java-static-field "java.awt.Color" r)
      (is (type r) 'string)   (apply awt-color (from-css-colour r))
                              (java-new "java.awt.Color" r g b)))

(defmemo from-css-colour (c)
  (let cc (coerce c 'cons)
    (list (/ (coerce (string (list (c 1) (c 2))) 'int 16) 256.0)
          (/ (coerce (string (list (c 3) (c 4))) 'int 16) 256.0)
          (/ (coerce (string (list (c 5) (c 6))) 'int 16) 256.0))))

(defmemo style-constant (name)
  (java-static-field "javax.swing.text.StyleConstants" (upcase-initial name)))

(defmemo swing-style name-value-pairs
  (alet (java-new "javax.swing.text.SimpleAttributeSet")
    (each (name value) (pair name-value-pairs)
      (it 'addAttribute (style-constant name) (if (is value t) t (awt-color value))))))

(mac courier (font-size)
  `(Font new "Courier" font-plain* ,font-size))

(mac dim (x y)
  `(java-new "java.awt.Dimension" ,x ,y))

(mac button (text . action)
  (w/uniq (jb)
  `(let ,jb (java-new "javax.swing.JButton" ,text)
    (,jb 'setRequestFocusEnabled nil)
    (,jb 'addActionListener (fn (action-event) ,@action))
    ,jb)))

(mac key-dispatcher bindings
   (w/uniq gkey
     `(fn (,gkey)
          (if ,@((afn (bb1)
                      (if bb1
                        (let (key-char body) (car bb1)
                          (cons `(is ,gkey ,key-char)
                                 (cons body (self (cdr bb1))))))) (pair bindings))))))

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

(mac on-char (component fun)
  `(,component 'addKeyListener 
               (fn (event) (,fun event!getKeyChar))))

(mac on-scroll (component (var) . body)
  `(,component 'addAdjustmentListener (fn (,var) ,@body)))

(mac on-caret-move (component (var) . body)
  `(,component 'addCaretListener (fn (,var) ,@body)))

(mac on-doc-update (doc (var) . body)
  `(,doc 'addDocumentListener (fn (,var) ,@body)))

(mac on-edit (doc (var) . body)
  `(,doc 'addUndoableEditListener (make-obj
      (undoableEditHappened (,var) ,@body))))

(def handle-keystroke (bindings actions keystroke f)
  "Looks for keystroke in bindings. If found, result is
   a key into actions. Action is a hash with key 'action.
   passes 'action value of action to f"
  (aif bindings.keystroke
    (f (actions.it 'action))))

(def frame (left top width height title)
  (bean "javax.swing.JFrame"
    'bounds       (list left top width height)
    'title        title
    'contentPane  (box 'vertical)))

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

(def box (orientation . content)
  (alet (java-static-invoke "javax.swing.Box"
                            (if (is orientation 'horizontal)
                                'createHorizontalBox
                                'createVerticalBox))
    ((afn (components)
      (if components (do
        (it 'add (car components))
        (self (cdr components))))) content)))

(def open-text-area (text)
  (let editor (text-area)
    (editor!setText text)
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

(def convert-key-event (event)
  (let ks ((KeyStroke getKeyStrokeForEvent event) 'toString)
    (coerce (downcase (subst "-" " " (subst "" "pressed " ks))) 
            'sym)))

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
