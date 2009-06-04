(register-test '(suite "Java Interface Tests"
  ("Type of java object is java-object"
    (type (java-new "java.util.HashMap"))
    java-object)

  (suite "object invocation"
    ("use getClass to get class"
      (((java-new "java.util.HashMap") 'getClass) 'getName)
      "java.util.HashMap"))

  (suite "static invocation"
    ("get the value of a static variable"
      (java-static-field "java.lang.Integer" 'MAX_VALUE)
      2147483647)

    ("call a static method"
      (java-static-invoke "java.lang.Integer" 'parseInt "3324")
      3324)

    ("call a static method with two parameters"
      (java-static-invoke "java.lang.Integer" 'parseInt "cfd" 16)
      3325))

  (suite "dynamic implementation"
    ("sort using comparator"
      ((fn ((comparator sorted-set))
        (assign comparator (implement "java.util.Comparator" compare (fn (a b) (- (len a) (len b)))))
        (assign sorted-set (java-new "java.util.TreeSet" comparator))
        (sorted-set 'add "nananana")
        (sorted-set 'add "x")
        (sorted-set 'add "ppp")
        (sorted-set 'add "ww")
        (sorted-set 'add "rrrr")
        (sorted-set 'toString)
      ) nil)
      "[x, ww, ppp, rrrr, nananana]"))))

