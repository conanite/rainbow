(register-test '(suite "miscellaneous tests"

  ("w/bars should ignore nil elements"
    (do (declare 'atstrings t)
        (eval '(tostring (w/bars (pr "baa") nil (pr "floop") nil))))
    "baa | floop"
  )

  (suite "watch out for dangerous optimisations"
     ("special case: swap two values"
       ( (fn (x y)
           (let tmp x
             (assign x y)
             (assign y tmp)
             `(,x ,y))) 1 2)
       (2 1))

     ("special case: inlining literals; scheme and rainbow do the same thing (shouldn't this be a bug?)"
       ((fn (f) (cons (f) (f)) )
         (fn () ((fn (xs) (scdr xs (cons 'x (cdr xs))) xs) '(a b c))))
       ((a x x b c) a x x b c))

     ("special case: don't inline stuff if it ends up out of order"
       (tostring (prn "a" "b" "c"))
       "abc\n")

     ("special case: can't inline pop-me here"
       (let mylist '(a b c d)
         ((fn (pop-me)
              (assign mylist (cdr mylist))
              pop-me)
          (car mylist)))
       a)

     ("special case: confused nesting"
       (let it 'foo
         (with (foobar (string it 'bar)
                foobaz (cons it 'baz))
           (cons foobar foobaz)
         )
       )
       ("foobar" foo . baz)
     )
  ) ; suite watch out

))

