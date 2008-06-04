(mac implement (class . body)
  `(java-implement ,class nil (obj ,@body)))

(defcall java-object (jo method . args) (if (no args) (java-invoke jo method) (java-invoke jo method args)))

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

