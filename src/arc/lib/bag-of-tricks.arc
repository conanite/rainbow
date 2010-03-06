; more efficient than (is x nil)
(def no (x) (if x nil t))

; same as map1 but avoids unnecessary call to no
(def map1 (f xs)
  (if xs
      (cons (f (car xs)) (map1 f (cdr xs)))))

; like map1, but caters for improper lists
(def map2 (f xs)
  (if (no xs)   nil
      (atom xs) (f xs)
                (cons (f:car xs) (map2 f cdr.xs))))

; like map but doesn't cons.
; I would like to call this 'each, but that name is already taken.
(def iter (f xs)
  (when xs
    (f car.xs)
    (iter f cdr.xs)))

; same as pr but uses iter instead of map1. Fewer conses, but not a major perf win
(def pr args
  (iter disp args)
  car.args)

(mac afnwith (withses . body)
  (let w (pair withses)
    `((afn ,(map car w) ,@body) ,@(map cadr w))))

(def nilfn args nil)

;; fast (non-atomic) and naive push and pop 

(mac fpush (x xs)
  `(assign ,xs (cons ,x ,xs)))

(mac fpop (xs)
  (w/uniq gp
  `(let ,gp (car ,xs)
     (assign ,xs (cdr ,xs))
     ,gp)))

;; alternative to 'in that's faster and returns its arg if found (don't use to find nil!)
(mac any? (x . choices)
  (w/uniq g
    `(let ,g ,x
       (if ,@(mappend (fn (c) `((is ,g ,c) ,g)) choices)))))

(mac sortfn (name comparer transform)
 `(def ,name args
    (apply ,comparer (map ,transform args))))

;; useful arguments to sort: (sort car< list-of-lists)
(sortfn car< < car)
(sortfn car> > car)
(sortfn cadr> > cadr)

(mac dbg (var)
  (w/uniq gvar
    `(let ,gvar ,var
       (ero ',var 'is ,gvar)
       ,gvar)))

(mac alet (val . body)
  `(let it ,val
       ,@body
       it))

(mac nobj args ; with thanks to http://arclanguage.org/item?id=7478 and http://arclanguage.org/item?id=7480
  `(obj ,@(mappend [list _ _] args)))

(def prerr args (w/stdout (stderr) (apply pr args)))

(let make-def (fn ((name args . body))
                  `(= ,name (fn ,args ,@body)))
  (mac make-obj args
    `(with (,(map car args) nil)
       ,@(map make-def args)
       (nobj ,@(map car args)))))

(def index-of (x xs)
  (catch
    (afnwith (count 0 xs1 xs)
      (if (no xs1)      (throw -1)
          (caris xs1 x) (throw count)
                        (self (+ 1 count) (cdr xs1))))))

(def mksym args
  (sym (apply string args)))

(def sym+ args
  (sym (apply + (map [string _] args))))

(def upcase-initial (x)
  (case (type x)
    string (string `(,(upcase (x 0)) ,@(cdr (coerce x 'cons))))
    sym    (sym:upcase-initial:string x)))

(mac atdef (name args . body)
  `(def ,name ,args (atomic ,@body)))

(def readstring (f)
  (awhen (readc f)
    (tostring
      (writec it)
      (whiler c (readc f) nil
        (writec c)))))

(def write-file (fname text)
  (w/outfile f fname (w/stdout f (pr text))))

(def eval-these (exprs)
  (if (acons exprs)
      (do (eval (car exprs)) (eval-these (cdr exprs)))))

(def trunc/pad (s n (o padding #\space))
  (= s (tostring:pr s))
  (let lens (len s)
    (if (< lens n)
        (+ s (newstring (- n lens) padding))
        (is lens n)
        s
        (cut s 0 n))))

(def text-column-writer cols
  (fn args
    (map (fn (col data) (pr:trunc/pad data col)) cols args)
    (prn)))

(def benchmark (times fun (o verbose nil))
  (pr "warm-up   ")
  (for i 1 times
    (fun)
    (if (and verbose (is (mod i 10) 0))
      (pr ".")))
  (prn)
  (pr "benchmark ")
  (with (mintime 2000000000 maxtime 0 totaltime 0 now nil)
    (for i 1 times
      (= now (msec))
      (fun)
      (let elapsed (- (msec) now)
        (zap [min _ elapsed] mintime)
        (zap [max _ elapsed] maxtime)
        (zap [+ _ elapsed] totaltime)
        (if (and verbose (is (mod i 10) 0))
            (pr "."))))
    (prn)
    (obj avg (/ totaltime times 1.0) min mintime max maxtime)))

(mac bm (times . body)
  `(benchmark ,times (fn () ,@body)))

(mac bmv (times . body)
  `(benchmark ,times (fn () ,@body) t))

(assign bm-tests nil)

(mac defbm (name withses . body)
  (w/uniq gcount
    (let bmfn `(fn (,gcount) (with ,withses (bmv ,gcount ,@body)))
      `(push (list ',name ,bmfn) bm-tests))))

(def run-benchmark-suite (repeat-count)
  (w/table t
    (with (i 0 n (len bm-tests))
      (each (name test) (sort car< bm-tests)
        (prn "benchmark " (++ i) " of " n " " name)
        (let result (test repeat-count)
          (= t.name result)
          (prn "avg " result!avg))))))

(def rbs-report (report)
  (let r (text-column-writer 25 10 10 10)
    (r "" "avg" "min" "max")
    (each (k v) (sort car< (tablist report))
      (r k v!avg v!min v!max)))
  )

(def rbs ((o count 200))
  (wipe bm-tests)
  (load "lib/bm-tests.arc")
  (rbs-report (run-benchmark-suite count)))

(mac dfn (name params . body)
  `(= ,name (fn ,(dfn-params params)
              (with ,(dfn-withses params)
                ,@body))))

(def dfn-p-mapper (p)
  (if (acons p) (map2 dfn-p-mapper p)
                (let toks (tokens (coerce p 'string) #\:)
                  (if cdr.toks
                      (if (is car.toks "?")
                          `(o ,(sym:cadr toks))
                          (sym:cadr toks))
                      p))))

(def dfn-params (params)
  (map2 dfn-p-mapper params))

(def dfn-param (p)
  (let toks (tokens (coerce p 'string) #\:)
    (if (and cdr.toks (isnt car.toks "?"))
        `(,(sym:cadr toks) (,(sym:car toks) ,(sym:cadr toks))))))

(def dfn-withses (params)
  (mappend idfn (accum x
    (afnwith (p params)
      (if (no p)   nil
          (atom p) (aif dfn-param.p x.it)
                   (do (self:car p) (self:cdr p)))))))

(dfn load-file (string:fname)
  (w/infile f fname (readstring f)))

(def find-in-path (file)
  (alet (string file ".arc")
    (if (file-exists it) it)))

(assign *required-libs* ())

(dfn require-lib (string:arc-lib)
  (if (no (find arc-lib *required-libs*))
    (aif (find-in-path arc-lib)
      (do (push arc-lib *required-libs*)
          (load it))
      (err:string "Didn't find " arc-lib))))

(mac requires (funcs lib)
  (if (acons funcs)
      `(do ,@(map [list 'requires _ lib] funcs))
      `(def ,funcs args
         (require-lib ',lib)
         (apply ,funcs args))))

(mac require-by-name (path . funcs)
  `(do
     ,@(map [quasiquote (requires ,_ ,(string path _))] funcs)))
     
(def as-output (f)
  (if (is type.f 'output)
      f
      (outfile f)))

; w/thanks to aw
(mac toerr body
  `(w/stdout (stderr) ,@body))

(mac define-dirs (collection-name . names)
  `(do ,@(map (fn (_) `(def ,_ ((o f "")) (string "arc/" ',collection-name ',_ f))) names)
       (= ,collection-name (list ,@names))))
