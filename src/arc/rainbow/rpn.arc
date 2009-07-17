
(def rpn-interpret (stack toks) 
  (if toks
     (let tok (car toks)
       (rpn-interpret (if (is tok "+") (cons (+ (car stack) (cadr stack)) 
                                             (cddr stack)) 
                          (is tok "-") (cons (- (cadr stack) (car stack)) 
                                             (cddr stack))
                                       (cons (coerce tok 'int) stack))
                      (cdr toks)))
        stack))

(def rpn-eval (text) (car:rpn-interpret nil (tokens text)))



(def distance (coordinates)
  (sqrt:reduce + (map [* _ _] coordinates)))
