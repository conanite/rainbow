(register-test '(suite "ssexpand"
  ("expand compose"
    (ssexpand 'x:y)
    (compose x y))

  ("expand complement"
    (ssexpand '~p)
    (complement p))

  ("expand compose/complement"
    (ssexpand 'p:~q:r)
    (compose p (complement q) r) )

  ("expand compose/complement"
    (ssexpand '~p:q:r)
    (compose (complement p) q r) )

  ("expand list"
    (ssexpand '+.a.b)
    ((+ a) b))

  ("expand quoted list"
    (ssexpand 'cons!a!b)
    ((cons (quote a)) (quote b)) )

  ("expand chained dots and bangs"
    (ssexpand 'a.b!c.d)
    (((a b) (quote c)) d))

  ("ssexpand with initial dot"
    (ssexpand '.a.b.c)
    (((get a) b) c))

  ("ssexpand with initial quote"
    (ssexpand '!a.b.c)
    (((get (quote a)) b) c))))

