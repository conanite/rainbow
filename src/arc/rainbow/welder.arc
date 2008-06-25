(set night-colour-scheme (obj
  background        (awt-color 'black)
  caret             (awt-color 'white)
  default           (swing-style-attributes 'Foreground (awt-color 'gray) 'Background (awt-color 'black))
  syntax            (swing-style-attributes 'Foreground (awt-color 'gray))
  unmatched-syntax  (swing-style-attributes 'Foreground (awt-color 'black) 'Bold t 'Background (awt-color 'red))
  paren-match       (swing-style-attributes 'Foreground (awt-color 'gray) 'Background (awt-color 'blue))
  sym               (swing-style-attributes 'Foreground (awt-color "#80D080"))
  sym-string        (swing-style-attributes 'Foreground (awt-color "#80D080"))
  sym-fn            (swing-style-attributes 'Foreground (awt-color "#C0D0C0") 'Bold t)
  sym-mac           (swing-style-attributes 'Foreground (awt-color "#9090B0") 'Bold t)
  string            (swing-style-attributes 'Foreground (awt-color "#C0D0D0"))
  int               (swing-style-attributes 'Foreground (awt-color "#808040"))
  char              (swing-style-attributes 'Foreground (awt-color "#706090"))
  comment           (swing-style-attributes 'Foreground (awt-color "#604060") 'Italic t)))

(set day-colour-scheme (obj
  background        (awt-color 'white)
  caret             (awt-color 'black)
  default           (swing-style-attributes 'Foreground (awt-color "#444444") 'Background (awt-color 'white))
  syntax            (swing-style-attributes 'Foreground (awt-color 'gray))
  unmatched-syntax  (swing-style-attributes 'Foreground (awt-color 'white) 'Bold t 'Background (awt-color 'red))
  paren-match       (swing-style-attributes 'Foreground (awt-color 'black) 'Background (awt-color "#8080FF"))
  sym               (swing-style-attributes 'Foreground (awt-color "#206020"))
  sym-string        (swing-style-attributes 'Foreground (awt-color "#602020"))
  sym-fn            (swing-style-attributes 'Foreground (awt-color "#202060") 'Bold t)
  sym-mac           (swing-style-attributes 'Foreground (awt-color "#802080") 'Bold t)
  string            (swing-style-attributes 'Foreground (awt-color "#206060"))
  int               (swing-style-attributes 'Foreground (awt-color "#808040"))
  char              (swing-style-attributes 'Foreground (awt-color "#706090"))
  comment           (swing-style-attributes 'Foreground (awt-color "#909090") 'Italic t)))

(set colour-scheme day-colour-scheme)
(set file-chooser (new-file-chooser))
(set welder-actions* (table))
(set welder-key-bindings* (table))

(mac defweld (name label help-text . body)
  `(= (welder-actions* ',name)
      (obj label ,label help-text ,help-text action (fn (editor) ,@body))))

(defweld new "New"
         "Open a new, empty welder window"
         (welder))

(defweld open "Open"
         "Choose a file and open it in a new welder window"
         (choose-open-file file-chooser f (welder f)))

(defweld close "Close"
         "Close this welder window"
         (editor!frame 'dispose)
         (kill-thread editor!update-thread))

(defweld save "Save"
         "Save the file in this window"
         (write-file editor!file all-text.editor))

(defweld save-as "Save As ..."
         "Choose a file to save the text in this window to"
         (choose-save-file file-chooser f
            (write-file f all-text.editor)
            (= editor!file f) 
            (editor!frame 'setTitle (welder-window-title editor))))

(defweld quit "Quit"
         "Close all welder windows and exit arc"             
         (quit))

(defweld help "Context Help"
         "Show help for symbol under caret"             
         (welder-help editor))

(defweld keystroke-help "Keystroke Help"
         "Show key bindings"             
         (prn "keystroke-help") (welder-key-help editor))

(defweld widen "Widen Selection"
         "Expand the selection to the token under caret,
          to the containing list, and so on, up to the whole file"
         (up-select editor))

(defweld ppr "Pretty Print"
         "Use 'ppr from pprint.arc to relayout code"
         (editor!pane 'setText (tostring:ppr-exprs:readall:all-text editor)))

(defweld eval "Eval"
         "Evaluate selected or all code"             
         (eval-these:readall:selected-text editor))

(defweld htmlify  "Htmlify"
         "Convert selected or all code to HTML, suitable for copy-pasting into your blog."
         (open-text-area:to-html-string:selected-text editor))

(defweld recolour "Colourise"
         "re-index and update code colouring"
         (welder-reindex editor) (colourise editor))

(def defkey (key binding) 
  (= welder-key-bindings*.key binding))

(defkey 'f1      'help           )
(defkey 'ctrl-k  'keystroke-help )
(defkey 'ctrl-h  'htmlify        )
(defkey 'meta-s  'save           )
(defkey 'f4      'close          )
(defkey 'ctrl-w  'widen          )
(defkey 'ctrl-e  'eval           )
(defkey 'meta-o  'open           )
(defkey 'ctrl-l  'recolour       )
(defkey 'meta-n  'new            )

(def welder-menu (editor)
  (let a welder-actions*
    (swing-menubar  (swing-menu "File" editor a!new a!open a!close a!save a!save-as a!quit)
                    (swing-menu "Edit" editor a!widen a!ppr a!eval a!htmlify a!recolour)
                    (swing-menu "Help" editor a!help a!keystroke-help))))

(def welder-key-help (editor)
  (prn "welder-key-help")
  (editor!show-help (tostring:htmlify-keybindings welder-key-bindings*)))

(def htmlify-keybindings (bindings)
  (pr "<table border='1' width='100%'>")
  (ontable k v bindings 
    (pr "<tr><td>" k "</td>" 
        "<td>" (welder-actions*.v 'label) "</td>"
        "<td>" (welder-actions*.v 'help-text) "</td>"
        "</tr>"))
  (pr "</table>"))

(def welder-help (editor)
  (withs (dot     (editor!caret 'getDot) 
          tok     (token-at editor dot)
          the-tok (if tok (read-atom tok)))
    (if (and the-tok (is (type the-tok) 'sym) (bound the-tok))
        (welder-help-bound-sym editor the-tok)
        (welder-help-token editor the-tok))))

(def welder-help-bound-sym (editor bound-token)
  (editor!show-help (+ "<pre>" (aif (helpstr bound-token) it (tostring:pr bound-token)) "</pre>")))

(def welder-help-token (editor token)
  (editor!show-help (+ "<pre>" (string token) "</pre>")))

(def token-at (editor dot)
  (catch
    (each (tok start finish) editor!index
      (if (and (no:is (type tok) 'sym) (< start dot finish))
        (throw tok)))))

(def up-select (editor)
  (withs (text      (all-text editor)
          dot       (editor!caret 'getDot) 
          selected  (find-previous-selectable editor!index dot))
    (if selected
        (do (editor!caret 'setDot  (selected 2))
            (editor!caret 'moveDot (selected 1)))
        (do (editor!caret 'setDot  0)
            (editor!caret 'moveDot (editor!doc 'getLength))))
    (editor!pane 'grabFocus)))

(def find-previous-selectable (source-index current-dot)
  (with (last-index-item nil previous-index-item nil)
    (catch
      (each index-item source-index
        (if (> (index-item 1) current-dot)
               (if (is (last-index-item 1) current-dot)
                   (throw previous-index-item)
                   (throw last-index-item))
            (> (index-item 2) current-dot)
              (do (= previous-index-item last-index-item)
                  (= last-index-item index-item)))))))

(def render-token (tok tok-atom)
  (tostring
    (if (is (type tok) 'char)         (write tok)
        (is (type tok) 'comment)      (disp (rep tok))
        (is (type tok-atom) 'string)  (disp tok)
                                      (pr (coerce tok 'string)))))

(def to-html-fragment (text)
  (pr "<pre class='arc'>")
  (read-arc-tokens (instring text) (fn (tok start finish)
    (if (syntax-char-names tok)
          (pr "<span class=\"syntax\">" (syntax-char-names tok) "</span>")
        (ws-char-names tok)
          (pr (ws-char-names tok))
          (withs (this-tok (read-atom tok) tok-type (type this-tok) bound-type nil)
            (if (and this-tok (is tok-type 'sym) (no:ssyntax tok) (bound this-tok)) 
                (= bound-type (type (eval this-tok))))
            (pr "<span class=\"" tok-type " " bound-type "\">") 
            (pr-escaped (render-token tok this-tok))
            (pr "</span>")))))
  (pr "</pre>"))
  
(def to-html-string (text)
  (tostring:to-html-fragment text))

(def ppr-exprs (exprs)
  (if (acons exprs)
      (do (ppr (car exprs))
          (prn)
          (prn)
          (ppr-exprs (cdr exprs)))))
          
(def match-pair (text position index)
  (if (< position (len text))
    (let current-char (text position)
      (if (is current-char #\()
            (find-right-matching 'right-paren text position index)
          (is current-char #\[)
            (find-right-matching 'right-bracket text position index)
          (aif (and (> position 0) (text (- position 1)))
            (if (is it #\))
              (find-left-matching 'left-paren text position index)
            (is it #\])
              (find-left-matching 'left-bracket text position index)))))))

(def find-left-matching (match text position index)
  (catch
    (each (tok start finish) index
      (if (and (is tok match)
               (is finish position))
          (throw start)))))

(def find-right-matching (match text position index)
  (catch
    (each (tok start finish) index
      (if (and (is tok match) 
               (is start position)) 
          (throw finish)))))

(def highlight-match (editor position)
  (aif editor!highlighting (highlight-position editor (car it) (cadr it) nil))
  (aif (match-pair (all-text editor) position editor!index)
    (do
      (if (< it position) (swap it position))
      (highlight-position editor position (- it 1) t))))

(def highlight-position (editor pos1 pos2 colour)
  (= editor!highlighting (if colour list.pos1.pos2 nil))
  (let attrs (colour-scheme (if colour 'paren-match 'syntax))
    (editor!doc 'setCharacterAttributes pos1 1 attrs t)
    (editor!doc 'setCharacterAttributes pos2 1 attrs t)))

(def token-attribute (tok)
  (withs (this-tok (read-atom tok) tok-type (type this-tok))
    (if (and this-tok (is tok-type 'sym) (no:ssyntax tok) (bound this-tok))
        (bound-symbol-token-attribute this-tok)
        tok-type)))

(def bound-symbol-token-attribute (asym)
  (let bound-type (coerce (+ "sym-" (coerce (type:eval asym) 'string)) 'sym)
    (if (colour-scheme bound-type) bound-type 'sym)))

(def colourise (editor)
  (with (pane editor!pane doc editor!doc (vis-start vis-finish) (visible-text editor!pane))
    (pane 'setCaretColor colour-scheme!caret)
    (pane 'setFont (courier 12))
    (pane 'setBackground colour-scheme!background)
    (doc 'setCharacterAttributes vis-start (- vis-finish vis-start) colour-scheme!default t)
    (each (tok start finish) editor!index
;      (if (and (> finish vis-start) (< start vis-finish))
          (aif (syntax-char-names tok)
                 (doc 'setCharacterAttributes 
                   (if (or (is tok 'right-paren) (is tok 'right-bracket)) (- finish 1) start) 
                   (len it)
                   colour-scheme!syntax
                   t)
               (unmatched tok)
                 (doc 'setCharacterAttributes 
                   start 
                   1
                   colour-scheme!unmatched-syntax 
                   t)
               (colour-scheme (token-attribute tok))
                 (doc 'setCharacterAttributes start (- finish start) it t))))) ;)

(def on-update (editor event)
  (= editor!dirty (msec)))

(def follow-updates (editor)
  (thread ((afn ()
    (if (and editor!dirty (> (- (msec) editor!dirty) 300))
        (do
          (wipe editor!dirty)
          (welder-reindex editor)
          (later (colourise editor)
                 (editor!frame 'setTitle (welder-window-title editor)))))
    (sleep 0.3)
    (self)))))

(def welder-reindex (editor)
  (= editor!index (index-source:all-text editor)))

(def welder-window-title (editor)
  (+ (or editor!file "*scratch*") " - " (string (len editor!index)) " tokens - Arc Welder" ))

(def welder-open (editor file)
  (= editor!file file)
  (editor!pane 'setText (load-file file)))

(def welder-keystroke (editor keystroke)
  (aif welder-key-bindings*.keystroke
    ((welder-actions*.it 'action) editor)))

(def search-field (editor)
  (withs (tf (text-field) sf (box 'horizontal tf))
    (on-key tf k (if (is k 'escape) sf!hide))
    (on-doc-update tf!getDocument (c) (prn "search for" tf!getText))
    sf))

(def welder ((o file))
  (let editor (editor-pane)
    (on-caret-move editor!pane (event) (later (highlight-match editor event!getDot)))
    (on-doc-update editor!doc  (event) (on-update editor event))
    (= editor!update-thread (follow-updates editor))
    (= editor!handle-key (fn (keystroke) (welder-keystroke editor keystroke)))
    (with (f  (frame 150 150 800 800 "Arc Welder")
           sc (scroll-pane editor!pane colour-scheme!background))
      (= editor!frame f)
      (f 'add sc)
;      (f 'add (search-field editor))
      (f 'setJMenuBar (welder-menu editor))
      (= editor!show-help (help-window f))
      f!show
      (if file (welder-open editor file)))))
