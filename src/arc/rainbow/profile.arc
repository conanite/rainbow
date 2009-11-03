(assign profile-reporter
        (text-column-writer 14 14 14 200)
        caller-count-reporter
        (text-column-writer 42 14 200))

(def merge-caller-counts (left right)
  (let mcc (listtab left)
    (each (f c) right
      (= mcc.f (+ c (or mcc.f 0))))
    (tablist mcc)))

(def get-invocation-profile (th)
  (if (is (type th) 'thread)
      rainbow-profile-report.th!invocation-profile
      th))

(def merge-profile-item (merged item)
  (aif (find [is _.3 item.3] merged)
       (do (for i 0 2 (++ it.i item.i))
           (= it.4 (merge-invocation-profiles it.4 item.4))
           (= it.5 (merge-caller-counts it.5 item.5))
           merged)
       (cons item merged)))

(dfn merge-invocation-profiles (get-invocation-profile:left get-invocation-profile:right)
  (if right
      (merge-invocation-profiles (merge-profile-item left car.right) cdr.right)
      left))

(assign new-thread-without-profiling new-thread)

(let profiled-threads nil
  (def profiling-on ()
    (assign new-thread (fn (f)
      (let nt (new-thread-without-profiling (fn () (rainbow-profile) (f)))
        (push nt profiled-threads)
        nt)))
    t)

  (def profiling-off ()
    (assign new-thread new-thread-without-profiling)
    (show-profile-report (obj invocation-profile
                              (reduce merge-invocation-profiles profiled-threads)))
    (wipe profiled-threads)))

(mac profiler expr
  `(after (do (rainbow-profile) ,@expr)
          (html-prof (rainbow-profile-report))))

(def show-profile-report (report)
  (prn "Invocation profiles")
  (prn "=================")
  (profile-reporter "total-time" "own-time" "invocations" "fn")
  (each item (sort car> report!invocation-profile)
    (profile-report-fn "" item)))

(def show-instruction-profile (report)
  (prn "Rainbow vm-instruction counts")
  (prn "=============================")
  (let r (text-column-writer 10 200)
    (r "count" "instruction class")
    (each (value . name) report!instruction-profile
      (r value name))))

(def millify (time)
  (/ (int (* time 1000)) 1000.0))

(def profile-report-fn (indent (all-nanos my-nanos count object kidz callers))
  (profile-reporter (string millify.all-nanos 'ms) (string millify.my-nanos 'ms) count (tostring:pr indent object))
  (each (f c) callers
    (caller-count-reporter "" (+ indent "  " c) f))
  (each item (sort car> kidz)
    (profile-report-fn (+ indent "  ") item)))

(attribute tr         class          opstring)
(attribute table      class          opstring)
(attribute th         colspan        opnum)

(def prof-tr (indent parent (all-nanos my-nanos count object kidz callers))
  (prn)
  (tag (tr class 'profiled)
    (tag td (pr:string millify.all-nanos 'ms))
    (tag td (pr:string millify.my-nanos 'ms))
    (tag td pr.count)
    (tag (td style "padding-left:#(indent)px;") (pr object)))
  (prof-tr-callers object parent indent callers)
  (each item (sort car> kidz)
    (prof-tr (+ indent 12) object item)))

(def prof-tr-callers (self parent indent callers)
  (when callers
    (prn)
    (tag tr
      (tag td)
      (tag td)
      (tag td)
      (tag (td style "padding-left:#(indent)px;")
        (tag (table class 'callers)
          (tag tr (tag (th colspan 2 align 'left ) (pr "callers")))
          (each (caller count) callers
            (tag tr
              (tag (td class 'count) (pr count))
              (tag td (pr (if (is self caller)
                              "<b>&lt;self&gt;</b>"
                              (is parent caller)
                              "<b>&lt;parent&gt;</b>"
                              caller))))))))))

(assign html-prof-style "
table {
  border-spacing: 0px;
  border-collapse: separate;
}

table.callers {
  border: 1px solid #EEF;
}

table.callers td.count {
  width: 10%;
  padding-left: 5px;
}

td {
  vertical-align: top;
  font: 10pt monospace;
  padding-right: 10px;
}

tr.profiled>td {
  border-top: 1px solid gray;
}

th {
  white-space: nowrap;
}

")

(def html-prof-help ()
  (tag div
    (tag p
      (tag b (pr "total time"))
      (pr " - is the total time spent within the function, including lexically nested functions"))
    (tag p
      (tag b (pr "own time"))
      (pr " - is the time spent within the function, not including lexically nested functions"))
    (tag p
      (tag b (pr "invocations"))
      (pr " - is the number of times the function was invoked"))
    (tag p
      (tag b (pr "callers"))
      (pr " - breakdown of invocation count by caller"))
    (tag p
      (tag b (pr "callers: '&lt;self&gt;'"))
      (pr " - number of times a function calls itself directly (e.g. via (afn ...) or (rfn ...))"))
    (tag p
      (tag b (pr "callers: '&lt;parent&gt;'"))
      (pr " - number of times a function is called by the lexically enclosing function (the most common case, e.g. (let ...) and (with ...))"))
  ))

(def html-prof (prof)
  (let f "profile-report-#((msec)).html"
    (w/outfile o f
      (w/stdout o
        (tag html
          (tag head (tag style (pr html-prof-style)))
          (tag body
            (html-prof-help)
            (tag table
              (tag tr
                (tag th (pr "total time"))
                (tag th (pr "own time"))
                (tag th (pr "invocations")))
              (each item (sort car> prof!invocation-profile)
                (prof-tr 5 nil item)))))))
    (system "open #(f)")))
