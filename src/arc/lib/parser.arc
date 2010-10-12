; This software is copyright (c) Conan Dalton 2008. Permission to use
; it is granted under the Perl Foundations's Artistic License 2.0.

(assign syntax-chars (obj
  #\( 'left-paren
  #\) 'right-paren
  #\[ 'left-bracket
  #\] 'right-bracket
  #\' 'quote
  #\` 'quasiquote))

(assign character-constants  (obj
  "#\\space"   #\space
  "#\\newline" #\newline
  "#\\tab"     #\tab
  "#\\return"  #\return))

(def whitespace? (ch)
  (any? ch #\space #\newline #\tab #\return))

(def slow-whitespace? (ch)
  (if (in ch #\space #\newline #\tab #\return) ch))

(def make-character (token)
  (if (is (len token) 3)
      (token 2)
      character-constants.token
      character-constants.token
      (on-err (fn (ex) (err (string "unknown char: " token)))
              (fn ()   (coerce (coerce (cut token 2) 'int 8) 'char)))))

(def tokenise-syntax (token start)
  (list 'syntax token start (+ start 1)))

(def tokenise-char (token start)
  (list 'char
        (make-character token)
        start
        (+ start len.token)))

(def read-symbol (txt index)
  (let a (coerce txt 'cons)
    (if (is car.a #\|)
        (let b (rev:cdr a)
          (if (is car.b #\|)
              (sym (coerce (rev:cdr b) 'string))
              (err (string "unrecognised token: " txt " at " index))))
        (sym txt))))

(def tokenise-other (token start kind)
  (let tokend (+ start len.token)
    (if kind
        (list kind token start tokend)
        (let c0 (token 0)
          (if whitespace?.c0   (list 'whitespace token start tokend)
              (is c0 #\#)      (list 'char (make-character token) start tokend) ; todo this is necessary for char token at end of stream (in-character state needs a next character to flush its token)
              (is c0 #\;)      (list 'comment token start tokend)
              (any? c0 #\. #\+ #\- #\0 #\1 #\2 #\3 #\4 #\5 #\6 #\7 #\8 #\9)
              (on-err (fn (ex) (list 'sym (sym token) start tokend))
                      (fn () (list 'int (coerce token 'num) start tokend)))
              (list 'sym (read-symbol token start) start tokend))))))

(def char-terminator (ch)
  (or syntax-chars.ch whitespace?.ch (any? ch #\" #\, #\#)))

(def arc-tokeniser (char-stream)
  (with ((default in-string interpolating escaping in-character in-comment in-atom in-unquote) nil
         (token state tq1 tq2 push-tok) nil
         (nextc enq-token1 enq-char enq/switch0 enq/switch tokenator) nil
         (enq-token enq-no-token enq-a-token) nil
         (add-to-token default-add-to-token initial-add-to-token) nil
         lines       1
         char-count  0
         token-start 0)

    (= push-tok     (fn (tok)
                        (if tq1 (assign tq2 tok)
                                (assign tq1 tok))
                        (assign add-to-token initial-add-to-token
                                enq-token    enq-no-token
                                token        nil))
       nextc        (fn ()
                        (assign char-count (+ char-count 1))
                        (readc char-stream))
       enq-no-token (fn args nil)
       enq-a-token  (fn ((o token-kind nil))
                        (push-tok:tokenise-other token (- token-start 1) token-kind))
       enq-token1   (fn (another (o token-kind nil))
                        (enq-token token-kind)
                        (push-tok (tokenise-syntax another (- char-count 1))))
       enq-char     (fn (ch)
                        (push-tok (tokenise-char token (- token-start 1)))
                        ((assign state default) ch))
       enq/switch0  (fn (new-state)
                        (enq-token)
                        (assign state new-state))
       enq/switch   (fn (another-tok new-state)
                        (enq-token1 another-tok)
                        (assign state new-state))
       tokenator    (afn ()
                        (if tq1
                            (let result tq1
                              (assign tq1 tq2)
                              (assign tq2 nil)
                              result)
                            (aif (nextc)
                                 (do (if (is it #\newline)
                                         (assign lines (+ 1 lines)))
                                     state.it
                                     (self))
                                 token
                                 (do (enq-token) (self)))))
       default-add-to-token (fn (ch)
                                (assign token (+ token (coerce ch 'string)))) ; for some unknown reason, (+ token ch) is slower
       initial-add-to-token (fn (ch)
                                (assign token-start  char-count
                                        token        (coerce ch 'string)
                                        enq-token    enq-a-token
                                        add-to-token default-add-to-token))

       default      (fn (ch)
           (if whitespace?.ch  (add-to-token ch)
               syntax-chars.ch (enq-token1 syntax-chars.ch)
               (is ch #\")     (enq/switch 'left-string-delimiter in-string)
               (is ch #\#)     ((enq/switch0 in-character) ch)
               (is ch #\,)     (enq/switch0 in-unquote)
               (is ch #\;)     ((enq/switch0 in-comment) ch)
                               ((enq/switch0 in-atom) ch)))
       in-string    (fn (ch)
           (if (is ch #\\)     (do (add-to-token ch)
                                   (assign state escaping))
               (is ch #\#)     (assign state interpolating)
               (is ch #\")     (do (enq-token1 'right-string-delimiter 'string-fragment)
                                   (assign state default))
                               (add-to-token ch)))
       interpolating (fn (ch)
           (if (is ch #\()     (do (enq-token1 'interpolation 'string-fragment)
                                   (assign state default))
                               (do (add-to-token #\#)
                                   (if (is len.token 1) (-- token-start))
                                   ((assign state in-string) ch))))
       escaping     (fn (ch)
           (add-to-token ch)
           (assign state in-string))
       in-character (fn (ch)
           (if (and (> (len token) 2) (char-terminator ch))
               (enq-char ch)
               (add-to-token ch)))
       in-comment   (fn (ch)
           (if (is ch #\newline) ((enq/switch0 default) ch)
                                 (add-to-token ch)))
       in-atom      (fn (ch)
           (if (or whitespace?.ch
                   syntax-chars.ch) ((enq/switch0 default) ch)
                                    (add-to-token ch)))
       in-unquote   (fn (ch)
           (if (is ch #\@)     (enq/switch 'unquote-splicing default)
                               ((enq/switch 'unquote default) ch)))

       state        default
       enq-token    enq-no-token
       add-to-token initial-add-to-token)

     (list
       tokenator
       (fn () lines)
       (fn () (assign state in-string))
       (fn () (- char-count
                 (if tq1 (- tq1.3 tq1.2) 0)
                 (if tq2 (- tq2.3 tq2.2) 0)
                 (len token)))
)))

(def read-tokens (text)
  (let (tt lns instr) (arc-tokeniser (instring text))
    (while (prn (tt)))))

(def parse-with-info (text)
  (let (tkz lc in-string consumed)
       (arc-tokeniser (if (isa text 'string) (instring text) text))
    (let result (parse-tokens (list tkz lc in-string))
      (list result (consumed)))))

(def parse (text)
  (parse-tokens (arc-tokeniser (if (isa text 'string) (instring text) text))))

(def parse-tokens ((tkz lc in-string))
  "parses the given text (input or string)
   and returns the corresponding arc object"
  (let (unescape-fragment assemble-string next-form read-string read-form read-list ignore) nil
    (= ignore (uniq))

    (def unescape-fragment (fragment)
      (string:accum s
        (afnwith (chs      (coerce fragment 'cons)
                  escaping nil)
          (iflet ch (car chs)
            (if escaping
                (do
                  (case ch
                    #\# (s ch)
                    #\\ (s ch)
                    #\" (s ch)
                    #\n (s #\newline)
                    #\r (s #\return)
                    #\t (s #\tab))
                  (self (cdr chs) nil))
                (case ch
                  #\\ (self (cdr chs) t)
                      (do (s ch)
                          (self (cdr chs) nil))))))))

    (def assemble-string (fragments)
      (if no.fragments
          ""
          (is len.fragments 1)
          car.fragments
          `(string ,@rev.fragments)))

    (def next-form (token token-generator)
      (if (token? token 'syntax 'left-paren)
          (read-list token-generator 'right-paren)
          (token? token 'syntax 'left-bracket)
          `(fn (_) ,(read-list token-generator 'right-bracket))
          (token? token 'syntax 'left-string-delimiter)
          (read-string token-generator nil)
          (and (token? token 'syntax)
	             (in cadr.token 'quasiquote 'quote 'unquote 'unquote-splicing))
          `(,cadr.token ,(read-form token-generator))
          (or (token? token 'whitespace)
              (token? token 'comment))
          ignore
          cadr.token))

    (def read-string (token-generator fragments)
      (let token (token-generator)
        (if (token? token 'syntax 'right-string-delimiter)
            (assemble-string fragments)
            (token? token 'syntax 'interpolation)
            (do (push (read-form token-generator) fragments)
                (let token2 (token-generator)
                  (if (token? token2 'syntax 'right-paren)
                      (in-string)
                      (err "unclosed string interpolation: " car.fragments)))
                (read-string token-generator fragments))
            (token? token 'string-fragment)
            (do (push (unescape-fragment (cadr token)) fragments)
                (read-string token-generator fragments))
            token
            (err (string "unexpected token in string: " token ": fragments are " fragments)))))

    (def read-form (token-generator)
      (let nextform (next-form (token-generator) token-generator)
        (if (is nextform ignore)
            (read-form token-generator)
            nextform)))

    (def read-list (token-generator terminator)
      (let token (token-generator)
        (if token
            (if (and (is car.token 'sym) (is (coerce token.1 'string) "."))
                (car:read-list token-generator terminator) ; todo: need to explicitly forbid (a b . c . d)
                (~token? token 'syntax terminator)
                (let nextform (next-form token token-generator)
                  (if (is nextform ignore)
                      (read-list token-generator terminator)
                      (cons nextform (read-list token-generator terminator))))))))

    (read-form tkz)))

(def token? ((kind tok s e) expected-kind (o expected-tok))
  (and (is kind expected-kind)
       (or (no expected-tok)
           (is tok expected-tok))))

(assign syntax-pairs (obj
  left-paren            'right-paren
  interpolation         'right-paren
  left-bracket          'right-bracket
  left-string-delimiter 'right-string-delimiter))

(= (syntax-pairs (string #\# #\()) 'right-paren)

(def unmatchify (token-name)
  (sym (string "unmatched-" token-name)))

(def link-parens (right left)
  (if left
      (do (if (no:is (right 1) (syntax-pairs (left 1)))
              (do (scar cdr.left  (unmatchify:cadr left))
                  (scar cdr.right (unmatchify:cadr right))))
          (sref right (left 2) 2)
          (sref left (right 3) 3))
      (scar cdr.right (unmatchify:cadr right))))

(def index-source (text (o keep-whitespace))
  (if (is (type text) 'string)
      (= text (instring text)))

  (with ((result parens brackets quotes) nil
         (tkz linecount in-string) (arc-tokeniser text))
    (whilet token (tkz)
      (fpush token result)
      (let (kind tok start length) token
        (if (is kind 'syntax)
            (if (is tok 'left-paren)             (fpush token parens)
                (is tok 'left-bracket)           (fpush token brackets)
                (is tok 'interpolation)          (fpush token parens)
                (is tok 'left-string-delimiter)  (fpush token quotes)
                (is tok 'right-bracket)          (link-parens token (fpop brackets))
                (is tok 'right-string-delimiter) (link-parens token (fpop quotes))
                (is tok 'right-paren)            (let left-tok (fpop parens)
                                                    (if (token? left-tok 'syntax 'interpolation)
                                                        (in-string))
                                                    (link-parens token left-tok)))
            (and no.keep-whitespace (is kind 'whitespace))
                                                 (fpop result))))
    (each unmatched (list quotes parens brackets)
      (each p unmatched (scar cdr.p (unmatchify:cadr p))))
    (list (rev result) (linecount))))

(def si-repl ()
  (prn "Type x! to return to the usual repl.
Interpolations look like this: \"string content " #\# "( interpolated-value ), and also " #\# "( (interpolated function call) ).
Enjoy interpolating.")
  ((afn ()
        (pr "arc$ ")
        (on-err (fn (ex)
                    (prn "Error: " (details ex))
                    (self))
                (fn ()
                    (let expr (parse (stdin))
                      (if (no:is expr 'x!)
                          (do (write (eval expr))
                              (prn)
                              (self)))))))))

