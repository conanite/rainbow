
(def gen-primes (upto)
  (with (primes (queue)
         n      3)
    (enq 2 primes)
    (while (< n upto)
      (with (facs qlist.primes sqr (sqrt n))
        (while (and facs (< car.facs sqr) (~is (mod n car.facs) 0))
               (assign facs cdr.facs))
        (when (or no.facs (> car.facs sqr)) (enq n primes)))
      (++ n 2))
    qlist.primes))

