(require-lib "rainbow/swing")
(require-lib "lib/parser")
(require-lib "lib/source")

(assign unmatched (obj
  unmatched-left-paren              "("
  unmatched-left-bracket            "["
  unmatched-interpolation           "\#("
  unmatched-left-string-delimiter   "\""
  unmatched-right-string-delimiter  "\""
  unmatched-right-paren             ")"
  unmatched-right-bracket           "]"))

(assign syntax-char-names  (obj
  left-paren        "("
  interpolation     "\#("
  left-bracket      "["
  right-paren       ")"
  right-bracket     "]"
  left-string-delimiter  "\""
  right-string-delimiter  "\""
  quote             "'"
  quasiquote        "`"
  unquote           ","
  unquote-splicing  ",@"))

(assign night-colour-scheme (obj
  background        (awt-color 'black)
  caret             (awt-color 'yellow)
  default           (swing-style 'foreground 'white     'background 'black 'bold t)
  syntax            (swing-style 'foreground 'gray)
  unmatched-syntax  (swing-style 'foreground 'black     'background 'red   'bold t )
  paren-match       (swing-style 'foreground 'gray      'background 'blue)
  search-highlight  (swing-style 'background 'yellow)
  sym               (swing-style 'foreground "#D0F0A0")
  sym-string        (swing-style 'foreground "#DDEEEE")
  sym-fn            (swing-style 'foreground 'white  'bold t)
  sym-mac           (swing-style 'foreground "#9090F0"  'bold t)
  string            (swing-style 'foreground "#DDEEEE")
  int               (swing-style 'foreground "#808040")
  char              (swing-style 'foreground "#706090")
  comment           (swing-style 'foreground "#604060"  'italic t)
  selection         (awt-color   "#4040C0")))

(assign day-colour-scheme (obj
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
  comment           (swing-style 'foreground "#909090"  'italic t)
  selection         (awt-color   "#202060")))


(assign font-size            12
        font-family          "Monaco"
        colour-scheme        night-colour-scheme
        file-chooser         (new-file-chooser)
        welder-actions*      (table)
        welder-key-bindings* (table))

(def editor-font () (make-font font-family font-size))

(mac dot () `(editor!caret 'getDot))

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
         (on-close-editor editor))

(defweld save "Save"
         "Save the file in this window"
         (welder-save-editor-content editor))

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
         (eval-these:readall:selected-text editor all-text))

(defweld htmlify  "Htmlify"
         "Convert selected or all code to HTML,
          suitable for copy-pasting into your blog."
         (open-text-area:to-html-string:selected-text editor all-text))

(defweld show-search "Search"
         "Opens the search bar"
         ((editor!search 'show)))

(defweld search-next "Find Again"
         "Search again for the next occurence
          of the current search term"
         ((editor!search 'next)))

(defweld search-prev "Find Previous"
         "Search for the previous occurence
          of the current search term"
         ((editor!search 'prev)))

(defweld dismiss "Dismiss"
         "Closes search bar and unhighlights
          matches"
         ((editor!search 'hide)))

(defweld pop-form "Pop form"
         "Replaces (foo (bar)) with (bar) if the caret is in (bar)"
         (pop-form editor))

(defweld push-form "Push form"
         "Replaces (bar) with ( (bar))"
         (push-form editor))

(defweld undo "Undo"
         "Undo last edit if possible"
         (later (editor!undoer 'undo)))

(defweld redo "Redo"
         "Redo last edit if possible"
         (later (editor!undoer 'redo)))

(defweld duplicate "Duplicate"
         "Duplicate selection or current line"
         (aif (selected-text editor nilfn)
           (duplicate-selection editor it)
           (duplicate-line editor)))

(defweld bigger-font "Enlarge font"
         "Increase font size"
         (zap [+ _ 2] font-size)
         (configure-bean editor!pane 'font (editor-font)))

(defweld littler-font "Reduce font"
         "Reduce font size"
         (zap [- _ 2] font-size)
         (configure-bean editor!pane 'font (editor-font)))

(defweld reset-font "Reset font"
         "Reset font size to default"
         (assign font-size 12)
         (configure-bean editor!pane 'font (editor-font)))

(defweld expand-macro "Macex"
         "Macro-expands the form under the caret"
         (let this-form (find-form-around editor!index (dot))
           (show-macro-expansion (cut (all-text editor) (this-form 2) (this-form 3)))))

(defweld insert-closing-paren "Insert Closing Paren"
  "Inserts a \")\""
  (insert-closing-delimiter editor ")"))

(defweld insert-closing-dbl-quote "Insert Closing \""
  "Inserts a \""
  (insert-closing-delimiter editor "\""))

(defweld kill-form "Kill form"
  "Kill the form under the caret"
  (prn "killing form")
  (whenlet this-form (find-form-around editor!index (dot))
    (prn "target form " this-form)
    (editor!doc 'remove
                (this-form 2)
                (- (this-form 3) (this-form 2)))))

(defweld go-to-source "Go To Source"
  "Go to the source definition of the symbol under caret"
  (ensure-source-index)
  (let this-tok (find-previous-selectable editor!index (dot))
    (whenlet defn (and (token? this-tok 'sym)
                       (car (all-defs cadr.this-tok)))
      (prn this-tok " is defined in " defn)
      (let w (welder (car defn))
        (w!caret 'setDot cadr.defn)))))

(defweld newline-indent "Insert newline and indent"
  "Insert a newline character and indent the new line like the previous line"
  (awhen (previous-indent editor (dot))
    (later (editor!doc 'insertString (dot) it colour-scheme!default))))

(def find-newline (editor position direction fun)
  (withs (text (all-text editor)
          text-len (text-length editor!doc)
          nl nil
          in-bounds [< _ text-len]
          found [is text._ #\newline])
    (loop (= nl position)
          (and (> nl 0) (in-bounds nl) (~found nl))
          (zap [direction _ 1] nl))
    (in-bounds&found&fun nl)))

(def previous-line (editor position fun)
  (let text (all-text editor)
    (if (or (>= position len.text)
            (is (text position) #\newline))
        (-- position))
    (find-newline editor position - (fn (begin)
      (find-newline editor position + (fn (end)
        (fun (cut text begin end) begin end)))))))

(def previous-indent (editor position)
  (previous-line editor (dot) (fn (text begin end)
    (let line (coerce text 'cons)
      (string:cdr:accum ws (whilet ch (whitespace? pop.line) ws.ch))))))

(def duplicate-line (editor)
  (previous-line editor (dot) (fn (text begin end)
    (later (editor!doc 'insertString
                       begin
                       text
                       colour-scheme!default)))))

(def ensure-source-index ()
  (when (or (~bound 'all-defs) (no all-defs))
    (assign all-defs (table))
    (index-defs (all-sources ".") all-defs)))

(def insert-closing-delimiter (editor str)
  (later (editor!doc 'insertString (dot) str colour-scheme!default)
         (editor!caret 'setDot (- (dot) len.str))))

(def show-macro-expansion (text)
  (let editor (welder-w/text (tostring (ppr (macex:parse text))))
    (on-update editor)))

(def find-form-around (index position)
  (let this-form (find-previous-selectable index position)
    (while (no:token? this-form 'syntax 'left-paren)
      (= this-form (find-previous-selectable index (this-form 2))))
    this-form))

(def push-form (editor)
  (let this-form     (find-form-around editor!index (dot))
    (later (editor!doc 'insertString
                       (this-form 3)
                       ")"
                       colour-scheme!default)
           (editor!doc 'insertString
                       (this-form 2)
                       "("
                       colour-scheme!default)
           (editor!caret 'setDot (+ 1 (this-form 2))))))

(def pop-form (editor)
  (withs (this-form     (find-form-around editor!index (dot))
          text-to-copy  (cut (all-text editor) (this-form 2) (this-form 3))
          previous-form (find-form-around editor!index (this-form 2)))
    (later (editor!doc 'remove
                       (previous-form 2)
                       (- (previous-form 3) (previous-form 2)))
           (editor!doc 'insertString
                       (previous-form 2)
                       text-to-copy
                       colour-scheme!default)
           (editor!caret 'setDot (+ 1 (previous-form 2))))))

(def defkey (key binding . others)
  (= welder-key-bindings*.key binding)
  (if others (apply defkey others)))

(defkey 'shift-9     'insert-closing-paren
        'shift-quote 'insert-closing-dbl-quote
        'escape      'dismiss
        'f1          'go-to-source
        'shift-f1    'help
        'ctrl-k      'keystroke-help
        'ctrl-h      'htmlify
        'meta-s      'save
        'f3          'search-next
        'shift-f3    'search-prev
        'f4          'close
        'ctrl-w      'widen
        'ctrl-e      'eval
        'meta-o      'open
        'meta-n      'new
        'enter       'newline-indent
        'meta-f      'show-search
        'meta-back_space    'kill-form
        'meta-d             'duplicate
        'meta-z             'undo
        'meta-p             'pop-form
        'shift-meta-p       'push-form
        'shift-meta-z       'redo
        'shift-meta-equals  'bigger-font
        'meta-minus         'littler-font
        'meta-0             'reset-font
        'f6                 'expand-macro )

(def welder-save-editor-content (editor)
  (aif editor!file (write-file it all-text.editor)))

(def welder-menu (editor)
  (with (label   (fn (item)
                     ((welder-actions* item) 'label))
         action  (fn (item)
                     (((welder-actions* item) 'action) editor)))
    (swing-menubar  (swing-menu "File"   label action
                      '(new open close save save-as quit))
                    (swing-menu "Edit"   label action
                      '(undo redo widen ppr eval htmlify show-search))
                    (swing-menu "Window" label action
                      nil)
                    (swing-menu "Help"   label action
                      '(help keystroke-help)))))

(def welder-key-help (editor)
  (editor!show-help (tostring:htmlify-keybindings welder-key-bindings*)))

(def htmlify-keybindings (bindings)
  (pr "<table border='1' width='100%'>")
  (each (k v) bindings
    (pr "<tr><td>" k "</td>"
        "<td>" welder-actions*.v!label     "</td>"
        "<td>" welder-actions*.v!help-text "</td>"
        "</tr>"))
  (pr "</table>"))

(def welder-help (editor)
  (let tok (token-at editor (dot))
    (if (and tok (isa tok 'sym) (bound tok))
        (welder-help-bound-sym editor tok)
        (welder-help-token editor tok))))

(def welder-help-bound-sym (editor bound-token)
  (editor!show-help "<pre>#((aif (helpstr bound-token) it bound-token))</pre>"))

(def welder-help-token (editor token)
  (editor!show-help "<pre>#(token)</pre>"))

(def token-at (editor dot)
  (find (fn ((kind tok start finish)) 
            (and (no:is kind 'syntax) 
                 (< start dot finish)) ) 
        editor!index))

(def up-select (editor)
  (let selected  (find-previous-selectable editor!index (dot))
    (editor!select-region (aif selected
                               (cddr it)
                               `(,0 ,(text-length editor!doc))))
    (editor!pane 'grabFocus)))

(def find-previous-selectable (source-index current-dot)
  (let (last-index-item previous-index-item) nil
    (catch
      (each (kind tok start finish) source-index
        (if (> start current-dot)
            (if (is (last-index-item 2) current-dot)
                (throw previous-index-item)
                (throw last-index-item))
            (and (> finish current-dot)
                 (or (no:is kind 'syntax)
                     (no (in tok 'right-paren 'right-bracket 'right-string-delimiter))))
            (= previous-index-item  last-index-item
               last-index-item      (list kind tok start finish))))

        (if (and last-index-item (is (last-index-item 2) current-dot))
            (throw previous-index-item)
            (throw last-index-item)))))

(def to-html-fragment (text)
  (pr "<pre class='arc'>")
  (with (render-token (fn (kind tok)
                          (tostring (if (is kind 'char)
                                        (write tok)
                                        (is kind 'comment)
                                        (disp tok)
                                        (is kind 'string-fragment)
                                        (disp tok)
                                        (pr tok))))
         (tkz lns)    (index-source text t))
    (whilet token (pop tkz)
      (let (kind tok start finish) token
        (if (token? token 'syntax)
            (pr "<span class=\"syntax\">" (syntax-char-names tok) "</span>")
            (token? token 'whitespace)
            (pr tok)
            (withs (tok-type (type tok) bound-type nil)
              (if (and tok (is tok-type 'sym) (no:ssyntax tok) (bound tok))
                  (= bound-type (type (eval tok))))
              (pr "<span class=\"" tok-type " " bound-type "\">")
              (pr-escaped (render-token kind tok))
              (pr "</span>"))))))
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
          (is current-char #\")
            (find-right-matching 'right-string-delimiter text position index)
          (aif (and (> position 0) (text (- position 1)))
            (if (is it #\))
                (find-left-matching [in _ 'left-paren 'interpolation] text position index)
                (is it #\")
                (find-left-matching [is _ 'left-string-delimiter] text position index)
                (is it #\])
                (find-left-matching [is _ 'left-bracket] text position index)))))))

(def find-left-matching (match-fn text position index)
  (catch
    (each (kind tok start finish) index
      (if (and (match-fn tok)
               (is kind 'syntax)
               (is finish position))
          (throw start)))))

(def duplicate-selection (editor text)
  (let ins (dot)
    (if (< ins editor!caret!getMark)
        (= ins editor!caret!getMark))
    (later (editor!doc 'insertString
                       ins
                       text
                       colour-scheme!default))))

(def find-right-matching (match text position index)
  (catch
    (each (kind tok start finish) index
      (if (and (is tok match)
               (is start position))
          (throw finish)))))

(def highlight-match (editor position)
  (aif editor!highlighting
       (highlight-position editor (car it) (cadr it) nil))
  (awhen (match-pair (all-text editor) position editor!index)
    (if (< it position) (swap it position))
    (highlight-position editor position (- it 1) t)))

(def highlight-position (editor pos1 pos2 colour)
  (= editor!highlighting (if colour (list pos1 pos2) nil))
  (let attrs (if colour 'paren-match 'syntax)
    (colour-region editor!doc pos1 1 attrs t)
    (colour-region editor!doc pos2 1 attrs t)))

(def token-attribute (tok)
  (let tok-type (type tok)
    (if (and tok (is tok-type 'sym) (~ssyntax tok) (bound tok))
        (bound-symbol-token-attribute tok)
        tok-type)))

(def bound-symbol-token-attribute (asym)
  (let bound-type (sym+ 'sym- (type:eval asym))
    (if (colour-scheme bound-type) bound-type 'sym)))

(def colourise-interval (index doc begin end)
    (colour-region doc begin (- end begin) 'default t)
    (each (kind tok start finish) index
      (if (and (> finish begin) (< start end))
          (aif (and (is kind 'syntax) (syntax-char-names tok))
                 (colour-region doc
                   (if (or (is tok 'right-paren) 
                           (is tok 'right-bracket))
                       (- finish 1)
                       start)
                   (len it)
                   'syntax
                   t)
               (and (is kind 'syntax) (unmatched tok))
                 (colour-region doc
                   start
                   1
                   'unmatched-syntax
                   t)
               (is kind 'comment)
                 (colour-region doc
                   start
                   (- finish start)
                   'comment
                   t)
               (colour-region doc
                 start
                 (- finish start)
                 (token-attribute tok)
                 t)))))

(def colourise (editor)
  (with (pane                   editor!pane 
         doc                    editor!doc 
         (vis-start vis-finish) (visible-text editor!pane))
    (colourise-interval editor!index doc vis-start vis-finish)
    (highlight-match editor (dot))
    (editor!search!hilite)))

(def on-update (editor)
  (= editor!dirty (msec)))

(def follow-updates (editor)
  (when (and editor!dirty (> (- (msec) editor!dirty) 400))
        (wipe editor!dirty)
        (welder-save-editor-content editor)
        (welder-reindex editor)
        (later (colourise editor)
               (editor!frame 'setTitle (welder-window-title editor))))
  (sleep 0.1)
  (follow-updates editor))

(def welder-reindex (editor)
  (let (idx lc) (index-source:all-text editor)
    (= editor!line-count lc)
    (= editor!index idx)))

(def welder-window-title (editor)
  (string (or editor!file "*scratch*") " - "
          editor!line-count " lines, "
          (len editor!index) " tokens - Arc Welder"))

(def welder-open (editor file)
  (= editor!file file)
  (editor!pane 'setText (load-file file)))

(def on-close-editor (editor)
  (wipe (open-welders editor!wname))
  (kill-thread editor!update-thread))

(def welder-keystroke (editor keystroke)
  (handle-keystroke welder-key-bindings*
                    welder-actions*
                    keystroke
                    [_ editor]))

(def colour-region (doc index length colour-key replace)
  (aif (and colour-key colour-scheme.colour-key)
    (doc 'setCharacterAttributes
         index
         length
         it
         replace)))

(def allmatches (pat seq)
  (accum matches
    (for i 0 (- (len seq) (len pat))
      (when (headmatch pat seq i) (matches i)))))

(def make-search-field (editor)
  (withs (tf    (text-field)
          sf    (box 'horizontal tf)
          (hit hits hl-x hl-len search-term) nil
          (search hilite unhilite show hide move next prev) nil)
    (def search  (term)
                 (= hits (allmatches term (all-text editor)))
                 (= search-term term)
                 (wipe hit)
                 (next 0))
    (def show    ()
                 sf!show
                 (sf!getParent 'revalidate)
                 tf!grabFocus)
    (def unhilite ()
                 (when hl-x
                   (colourise-interval editor!index
                                       editor!doc
                                       hl-x (+ hl-x hl-len))
                   (wipe hl-x hl-len)))
    (def hilite  ()
                 (if hit
                     (colour-region editor!doc
                          hit
                          (len search-term)
                          'search-highlight
                          nil)))
    (def move    (hit-fn)
                 (unhilite)
                 (if hits
                     (do
                       (= hit (hit-fn))
                       (= hl-x hit hl-len (len search-term))
                       (editor!caret 'setDot hit)
                       (hilite))
                     (prn "no hits for " search-term)))
    (def next    ((o offset-from-dot 1))
                 (move (fn () (or (find [< (+ offset-from-dot (dot) -1) _] hits)
                                  (car hits)))))
    (def prev    ()
                 (move (fn () (let rhits (rev hits)
                            (or (find [> (dot) _] rhits)
                                (car rhits))))))
    (def hide    ()
                 (wipe hit)
                 (unhilite)
                 sf!hide
                 (sf!getParent 'revalidate))
    (on-key tf k
      (if (is k 'escape)   (hide)
          (is k 'down)     (next)
          (is k 'up)       (prev)
          (is k 'f3)       (next)
          (is k 'shift-f3) (prev)))
    (on-doc-update tf!getDocument (c)
      (search tf!getText))
    (sf 'hide)
    (nobj sf show hide next prev hilite)))

(assign welder-initialisers nil)

(mac welder-init (var . body)
  `(push (fn ,var ,@body) welder-initialisers))

(welder-init (editor)
  (configure-bean editor!pane
    'caretColor      colour-scheme!caret
    'font            (editor-font)
    'selectionColor  colour-scheme!selection
    'background      colour-scheme!background))

(welder-init (editor)
  (on-caret-move editor!pane (event)
    (later (highlight-match editor event!getDot))))

(welder-init (editor)
  (on-doc-update editor!doc  (event)
    (on-update editor)))

(welder-init (editor)
  (let f (frame 360 0 640 1000 "Arc Welder")
    (= editor!frame f)
    (= editor!show-help
       (help-window f))
    (f 'setJMenuBar
       (welder-menu editor))))

(welder-init (editor)
  (let sc (scroll-pane editor!pane colour-scheme!background)
    (editor!frame 'add sc)
    (on-scroll sc!getVerticalScrollBar (e) (later (colourise editor)))))

(welder-init (editor)
  (= editor!undoer (undo-manager))
  (on-edit editor!doc (event)
    (if (no:is "style change" (event!getEdit 'getPresentationName))
        (editor!undoer 'undoableEditHappened event))))

(welder-init (editor)
  (let search (make-search-field editor)
    (editor!frame 'add search!sf)
    (= editor!search search)))

(welder-init (editor)
  (afnwith (m editor!pane!getInputMap)
    (when m
      (map [m 'remove _] (keep [in convert-keystroke._ 'ctrl-w 'ctrl-h] m!allKeys))
      (self m!getParent))))

(welder-init (editor)
  (fill-table editor (list
    'update-thread  (thread (follow-updates editor))
    'handle-key     (fn (keystroke)
                        (welder-keystroke editor keystroke))
    'select-region  (fn ((start finish))
                        (editor!caret 'setDot (if (isa finish 'fn)
                                                  (finish start)
                                                  finish))
                        (editor!caret 'moveDot start)))))
(welder-init (editor)
  (editor!frame 'addWindowListener (obj
    windowClosed (fn (ev) (on-close-editor editor)))))

(assign open-welders (table))

(java-imports javax.swing UIManager)
(java-imports java.lang System)

(def welder-first-time ()
  (UIManager 'setLookAndFeel UIManager.getSystemLookAndFeelClassName)
  (System 'setProperty "apple.laf.useScreenMenuBar" "true")
  (assign welder-first-time nilfn))

(def welder ((o file) (o txt))
  (welder-first-time)
  (let wname (if file canonical-path.file
                 txt  "*scratch(#((len open-welders)))*"
                      "*scratch*")
    (aif open-welders.wname
         (do it!frame!show
             it!frame!toFront
             it)
         (let editor (editor-pane)
           (= open-welders.wname editor)
           (= editor!wname wname)
           (map [_ editor] rev.welder-initialisers)
           (editor!frame 'show)
           (if file (welder-open editor file)
               txt  (editor!pane 'setText txt))
           editor))))

(def welder-w/text (txt)
  (welder nil txt))
