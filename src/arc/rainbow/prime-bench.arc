(def test-primeness (n return)
  (let sq (sqrt n)
    (loop (= x 2) (< x (+ 1 sq)) (++ x)
      (if (exact (/ n x)) (return nil))))
  t)

(def prime? (n) (ccc (fn (cc) (test-primeness n cc))))

(def primes-under (max)
  (let count 0
    (loop (= pc 2) (< pc max) (++ pc)
      (if (prime? pc) (++ count)))
    count))

(def prime-bench (n) (time (primes-under n)))

