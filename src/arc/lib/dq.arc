
(def divide-and-conquer (divisible? divide conquer recombine)
  (afn (value)
    (if (divisible? value)
        (recombine (map self (divide value)))
        (conquer value))))

(def lastcdr (xs)
  (aif (cdr xs) (lastcdr it) xs))

(def quicksort (op)
  (divide-and-conquer
    (fn (xs) (and (no:atom xs) (> (len xs) 1)))
    (fn (xs) xs
        (if (atom xs) xs
            ((afn (xx k (o low nil) (o high nil))
              (if xx
                  (let elm (car xx)
                    (if (op k elm)
                        (self (cdr xx) k low (cons elm high))
                        (self (cdr xx) k (cons elm low) high)))
                  (list low k high))) (cdr xs) (car xs))))
    (fn (xs) xs)
    (fn ((left k right))
      (if left
          (do (scdr (lastcdr left)
                    (cons k right))
              left)
          (cons k right)))))

(def make-random-list (size limit)
  (if (> size 0) (cons (rand limit) (make-random-list (- size 1) limit))))

(time ((quicksort <) (make-random-list 10 10)))
(time10 ((quicksort <) (make-random-list 100 10000)))
(time10 (sort < (make-random-list 10000 10000)))

