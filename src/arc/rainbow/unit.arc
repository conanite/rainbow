(set show-failed-only t)

(set all-tests nil)

(def register-test (test)
  (push test all-tests))
  
(def run-all-tests ()
  (let results (obj passed 0 failed 0)
    (run-tests `(suite "all tests" ,@all-tests) results)
    (prn "passed: " results!passed)
    (prn "failed: " results!failed)
    (/ results!passed (+ results!passed results!failed))))

(def run-tests (tests (o results (obj passed 0 failed 0)))
  (execute-test "" tests results)
  results)

(def execute-test (desc test results)
  (if (is 'suite (car test))
      (execute-tests (+ desc " - " (cadr test)) (cddr test) results)
      (execute-single-test desc test results)))

(def execute-single-test (desc test results)
	(with (expected (test 2) result (eval (cadr test)))
	      (if (iso result expected)
	          (do (if (is show-failed-only nil) (prn desc " - " (car test) " - ok")) (++ results!passed))
	          (do (prn desc " - " (car test) " - FAILED: expected " expected ", got " result) (++ results!failed)))))

(def execute-tests (desc tests results)
  (execute-test desc (car tests) results)
  (if (cdr tests) (execute-tests desc (cdr tests) results)))

