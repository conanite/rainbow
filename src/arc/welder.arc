(def file-control (editor onopen (o startfile))
  (withs (filename (text-field)
          open-button (button "open"
                              (onopen filename!getText)
                              (editor 'setText (load-file filename!getText))))
    (if startfile (do filename!setText.startfile open-button!doClick))
    (box 'horizontal
         filename
         open-button
         (button "save" (write-file filename!getText editor!getText)))))

(def welder-buttons (editor)
  (box 'horizontal
       (button "eval buffer" (eval-these (readall (editor 'getText))))
       (button "eval selection" (eval-these (readall (editor 'getSelectedText))))
       (button "ppr" (editor 'setText (ppr-these (readall (editor 'getText)))))
       (button "new" (welder))))

(def ppr-these (exprs)
  (let os (outstring)
    (w/stdout os (ppr-exprs exprs))
    (inside os)))

(def ppr-exprs (exprs)
  (if (acons exprs)
      (do (ppr (car exprs))
          (prn)
          (prn)
          (ppr-exprs (cdr exprs)))))

(def welder ((o file))
  (with (editor (text-area))
    (editor 'setFont (courier))
    (let f (frame 150 150 800 640 "Arc Welder")
      (f 'add
         (file-control editor 
                       (fn (fname) (f 'setTitle (+ fname " - Arc Welder")))
                       file))
      (f 'add (scroll-pane editor))
      (f 'add (welder-buttons editor))
      f!show)))
