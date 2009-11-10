(mac profiler expr
  `(after (do (rainbow-profile) ,@expr)
          (html-prof ((rainbow-profile-report) 'invocation-profile))))

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
    (html-prof (reduce merge-invocation-profiles profiled-threads))
    (wipe profiled-threads)))

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
           (= it.6 (merge-caller-counts it.6 item.6))
           merged)
       (cons item merged)))

(dfn merge-invocation-profiles (get-invocation-profile:left get-invocation-profile:right)
  (if right
      (merge-invocation-profiles (merge-profile-item left car.right) cdr.right)
      left))

(def millify (time)
  (/ (int (* time 1000)) 1000.0))

(def percentify (time total)
  (/ (int (* 1000.0 (/ time total))) 10.0))

(attribute tr         class          opstring)
(attribute tr         id             opstring)
(attribute table      class          opstring)
(attribute th         colspan        opnum)
(attribute a          name           opstring)

(def id-for-fn (ids fn-as-string)
  (or ids.fn-as-string
      (= ids.fn-as-string (uniq))))

(def prof-tr (ids indent parent overall (all-nanos my-nanos count object kidz callers callees))
  (prn)
  (tag (tr class 'profiled id (id-for-fn ids object))
    (tag td (pr millify.all-nanos 'ms " (" (percentify all-nanos overall) "%)" ))
    (tag td (pr millify.my-nanos 'ms))
    (tag td pr.count)
    (tag (td style "padding-left:#(indent)px;")
      (tag (a name (id-for-fn ids object))
        (pr-escaped object))))
  (prof-tr-callers 'callers ids object parent nil indent callers)
  (prof-tr-callers 'callees ids object parent (only-child kidz) indent callees)
  (each item (sort car> kidz)
    (prof-tr ids (+ indent 12) object overall item)))

(def only-child (kidz) (if (is len.kidz 1) kidz.0.3))

(def only-parent (parent callers)
  (and (is len.callers 1) (is caar.callers parent)))

(def pr-caller (caller self parent child)
  (if (is self caller)   (pr "<b>&lt;self&gt;</b>")
      (is parent caller) (pr "<b>&lt;parent&gt;</b>")
      (is child caller)  (pr "<b>&lt;child&gt;</b>")
                         pr-escaped.caller))

(def prof-tr-callers (label ids self parent child indent callers)
  (when (and callers (no:only-parent parent callers))
    (prn)
    (tag tr
      (tag td) (tag td) (tag td)
      (tag (td style "padding-left:#((+ indent 12))px;")
        (tag (table class 'callers)
          (tag tr (tag (th colspan 2) (pr label)))
          (each (caller count) (sort cadr> callers)
            (tag tr
              (tag (td class 'count) (pr count))
              (tag td
                (tag (a href (string #\# (id-for-fn ids caller)))
                  (pr-caller caller self parent child))))))))))

(assign html-prof-style "
table { border-spacing: 0px; border-collapse: separate; }
table.callers { border: 1px solid #EEF; }
table.callers td.count { width: 10%; padding-left: 5px; }
td { vertical-align: top; font: 10pt monospace; padding-right: 10px; }
tr.profiled>td {border-top: 1px solid gray; }
th { white-space: nowrap; text-align: left; }
")

(def html-prof-help ()
  (tag (div style "width:600px;")
    (tag p
      (tag b (pr "total time"))
      (pr " - is the total time spent within the function, including lexically
              nested functions"))
    (tag p
      (tag b (pr "own time"))
      (pr " - is the time spent within the function, not including lexically
              nested functions"))
    (tag p
      (tag b (pr "invocations"))
      (pr " - is the number of times the function was invoked. Some functions
              have an apparently impossible invocation count of zero - these
              are usually of the form (fn nil ...) ; they are (do ...) forms
              that rainbow optimizes away so they never get counted."))
    (tag p
      (tag b (pr "function and callers"))
      (pr " - names the function being profiled. If the function has a global
              shows that name, otherwise shows the code for the function. The
              code is partially macro-compressed where rainbow can recognise
              simple macro-expanded forms (do, let, afn). Indentation in this
              column represents lexical nesting of functions."))
    (tag p
      (tag b (pr "callers"))
      (pr " - breakdown of invocation count by caller. Not shown if parent is
              the sole caller"))
    (tag p
      (tag b (pr "callers: '&lt;self&gt;'"))
      (pr " - number of times a function calls itself directly
              (e.g. via (afn ...) or (rfn ...))"))
    (tag p
      (tag b (pr "callers: '&lt;parent&gt;'"))
      (pr " - number of times a function is called by the lexically enclosing
              function (the most common case, e.g. (let ...) and (with ...))"))))

(def html-prof (prof)
  (with (f "profile-report-#((msec)).html"
         fnids (table)
         overall-time (apply + (map car prof)))
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
                (tag th (pr "invocations"))
                (tag th (pr "function and callers")))
              (each item (sort car> prof)
                (prof-tr fnids 5 nil overall-time item)))))))
    (system "open #(f)")))
