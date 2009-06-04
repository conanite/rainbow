
(def bm (times fun (o verbose nil))
  (with (mintime 2000000000 maxtime 0 totaltime 0 now nil)
    (for i 0 (- times 1)
      (= now (msec))
      (fun)
      (let elapsed (- (msec) now)
        (if verbose (prn i " . " elapsed))
        (zap [min _ elapsed] mintime)
        (zap [max _ elapsed] maxtime)
        (zap [+ _ elapsed] totaltime)))
    (prn)
    (prn "avg " (/ totaltime times 1.0))
    (prn "min " mintime)
    (prn "max " maxtime)
    (/ totaltime times 1.0)))

;(assign a (load-file "~/projects/rainbow/src/arc/rainbow/welder.arc"))
;(bm 20 (fn () (index-source a)))
;(bm 10 [prime-bench 20000])

; prime-bench:
; avg 3616.5
; min 3559
; max 3725

; index-source
; avg 4807.9
; min 4726
; max 4952
