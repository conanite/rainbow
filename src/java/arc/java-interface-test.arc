(defcall java-object (jo method . args) (java-invoke jo method args))

(set show-failed-only t)

(set java-interface-test-suite '(suite "Anarki Compatibility Tests"
  ("Type of java object is java-object"
    (type (java-new "java.util.HashMap"))
    java-object)

  (suite "object invocation"
    ("use getClass to get class"
      (((java-new "java.util.HashMap") 'getClass) 'getName)
      "java.util.HashMap")
  )
  
  (suite "static invocation"
    ("get the value of a static variable"
      (java-static-field (java-class "java.lang.Integer") 'MAX_VALUE)
      2147483647)
      
    ("call a static method"
      (java-static-invoke (java-class "java.lang.Integer") 'parseInt "3324")
      3324)
      
    ("call a static method"
      (java-static-invoke (java-class "java.lang.Integer") 'parseInt "cfd" 16)
      3325)
  )
  
  (suite "dynamic implementation"
    ("sort using comparator"
      ((fn ()
        (set comparator-functions (obj compareTo (fn (a b) (< (len a) (len b)))))
        (set comparator (java-implement "java.util.Comparator" comparator-functions))
        (set sorted-set (java-new "java.util.TreeSet"))
        (sorted-set 'add "nananana")              
        (sorted-set 'add "x")              
        (sorted-set 'add "ppp")              
        (sorted-set 'add "ww")              
        (sorted-set 'add "rrrr")
        (sorted-set 'toString)
      ))
      "[x ww ppp rrrr nananana]")
  )
))

(prn (run-tests java-interface-test-suite))

