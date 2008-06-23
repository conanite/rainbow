(register-test '(suite "parser tests"
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

(register-test '(suite "source code indexer test"
  ("index a proper expression"
    (index-source "
(def foo (bar)
  (toto 'a \"blah\"))") 
      ((left-paren   1 35  )
       ("def"        2  5  )
       ("foo"        6  9  )
       (left-paren   10 15 )
       ("bar"        11 14 )
       (right-paren  10 15 )
       (left-paren   18 34 )
       ("toto"       19 23 )
       (quote        24 25 )
       ("a"          25 26 )
       ("\"blah\""   27 33 )
       (right-paren  18 34 )
       (right-paren   1 35 )))))
