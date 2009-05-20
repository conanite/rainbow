(mac dbg-var (var)
  (w/uniq gvar
    `(let ,gvar ,var
       (ero ',var 'is ,gvar)
       ,gvar)))

(mac java-import (class (o simple-name (last:tokens class #\.)))
  `(mac ,(sym simple-name) (method . args)
     (if (is method 'new)       `(java-new ,,class ,@args)
         (is method 'implement) `(java-implement ,,class ,@args)
                                `(java-static-invoke ,,class ',method ,@args))))

(mac alet (val . body)
  `(let it ,val
       ,@body
       it))

(mac nobj args ; with thanks to http://arclanguage.org/item?id=7478 and http://arclanguage.org/item?id=7480
  `(obj ,@(mappend [list _ _] args)))

(with ((make-def double-each) nil)
  (= make-def (fn ((name args . body))
     `(= ,name (fn ,args ,@body))))
  (mac make-obj args
    `(with (,(map car args) nil)
       ,@(map make-def args)
       (nobj ,@(map car args)))))

(def index-of (x xs)
  (catch
    ((afn (count xs1)
      (if (no xs1)      (throw -1)
          (caris xs1 x) (throw count)
                        (self (+ 1 count) (cdr xs1)))) 0 xs)))

(def sym+ args
  (sym (apply + (map [string _] args))))

(def upcase-initial (x)
  (case (type x)
    string (string `(,(upcase (x 0)) ,@(cdr (coerce x 'cons))))
    sym    (sym:upcase-initial:string x)))

(def java-accessor (dir name)
  (let prop-chars (coerce (string name) 'cons)
    (sym+ dir (upcase-initial name))))

(defmemo java-getter (prop) (java-accessor 'get prop))
(defmemo java-setter (prop) (java-accessor 'set prop))

(mac implement (class . body)
  `(java-implement ,class nil (obj ,@body)))

(defcall java-object (jo method . args)
  (if (no args)
      (java-invoke jo method)
      (java-invoke jo method args)))

(def bean (class . args)
  (apply configure-bean (java-new class) args))

(def configure-bean (target . args)
  ((afn (props)
    (if props
      (with ((prop val) (car props))
        (apply target (java-setter prop) (if (acons val) val (list val)))
        (self (cdr props))))) (pair args))
  target)

(def j-enumeration (mapper xs)
  (implement "java.util.Enumeration"
    hasMoreElements (fn () xs)
    nextElement     (fn () (mapper (pop xs)))))

(def to-iterator (xs (o mapper idfn))
  (implement "java.util.Iterator"
    hasNext (fn () xs)
    next    (fn () (mapper (pop xs)))))

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
  (w/infile f (if (is (type fname) 'string) fname (coerce fname 'string))
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
