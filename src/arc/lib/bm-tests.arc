
(defbm string-tokeniser
  (s (rand-string 10000))
  (repeat 10 (tokens s #\0)))

(defbm arc-code-tokeniser
  (a (load-file "arc.arc"))
  (index-source a))

(defbm sort-random-numbers
  (ns (n-of 10000 (rand 10000)))
  (sort < ns))

(defbm find-top-numbers
  (ns (n-of 1000 (rand 50)))
  (repeat 10 (bestn 100 > ns)))

(require-lib 'lib/primes)

(defbm generate-primes
  ()
  (gen-primes 10000))
