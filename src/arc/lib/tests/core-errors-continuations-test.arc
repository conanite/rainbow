(register-test '(suite "Foundation Tests"
  (suite "Errors and Continuations"
    (suite "ccc"
      ("use ccc to return a value"
        (ccc (fn (esc) (esc "bailout value") 42))
        "bailout value")

      ("support continuation-passing style to calculate hypoteneuse"
        ( (fn ((cps* cpsplus cps-sqrt cps-pyth))
          (assign cps* (fn (x y k) (k (* x y))))
          (assign cpsplus (fn (x y k) (k (+ x y))))
          (assign cps-sqrt (fn (x k) (k (sqrt x))))
          (assign cps-pyth (fn (x y k)
            (cps* x x (fn (x2)
              (cps* y y (fn (y2)
                (cpsplus x2 y2 (fn (x2py2)
                  (cps-sqrt x2py2 k)))))))))
          (< 6.40312423743284 (ccc (fn (cc) (cps-pyth 4 5 cc))) 6.40312423743285)) nil)
        t)

      ("support co-routines" ; adapted from http://community.schemewiki.org/?call-with-current-continuation
        ((fn (hefty-info)
          (assign hefty-stuff (fn (other-stuff)
            (assign rec-hefty (fn (n)
              (assign hefty-info (cons "hefty" (cons n hefty-info)))
              (assign other-stuff (ccc other-stuff))
              (if (> n 0) (rec-hefty (- n 1)))))
            (rec-hefty 5)))

          (assign light-stuff (fn (other-stuff)
            (assign rec-light (fn (x)
              (assign hefty-info (cons "light" hefty-info))
              (assign other-stuff (ccc other-stuff))
              (rec-light 0)))))

          (if (is hefty-info nil) (hefty-stuff light-stuff))

          hefty-info
        ) nil)
        ("light" "hefty" 0 "light" "hefty" 1 "light" "hefty" 2 "light" "hefty" 3 "light" "hefty" 4 "light" "hefty" 4 "hefty" 5))
    )

    ("protect"
      ((fn (x)
        (protect (fn () (/ 1 2)) (fn () (assign x "protected-foo")))
        x) nil)
      "protected-foo")

    (suite "Error handling"
      ("no error"
        (on-err (fn (ex) "got error")
                (fn () (* 6 7)))
        42 )

      ("error"
        (on-err (fn (ex) (+ "got error " (details ex)))
                (fn () (/ 42 0)))
        "got error /: division by zero" )

      ("explicit error"
        (on-err (fn (ex) (+ "got error " (details ex)))
                (fn () (err "we can also throw our own exceptions")))
        "got error we can also throw our own exceptions" )
    )
  )))
