(set named-chars  (obj "#\\space" #\space "#\\newline" #\newline "#\\tab" #\tab "#\\return" #\return))
(set syntax-chars (obj #\( 'left-paren #\) 'right-paren #\[ 'left-bracket #\] 'right-bracket #\' 'quote #\` 'quasiquote))
(set ws-char-names (obj space " " newline #\newline return "\\r" tab "\t"))

(set syntax-char-names  (obj 
  left-paren        "(" 
  left-bracket      "[" 
  right-paren       ")" 
  right-bracket     "]" 
  quote             "'" 
  quasiquote        "`" 
  unquote           "," 
  unquote-splicing  ",@"))

(def arc-tokens (reader)
  (let token-list nil
    (read-arc-tokens reader 
                    (fn (tok start finish) 
                        (push tok token-list)))
    (rev token-list)))

(def tokenise (charlist)
  (let tok (coerce (rev charlist) 'string)
    (if (and (is (tok 0) #\#) (is (tok 1) #\\))
      (if (is (len tok) 2)      #\space
          (is (len tok) 3)      (tok 2)
          (aif (named-chars tok) it (err (+ "unknown char: " tok))))
      (is (tok 0) #\;) (annotate 'comment tok)
      tok)))

(def char-terminator (ch)
  (find ch '(#\( #\) #\[ #\] #\# #\, #\` #\newline #\space #\return #\tab #\" #\')))
       
(def read-arc-tokens (reader accfn)
  (with (state        nil
         escaping     nil 
         start        0 
         char-count   0 
         token        nil)
    (with (notify           (fn (delim)
                                (if token (accfn (tokenise token) start char-count))
                                (wipe token)
                                (if delim (accfn delim char-count (+ char-count 1))))
           append-to-token  (fn (ch)
                                (if (no token) (= start char-count))
                                (push ch token)))
      ((afn (ch)
        (if (is state 'reading-comment)
                                (if (is ch #\newline)   (wipe state)
                                    ch                  (do (append-to-token ch) (= ch 'ignore))))
        (if (is state 'reading-unquote)
                                (do (wipe state)
                                    (if (is ch #\@)
                                        (do (notify 'unquote-splicing) 
                                            (= ch 'ignore))
                                        (notify 'unquote))))
        (if (is state 'reading-char)
                                (do (if ch 
                                        (append-to-token ch) 
                                        (wipe state))
                                    (if (and (> (len token) 3) (char-terminator ch))
                                        (do (wipe state) (pop token))
                                        (= ch 'ignore))))
        (if (is state 'reading-string)
                                (do (if ch (append-to-token ch))
                                    (if escaping
                                        (wipe escaping)
                                        (if (is ch #\") (wipe state)
                                            (is ch #\\) (do (assert escaping) ))))
            (and (no token) (is ch #\#))
                                (do (append-to-token ch) 
                                    (= state 'reading-char))
            (and (no token) (is ch #\;))
                                (do (append-to-token ch) 
                                    (= state 'reading-comment))
            (syntax-chars ch)   (notify (syntax-chars ch))
            (is ch #\,)         (do (notify nil) 
                                    (= state 'reading-unquote))
            (is ch #\newline)   (notify 'newline)
            (is ch #\space)     (notify 'space)
            (is ch #\")         (do (notify nil) 
                                    (= state 'reading-string) 
                                    (append-to-token ch))
            (no ch)             (notify nil)
            (no:is ch 'ignore)  (append-to-token ch))
        (if ch                  (do (++ char-count) 
                                    (self (readc reader))))
      ) (readc reader)))))

(def parse (text)
  (parse-toks (arc-tokens (instring text))))

(def parse-toks (toks)
  (read-form (fn () (let tok (car toks) (= toks (cdr toks)) tok))))

(def next-form (tok token-generator)
  (if 
    (is tok 'left-paren)          (read-list token-generator 'right-paren)
    (is tok 'left-bracket)        (list 'fn '(_) (read-list token-generator 'right-bracket))
    (or (is tok 'quasiquote) (is tok 'quote) (is tok 'unquote) (is tok 'unquote-splicing))
                                  (list tok (read-form token-generator))
    (ws-char-names tok)           (read-form token-generator)
                                  (read-atom tok)))

(def read-form (token-generator)
  (next-form (token-generator) token-generator))

(def read-string-tok (tok-chars)
  (string:rev (accum s
    ((afn (chs escaping)
      (let ch (car chs)
        (if escaping 
            (do
              (case ch
                #\\ (s ch)
                #\" (s ch)
                #\n (s #\newline)
                #\r (s #\return)
                #\t (s #\tab))
              (self (cdr chs) nil))
            (case ch
              #\\ (self (cdr chs) t)
              #\" nil
                  (do (s ch) 
                      (self (cdr chs) nil)))))
    ) (cdr tok-chars) nil))))

(def read-atom (tok)
  (if (is (type tok) 'char)     tok
      (is (type tok) 'comment)  tok
      (and (is #\" (tok 0)) (is #\" (tok (- (len tok) 1))))
                                (read-string-tok (coerce tok 'cons))
                                (on-err (fn (ex) (coerce tok 'sym))
                                        (fn ()   (coerce tok 'int)))))

(def read-list (token-generator terminator)
  (let toklist nil
    ((afn (tok)
      (if (no:is tok terminator)
        (do 
          (push (next-form tok token-generator) toklist) 
          (self (token-generator)))
      )) (token-generator))
    (rev toklist)))

(def index-source (text)
  (with (result nil parens nil brackets nil) 
    (read-arc-tokens (instring text) 
                     (fn args
                        (push args result)
                        (if (is (car args) 'left-paren)   (push args parens))
                        (if (is (car args) 'left-bracket) (push args brackets))
                        (if (ws-char-names (car args))    (pop result))
                        (if (is (car args) 'right-bracket) 
                            (let original (pop brackets)
                              (= (args 1) (original 1))
                              (= (original 2) (args 2))))
                        (if (is (car args) 'right-paren) 
                            (let original (pop parens)
                              (= (args 1) (original 1))
                              (= (original 2) (args 2))))))
    (rev result)))

(set parser-test-suite '(suite "parser tests"
  ("parse a single symbol"              (parse "foo")                   foo                 )
  ("parse a single symbol: is sym"      (type:parse "foo")              sym                 )
  ("parse a single string"              (parse "\"foo\"")               "foo"               )
  ("parse a single string: is string"   (type:parse "\"foo\"")          string              )
  ("parse a quoted symbol"              (parse "'foo")                  'foo                )
  ("parse an empty list"                (parse "()")                    ()                  )
  ("parse an empty list: is sym"        (type:parse "()")               sym                 )
  ("parse a character"                  (parse "#\\a")                  #\a                 )
  ("parse a newline character"          (parse "#\\
")                                                                      #\newline           )
  ("parse a character: is char"         (type:parse "#\\a")             char                )
  ("parse a number"                     (parse "99/101")                99/101              )
  ("parse a number: is num"             (type:parse "99/101")           num                 )
  ("parse numbers in a list"            (parse "(12 34.56 -17 3/4)")    (12 34.56 -17 3/4)  )
  ("parse a list of characters"         
    (eval (parse "(coerce '(#\\( #\\a #\\b #\\space #\\c #\\  #\\d #\\)) 'string)"))
    "(ab c d)")
  ("raise error for unrecognised chars"
    (on-err (fn (ex) (details ex)) (fn () (parse "#\\spade")))               
    "unknown char: #\\spade")
  ("parse a string containing spaces"
    (parse "\"foo bar\"")               
    "foo bar")
  ("parse a nasty string containing parens and escapes"
    (parse "(parse \"\\\"foo bar\\\"\")")
    (parse "\"foo bar\""))
  ("parse bracket syntax for functions"
    (apply (eval (parse "[* _ _]")) '(27))
    729)
  ("parse a complex expression"    
    (parse "(foo bar '(toto) `(do ,blah ,@blahs \"astring\") titi)")
    (foo bar '(toto) `(do ,blah ,@blahs "astring") titi))))


(def run-parser-tests ()
  (set show-failed-only t)
  (run-tests parser-test-suite))

(run-parser-tests)

(set indexer-test-text "
(def foo (bar)
  (toto 'a \"blah\"))")

(set indexer-test-expected 
  (list
    '(left-paren   1 35  )
    '("def"        2  5  )
    '("foo"        6  9  )
    '(left-paren   10 15 )
    '("bar"        11 14 )
    '(left-paren   18 34 )
    '("toto"       19 23 )
    '(quote        24 25 )
    '("a"          25 26 )
    '("\"blah\""   27 33 )))

(set indexer-test-suite (list "indexer" `(index-source ,indexer-test-text) indexer-test-expected))

(run-tests indexer-test-suite)
