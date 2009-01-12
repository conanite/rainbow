(def test-complex (c) (map [trunc _] (complex-parts c)))
(def test-complex-precise (c) (map [trunc (* 10000 _)] (complex-parts c)))

(register-test '(suite "Extra Math tests"
  (suite "predicates on complex numbers"
    ("complex numbers are not ordered"
      (on-err (fn (ex) (details ex))
              (fn () (< 1+2i 3+4i)))
      "Compare: complex numbers are unordered and cannot be compared"))

  (suite "complex numbers"
    ("addition"
      (test-complex (+ 6+7i 1.5+2.5i 3.5+2.5i))
      (11 12))

    ("subtraction"
      (test-complex (- 12+12i 2.5+2.5i 3.5+2.5i))
      (6 7))

    ("multiplication by scalar"
      (test-complex (* 2 3.5+2.5i))
      (7 5))

    ("multiplication by complex"
      (test-complex (* 28+4i 3.5+2.5i))
      (88 84))

    ("multiple multiplication by complex"
      (test-complex (* 2 28+4i 3.5+2.5i))
      (176 168))

    ("division by scalar"
      (test-complex (/ 4+6i 2))
      (2 3))

    ("division by complex"
      (test-complex-precise (/ 2 4+6i))
      (1538 -2308))

    ("divide complex by complex"
      (test-complex-precise (/ 12.4-63.6i 0.1+0.2i))
      (-2296000 -1768000))

    ("divide complex by several complexes"
      (test-complex-precise (/ 12.4-63.6i 0.1+0.2i -0.5-0.4i))
      (4524878 -83903))

    ("exponent of complex number"
      (test-complex (expt 1+2i 2))
      (-3 4) )

    ("double exponent of complex number"
      (test-complex (expt -3+4i 0.5))
      (1 2) )

    ("i^i"
      (test-complex-precise (expt 0+i 0+i))
      (2078 0))

    ("complex exponent, complex base"
      (test-complex-precise (expt 2+3i 1+2i))
      (-4640 -1996))

    ("real base, complex exponent"
      (test-complex-precise (expt 5 2+3i))
      (28916 -248323))

    ("complex logarithm"
      (test-complex-precise (log 1+2i))
      (8047 11071))
  )

  (suite "sine"
    ("sin of pi/6 is 0.5"      (<  0.4999 (sin (/ ¹ 6)) 0.5001)   t)
    ("sin of pi is 0"          (< -0.0001 (sin ¹)       0.0001)   t)
    ("sin of 0 is 0"           (< -0.0001 (sin 0)        0.0001)   t)
    ("sin of pi/2 is 1"        (sin (/ ¹ 2)) 1.0))

  (suite "cosine"
    ("cos of 0 is 1"           (cos 0) 1.0)
    ("cos of pi is 1"          (cos ¹) -1.0)
    ("cos of pi/3 is 0.5"      (< 0.4999  (cos (/ ¹ 3)) 0.5001)   t)
    ("cos of pi/2 is 0"        (< -0.0001 (cos (/ ¹ 2)) 0.0001)   t))

  (suite "tangent"
    ("tan of pi/4 is 1"        (< 0.9999  (tan (/ ¹ 4)) 1.0001)   t)
    ("tan of 0 is 0"           (tan 0) 0.0)
    ("tan of pi is 0"          (< -0.0001 (tan ¹)       0.0001)   t))))
