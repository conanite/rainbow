(set night-colour-scheme (obj
  background        (awt-color 'black)
  caret             (awt-color 'white)
  default           (swing-style 'foreground 'gray      'background 'black)
  syntax            (swing-style 'foreground 'gray)
  unmatched-syntax  (swing-style 'foreground 'black     'background 'red   'bold t )
  paren-match       (swing-style 'foreground 'gray      'background 'blue)
  search-highlight  (swing-style 'background 'yellow)
  sym               (swing-style 'foreground "#80D080")
  sym-string        (swing-style 'foreground "#80D080")
  sym-fn            (swing-style 'foreground "#C0D0C0"  'bold t)
  sym-mac           (swing-style 'foreground "#9090B0"  'bold t)
  string            (swing-style 'foreground "#C0D0D0")
  int               (swing-style 'foreground "#808040")
  char              (swing-style 'foreground "#706090")
  comment           (swing-style 'foreground "#604060"  'italic t)))

(set day-colour-scheme (obj
  background        (awt-color 'white)
  caret             (awt-color 'black)
  default           (swing-style 'foreground "#444444"  'background 'white)
  syntax            (swing-style 'foreground 'gray)
  unmatched-syntax  (swing-style 'foreground 'white     'background 'red       'bold t)
  paren-match       (swing-style 'foreground 'black     'background "#8080FF")
  search-highlight  (swing-style 'background 'yellow)
  search-selected   (swing-style 'background 'blue)
  sym               (swing-style 'foreground "#206020")
  sym-string        (swing-style 'foreground "#602020")
  sym-fn            (swing-style 'foreground "#202080"  'bold t) 
  sym-mac           (swing-style 'foreground "#A02080"  'bold t)
  string            (swing-style 'foreground "#003030"  'bold t)
  int               (swing-style 'foreground "#808040")
  char              (swing-style 'foreground "#706090")
  comment           (swing-style 'foreground "#909090"  'italic t)))

(set colour-scheme day-colour-scheme)
(set file-chooser (new-file-chooser))
(set welder-actions* (table))
(set welder-key-bindings* (table))

(mac defweld (name label help-text . body)
  `(= (welder-actions* ',name)
      (obj label ,label 
           help-text ,help-text 
           action (fn (editor) (on-err 
             (fn (ex) (prn ,label " failed: " (details ex)))
             (fn () ,@body))))))

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
         (welder-key-help editor))

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

(defweld show-search "Search"
         "Opens the search bar"
         (editor!show-search))

(defweld undo "Undo"
         "Undo last edit if possible"
         (editor!undoer 'undo))

(defweld redo "Redo"
         "Redo last edit if possible"
         (editor!undoer 'redo))

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
(defkey 'meta-n  'new            )
(defkey 'meta-f  'show-search    )
(defkey 'meta-z  'undo           )
(defkey 'shift-meta-z  'redo     )

(def welder-menu (editor)
  (with (label   (fn (item)
                     (welder-actions*.item 'label))
         action  (fn (item)
                     ((welder-actions*.item 'action) editor)))
    (swing-menubar  (swing-menu "File" label action '(new open close save save-as quit))
                    (swing-menu "Edit" label action '(undo redo widen ppr eval htmlify show-search))
                    (swing-menu "Help" label action '(help keystroke-help)))))

(def welder-key-help (editor)
  (editor!show-help (tostring:htmlify-keybindings welder-key-bindings*)))

(def htmlify-keybindings (bindings)
  (pr "<table border='1' width='100%'>")
  (ontable k v bindings 
    (pr "<tr><td>" k "</td>" 
        "<td>" (welder-actions*.v 'label) "</td>"
        "<td>" (welder-actions*.v 'help-text) "</td>"
        "</tr>"))
  (pr "</table>"))

(mac dot () `(editor!caret 'getDot))

(def welder-help (editor)
  (withs (tok     (token-at editor (dot))
          the-tok (if tok (read-atom tok)))
    (if (and the-tok (isa the-tok 'sym) (bound the-tok))
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
  (let selected  (find-previous-selectable editor!index (dot))
    (editor!select-region (aif selected
                               (cdr it)
                               `(,0 ,(text-length editor!doc))))
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
  (read-arc-tokens (instring text) (fn (return tok start finish)
    (if (syntax-char-names tok)
          (pr "<span class=\"syntax\">" (syntax-char-names tok) "</span>")
        (ws-char-names tok)
          (pr (ws-char-names tok))
          (withs (this-tok (read-atom tok) tok-type (type this-tok) bound-type nil)
            (if (and this-tok (is tok-type 'sym) (no:ssyntax tok) (bound this-tok)) 
                (= bound-type (type (eval this-tok))))
            (pr "<span class=\"" tok-type " " bound-type "\">") 
            (pr-escaped (render-token tok this-tok))
            (pr "</span>")))
    (return nil)))
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
  (let attrs (if colour 'paren-match 'syntax)
    (colour-region editor!doc pos1 1 attrs t)
    (colour-region editor!doc pos2 1 attrs t)))

(def token-attribute (tok)
  (withs (this-tok (read-atom tok) tok-type (type this-tok))
    (if (and this-tok (is tok-type 'sym) (no:ssyntax tok) (bound this-tok))
        (bound-symbol-token-attribute this-tok)
        tok-type)))

(def bound-symbol-token-attribute (asym)
  (let bound-type (sym+ 'sym- (type:eval asym))
    (if (colour-scheme bound-type) bound-type 'sym)))

(def colourise-interval (index doc begin end)
    (colour-region doc begin (- end begin) 'default t)
    (each (tok start finish) index
      (if (and (> finish begin) (< start end))
          (aif (syntax-char-names tok)
                 (colour-region doc
                   (if (or (is tok 'right-paren) (is tok 'right-bracket)) (- finish 1) start) 
                   (len it)
                   'syntax
                   t)
               (unmatched tok)
                 (colour-region doc
                   start 
                   1
                   'unmatched-syntax 
                   t)
               (colour-region doc 
                 start 
                 (- finish start) 
                 (token-attribute tok)
                 t)))))

(def colourise (editor)
  (with (pane editor!pane doc editor!doc (vis-start vis-finish) (visible-text editor!pane))
    (colourise-interval editor!index doc vis-start vis-finish)
    (highlight-match editor (dot))))

(def on-update (editor)
  (= editor!dirty (msec)))

(def follow-updates (editor)
  (if (and editor!dirty (> (- (msec) editor!dirty) 500))
      (do
        (wipe editor!dirty)
        (welder-reindex editor)
        (later (colourise editor)
               (editor!frame 'setTitle (welder-window-title editor)))))
  (sleep 0.1)
  (follow-updates editor))

(def welder-reindex (editor)
  (= editor!index (index-source:all-text editor)))

(def welder-window-title (editor)
  (+ (or editor!file "*scratch*") " - " (string (len editor!index)) " tokens - Arc Welder"))

(def welder-open (editor file)
  (= editor!file file)
  (editor!pane 'setText (load-file file)))

(def welder-keystroke (editor keystroke)
  (aif welder-key-bindings*.keystroke
    ((welder-actions*.it 'action) editor)))

(def colour-region (doc index length colour-key replace)
  (aif (and colour-key colour-scheme.colour-key)
    (doc 'setCharacterAttributes 
         index 
         length 
         it
         replace)))

(def allmatches (pat seq)
  (rev (accum matches
    (for i 0 (- (len seq) (len pat))
      (when (headmatch pat seq i) (matches i))))))

(def make-search-field (editor)
  (withs (tf    (text-field)
          sf    (box 'horizontal tf)
          (hits hl-x hl-len search-term) nil
          (search unhilite show hide move next prev) nil)
    (def search  (term)
                 (= hits (allmatches term (all-text editor)))
                 (= search-term term)
                 (next 0))
    (def unhilite ()
                 (if hl-x
                     (do
                       (colourise-interval editor!index
                                           editor!doc 
                                           hl-x (+ hl-x hl-len))
                       (wipe hl-x hl-len))))
    (def show    ()
                 sf!show   
                 (sf!getParent 'revalidate) 
                 tf!grabFocus)
    (def move    (hit-fn)
                 (unhilite)
                 (if hits
                     (let hit (hit-fn) 
                       (= hl-x hit hl-len (len search-term))
                       (editor!caret 'setDot hit)
                       (colour-region editor!doc
                          hit
                          (len search-term)
                          'search-highlight
                          nil))
                     (prn "no hits for " search-term)))
    (def next    ((o offset-from-dot 1))
                 (move [or (find [< (+ offset-from-dot (dot) -1) _] hits)
                           (car hits)]))
    (def prev    ()
                 (move [let rhits (rev hits)
                            (or (find [> (dot) _] rhits)
                                (car rhits))]))
    (def hide    ()
                 (unhilite)
                 sf!hide
                 (sf!getParent 'revalidate))
    (on-key tf k 
      (if (is k 'escape)   (hide)
          (is k 'down)     (next)
          (is k 'up)       (prev)
          (is k 'f3)       (next)))
    (on-doc-update tf!getDocument (c)
      (search tf!getText))
    (sf 'hide)
    (list sf show hide)))

(def welder ((o file))
  (let editor (editor-pane)
    (configure-bean editor!pane
      'caretColor colour-scheme!caret
      'font       (courier 12)
      'background colour-scheme!background)
    (on-caret-move editor!pane (event) 
      (later (highlight-match editor event!getDot)))
    (on-doc-update editor!doc  (event)
      (on-update editor))
    (with (f  (frame 150 150 800 800 "Arc Welder")
           sc (scroll-pane editor!pane colour-scheme!background)
           undoer (undo-manager)
           (sf show hide) (make-search-field editor))
      (f 'add sc)
      (f 'add sf)
      (f 'setJMenuBar (welder-menu editor))
      (on-scroll sc!getVerticalScrollBar (e) (on-update editor))
      (fill-table editor (list 
        'update-thread  (thread (follow-updates editor))
        'handle-key     (fn (keystroke)
                            (welder-keystroke editor keystroke))
        'frame          f 
        'undoer         undoer
        'show-search    show
        'show-help      (help-window f)
        'select-region  (fn ((start finish))
                            (editor!caret 'setDot (if (isa finish 'fn) 
                                                      (finish start) 
                                                      finish))
                            (editor!caret 'moveDot start))))
      f!show
      (if file (welder-open editor file))
      (on-edit editor!doc (event)
        (if (no:is "style change" (event!getEdit 'getPresentationName))
            (undoer 'undoableEditHappened event))))))
