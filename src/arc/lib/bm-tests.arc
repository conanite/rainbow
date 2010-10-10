(require-lib 'lib/primes)
(require-lib 'lib/nbody)

(def bm-cps-pyth ()
  (with (cps* (fn (x y k) (k (* x y)))
         cpsplus (fn (x y k) (k (+ x y)))
         cps-sqrt (fn (x k) (k (sqrt x))))
    (let cps-pyth (fn (x y k)
            (cps* x x (fn (x2)
              (cps* y y (fn (y2)
                (cpsplus x2 y2 (fn (x2py2)
                  (cps-sqrt x2py2 k))))))))
      (for i 1 250
        (for j 1 250
          (ccc (fn (cc) (cps-pyth i j cc))))))))

(def bm-straight-pyth ()
  (for i 1 400
    (for j 1 400
      (sqrt (+ (* i i) (* j j))))))

(defbm arc-code-indexer
  (a (load-file "arc.arc"))
  (index-source a))

(defbm read-arc-dot-arc-content
  ()
  (load-file "arc.arc"))

(defbm string-tokeniser
  (s (rand-string 10000))
  (repeat 10 (tokens s #\0)))

(defbm sort-random-numbers
  (ns (n-of 10000 (rand 10000)))
  (sort < ns))

(defbm find-top-numbers
  (ns (n-of 1000 (rand 50)))
  (repeat 15 (bestn 100 > ns)))

(defbm generate-primes
  ()
  (gen-primes 22500))

(defbm nbody-600
  ()
  (nbody 600))

; impossibly slow with unoptimised ccc
;(defbm using-ccc
;  ()
;  (bm-cps-pyth))

(defbm sqrt
  ()
  (bm-straight-pyth))

(defbm catch/throw
  (s (coerce "aab" 'cons))
  (repeat 35000
    (catch
      (each ch s (if (is ch #\b) (throw ch))))))
