(set welder-stylesheet "
body           { background: black; color: #C0C0C0; font-size:12pt;}
.sym           { color:#80C080;   }
.sym-string    { color:#80D080;   }
.sym-fn        { color:#C040C0; font-weight:bold   }
.sym-mac       { color:#4040C0; font-weight:bold   }
.string        { color:#B0C0C0;    } 
.int           { color:#808040;    } 
.char          { color:#806080;    } 
.syntax        { color:gray;       } 
.comment       { color:604060; font-style:italic;     } 
")

(set syntax-char-names  (obj 
  left-paren        "(" 
  left-bracket      "[" 
  right-paren       ")" 
  right-bracket     "]" 
  quote             "'" 
  quasiquote        "`" 
  unquote           "," 
  unquote-splicing  ",@"))

(def file-control (editor onopen (o startfile))
  (withs (filename (text-field)
          open-button (button "open"
                              (onopen filename!getText)
                              (editor 'setText (to-html-page (load-file filename!getText)))))
    (if startfile (do filename!setText.startfile open-button!doClick))
    (box 'horizontal
         filename
         open-button
         (button "save" (write-file filename!getText (all-text editor))))))

(def welder-buttons (editor)
  (box 'horizontal
       (button "html"             (open-text-area (to-html-string (selected-text editor))))
       (button "expand selection" (up-select editor))
       (button "eval"             (eval-these (readall (selected-text editor))))
       (button "ppr"              (editor 'setText (tostring (ppr-exprs (readall (editor 'getText))))))
       (button "new"              (welder))))
       
(def up-select (editor)
  (withs (text      (all-text editor)
          dot       (editor!getCaret 'getDot) 
          selected  (find-previous-selectable index-source.text dot))
    (if selected
        (do (editor!getCaret 'setDot  (selected 2))
            (editor!getCaret 'moveDot (selected 1)))
        (do (editor!getCaret 'setDot  0)
            (editor!getCaret 'moveDot (text-length editor))))
    editor!grabFocus))

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

(def match-pair (text position)
  (if (and (> position 0) (is (text (- position 1)) #\)))
        (find-left-matching 'left-paren text position)
      (is (text position) #\()
        (find-right-matching 'right-paren text position)
      (and (> position 0) (is (text (- position 1)) #\]))
        (find-left-matching 'left-bracket text position)
      (is (text position) #\[)
        (find-right-matching 'right-bracket text position)))

(def find-left-matching (match text position)
  (catch
    (each (tok start finish) index-source.text
      (if (and (is tok match) 
               (is finish position)) 
          (throw start)))
  nil))

(def find-right-matching (match text position)
  (catch
    (each (tok start finish) index-source.text
      (if (and (is tok match) 
               (is start position)) 
          (throw finish)))
  nil))

(def to-html-page (text)
  (tostring
    (pr "<html><body>")
    (to-html-fragment text)
    (pr "</body></html>")))

(def to-html-fragment (text)
  (pr "<pre class='arc'>")
  (read-arc-tokens (instring text) (fn (tok start finish)
    (if (syntax-char-names tok)
          (pr "<span class=\"syntax\">" (syntax-char-names tok) "</span>")
        (ws-char-names tok)
          (pr (ws-char-names tok))
          (withs (this-tok (read-atom tok) tok-type (type this-tok) bound-type nil)
            (if (and this-tok (is tok-type 'sym) (bound this-tok)) 
                (= bound-type (type (eval this-tok))))
            (pr "<span class=\"" 
                tok-type 
                  (if bound-type (+ "-" (coerce bound-type 'string)) "") 
                "\">") 
            (pr-escaped (render-token tok this-tok))
            (pr "</span>")))))
  (pr "</pre>"))
  
(def to-html-string (text)
  (tostring (to-html-fragment text)))

(def ppr-exprs (exprs)
  (if (acons exprs)
      (do (ppr (car exprs))
          (prn)
          (prn)
          (ppr-exprs (cdr exprs)))))

(def welder ((o file))
  (let editor (html-pane)
    ((editor!getDocument 'getStyleSheet) 'addRule welder-stylesheet)
    (editor 'setCaretColor (awt-color 'white))
    (let f (frame 150 150 800 640 "Arc Welder")
      (f 'add
         (file-control editor 
                       (fn (fname) (f 'setTitle (+ fname " - Arc Welder")))
                       file))
      (f 'add (scroll-pane editor))
      (f 'add (welder-buttons editor))
      f!show)))
