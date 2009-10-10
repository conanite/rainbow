; we don't have to worry about scheme's nil-terminators
(def list args args)

; java's toString() on a tagged object delegates to
; the corresponding function in this table if present
(assign tagged-writers (table))

; borrowed from anarki
(mac defcall (name parms . body)
  `(sref call* (fn ,parms ,@body) ',name))

(mac java-import (class (o simple-name (last:tokens string.class #\.)))
  (= class string.class)
  `(mac ,(sym simple-name) (method . args)
     (if (is method 'new)       `(java-new ,,class ,@args)
         (is method 'implement) `(java-implement ,,class ,@args)
         (is method 'class)     `(java-static-invoke "java.lang.Class" 'forName ,,class)
         (is method '*reflect)  `(each m ((,',(sym simple-name) class) 'getMethods) (prn m))
         (is type.method 'sym)  `(java-static-invoke ,,class ',method ,@args)
                                `(java-static-invoke ,,class ,method ,@args))))

(mac java-imports (pkg . classes)
  `(do ,@(map (fn (_) `(java-import ,(string pkg "." _))) classes)))

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

(java-import java.io.File)

(def canonical-path (file)
  ((File new file) 'getCanonicalPath))

(mac profiler expr
  `(after (do (rainbow-profile) ,@expr)
          (show-profile-report (rainbow-profile-report))))

(def show-profile-report (report)
  (prn "Invocation profiles")
  (prn "=================")
  (let r (text-column-writer 14 14 14 200)
    (r "total-time" "own-time" "invocations" "fn")
    (each item report!invocation-profile
      (profile-report-fn r "" item))))

(def show-instruction-profile (report)
  (prn "Rainbow vm-instruction counts")
  (prn "=============================")
  (let r (text-column-writer 10 200)
    (r "count" "instruction class")
    (each (value . name) report!instruction-profile
      (r value name))))

(def profile-report-fn (r indent (all-nanos my-nanos count object kidz))
  (r (string all-nanos 'ms) (string my-nanos 'ms) count (tostring:pr indent object))
  (each item (sort car> kidz)
    (profile-report-fn r (+ indent "  ") item)))

(def at-toks (str)
  (with (s nil f nil)
    (= s (fn (chs acc (o tok))
           (if (no chs)
               (if tok
                   (cons (string:rev tok) acc)
                   acc)
               (is car.chs #\@)
               (if (is cadr.chs #\@)
                   (s cddr.chs acc (cons #\@ tok))
                   (f cdr.chs (cons (string:rev tok) acc)))
               (s cdr.chs acc (cons car.chs tok))))
       f (fn (chs acc)
           (if (no chs)
               acc
               (withs (str (coerce chs 'string)
                       ins  (instring str)
                       (form consumed) (parse-with-info ins)
                       rest (cut str consumed))
                 (s (coerce rest 'cons) (cons form acc))))))
    (rev (s (coerce str 'cons) nil))))

(def at-string (str)
  (let toks (at-toks str)
    (if no.toks
        ""
        (no:cdr toks)
        car.toks
        `(string ,@toks))))




