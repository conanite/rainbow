(mac implement (class . body)
  `(java-implement ,class nil (obj ,@body)))

(defcall java-object (jo method . args) 
  (if (no args) 
      (java-invoke jo method) 
      (java-invoke jo method args)))

(def bean (class . args)
  (configure-bean (java-new class) (pair args)))

(def configure-bean (target args)
  (if args
    (with ((prop val) (car args))
      (apply target prop (if (acons val) val (list val)))
      (configure-bean target (cdr args))))
  target)

(mac atdef (name args . body)
  `(def ,name ,args (atomic ,@body)))

(def arc-path ()
  (tokens (or (*env* "ARC_PATH") ".") #\:))

(def find-in-path (file)
  (catch
    (each p (arc-path)
      (let f (+ p "/" file ".arc")
        (if (file-exists f) (throw f))))
    nil))

(set *required-libs* ())

(def require-lib (arc-lib)
  (if (no (find arc-lib *required-libs*))
    (aif (find-in-path arc-lib)
      (do
        (= *required-libs* (cons arc-lib *required-libs*))
        (load it)))))

(def load-file (fname)
  (w/infile f fname
    (awhen (readc f)
      (tostring 
        (writec it)
        (whiler c (readc f) nil
          (writec c))))))

(def write-file (fname text)
  (w/outfile f fname (w/stdout f (pr text))))

(def eval-these (exprs)
  (if (acons exprs)
      (do (eval (car exprs)) (eval-these (cdr exprs)))))
