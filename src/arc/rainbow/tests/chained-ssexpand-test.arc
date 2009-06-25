(register-test '(suite "rainbow ssexpand"
  ("andf"
    (ssexpand '+a+b+)
    (andf +a b+))
))

