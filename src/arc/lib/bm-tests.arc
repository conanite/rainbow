
(require-lib 'lib/primes)

(defbm string-tokeniser
  (s (rand-string 10000))
  (repeat 10 (tokens s #\0)))

(defbm sort-random-numbers
  (ns (n-of 10000 (rand 10000)))
  (sort < ns))

(defbm find-top-numbers
  (ns (n-of 1000 (rand 50)))
  (repeat 10 (bestn 100 > ns)))

(defbm generate-primes
  ()
  (gen-primes 10000))

(defbm tokenise-index-arc-code
  (a (load-file "arc.arc"))
  (index-source a))
