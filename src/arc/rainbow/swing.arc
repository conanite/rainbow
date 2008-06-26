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
  (let sas (java-new "javax.swing.text.SimpleAttributeSet")
    (each (name value) (pair name-value-pairs)
      (sas 'addAttribute (style-constant name) (if (is value t) t (awt-color value))))
    sas))

(mac courier (font-size) 
  `(java-new "java.awt.Font" "Courier" font-plain* ,font-size))

(mac dim (x y) 
  `(java-new "java.awt.Dimension" ,x ,y))

(mac button (text . action)
  `(let jb (java-new "javax.swing.JButton" ,text)
    (jb 'setRequestFocusEnabled nil)
    (jb 'addActionListener 
        (java-implement "java.awt.event.ActionListener" t (obj actionPerformed (fn (action-event) ,@action))))
    jb))

(def frame (left top width height title)
  (bean "javax.swing.JFrame" 
    'bounds       (list left top width height) 
    'title        title
    'contentPane  (box 'vertical)))

(def panel () (bean "javax.swing.JPanel"))

(def text-field ()
  (let tf (java-new "javax.swing.JTextField")
    (with (height (tf!getMinimumSize 'getHeight)
           width (tf!getMaximumSize 'getWidth))
      (tf 'setMaximumSize (dim width height)))
    tf))

(def text-area () (java-new "javax.swing.JTextArea"))

(mac on-key (component var . actions)
  (w/uniq (gev)
  `(,component 'addKeyListener (java-implement "java.awt.event.KeyListener" nil (obj keyPressed 
    (fn (,gev) (let ,var (convert-key-event ,gev) ,@actions)))))))

(def visible-text (scrolled-pane)
  (let vr (scrolled-pane!getParent 'getViewRect)
    (list (scrolled-pane 'viewToModel vr!getLocation)
          (scrolled-pane 'viewToModel (java-new "java.awt.Point" vr!getMaxX vr!getMaxY)))))

(def editor-pane ()
  (let ed (table)
    (= ed!pane      (java-new "rainbow.cheat.NoWrapTextPane"))
    (= ed!doc       (ed!pane 'getDocument))
    (= ed!caret     (ed!pane 'getCaret))
    (on-key ed!pane keystroke (ed!handle-key keystroke))
    ed))

(def selected-text (editor)
  (aif (editor!pane 'getSelectedText) it
       (all-text editor)))
       
(def all-text (editor)
  (editor!pane 'getText 0 (editor!doc 'getLength)))

(def text-length (doc)
  (doc 'getLength))

(def scroll-pane (component bgcolor) 
  (let jsp (java-new "javax.swing.JScrollPane" component)
    (jsp!getViewport 'setBackground bgcolor)
    jsp))

(def box (orientation . content)
  (let the-box (java-static-invoke "javax.swing.Box" (if (is orientation 'horizontal) 'createHorizontalBox 'createVerticalBox))
    ((afn (components) 
      (if components (do
        (the-box 'add (car components))
        (self (cdr components))))) content)
    the-box))
    
(mac key-dispatcher bindings
   (let bb (pair bindings)
     `(fn (key)
          (if ,@((afn (bb1)   
                      (if bb1
                        (let (key-char body) (car bb1)
                          (cons `(is key ,key-char) (cons body (self (cdr bb1))))))) bb)))))

(mac on-char (component fun)
  `(,component 'addKeyListener (java-implement "java.awt.event.KeyListener" nil
      (obj keyTyped (fn (event) (,fun event!getKeyChar))))))

(def open-text-area (text)
  (let editor (text-area)
    editor!setText.text
    (editor!getCaret 'setDot  0)
    (editor!getCaret 'moveDot (len text))
    (let f (frame 200 200 600 480 "Arc Welder")
      (f 'add (scroll-pane editor (awt-color 'white)))
      f!show)))

(mac on-caret-move (component (var) . body)
  `(,component 'addCaretListener 
    (java-implement "javax.swing.event.CaretListener" t (obj 
      caretUpdate (fn (,var) ,@body)))))

(mac on-doc-update (doc (var) . body)
  `(,doc 'addDocumentListener
    (java-implement "javax.swing.event.DocumentListener" nil (obj
      insertUpdate  (fn (,var) ,@body)
      removeUpdate  (fn (,var) ,@body)))))

(mac later body
  `(java-static-invoke "javax.swing.SwingUtilities" 'invokeLater 
    (java-implement "java.lang.Runnable" t (obj run (fn () ,@body)))))

(def to-swing-action (action context)
  (java-implement "javax.swing.Action" nil (obj
    actionPerformed (fn (event) (action!action context))
    getValue        (fn (s) (if (is s "Name") action!label))
    isEnabled       (fn () t)
  )))

(def swing-menu (name context . items)
  (let jm (java-new "javax.swing.JMenu" name)
    (each item items
      (jm 'add (to-swing-action item context)))
    jm))

(def swing-menubar menus
  (let jmb (java-new "javax.swing.JMenuBar")
    (each menu menus
      (jmb 'add menu))
    jmb))

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
  (let ks ((java-static-invoke "javax.swing.KeyStroke" 'getKeyStrokeForEvent event) 'toString)
    (coerce (downcase (subst "-" " " (subst "" "pressed " ks))) 'sym)))

(mac create-action (label help-text . body)
  `(obj label ,label help-text ,help-text action (fn () ,@body)))

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
