(register-test '(suite "rainbow ssexpand"
  ("andf"
    (ssexpand '&a&b&)
    (andf &a b&))

  ("andf - ignore &"
    (ssyntax '&)
    nil)

  ("andf - ignore &&"
    (ssyntax '&&)
    nil)

))

