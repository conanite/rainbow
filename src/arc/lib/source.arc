(def all-sources (roots)
  (if (no roots)
      nil
      (is roots ".")
      (all-sources (dir roots))
      (let first (car roots)
        (flat:cons
          (if (dir-exists first)
              (all-sources (map [string first "/" _] (dir first)))
              (endmatch ".arc" first)
              first)
          (all-sources (cdr roots))))))

(def index-defs (files itable)
  (each f files
    (prn "indexing definitions from " f)
    (index-from-file f
                     (find-defs f)
                     itable)))

(def index-from-file (file toks itable)
  (each tok toks
    (zap [cons (list file (tok 3)) _]
         (itable (cadr tok)))))

(def delete-from-index (itable remfn)
  (each (k v) itable
    (= itable.k (rem remfn v))))

(assign definers (cdr '(x def mac redef assign defop newsop defweld atdef)))

(mac fdcall (fun) `(,fun car.tkz cdr.tkz acc))

(let (fd1 fd2 fd3) nil
  (defs
    fd1 (tok tkz acc) (if tok
                          (fdcall (if (token? tok 'syntax 'left-paren) fd2 fd1)))
    fd2 (tok tkz acc) (if tok
                          (fdcall
                            (if (find cadr.tok definers)  fd3
                                (is car.tok 'whitespace)  fd2
                                                          fd1)))
    fd3 (tok tkz acc) (if (and tok (~is car.tok 'whitespace))
                          (do (if (is car.tok 'sym)
                                  (acc tok))
                              (fdcall fd1))
                          (fdcall fd3))
    find-defs (source)
      (accum acc
        (let tkz (car:index-source (infile source))
          (fdcall fd1)))))
