;; contributed by fallintothis at http://arclanguage.org/item?id=10503
;; todo: doesn't work on rainbow due to restriction of integers to 63 bits

(= digits-per-line*     10
   default-stop-digits* 1000)

(def make-digit-generator ()
  (with (zq 1 zr 0 zt 1 k 0 4k+2 2 2k+1 1)
    (with (extract [trunc:/ (+ (* zq _) zr) zt]
           comp    (fn (aq ar at bq br bt)
                     (= zq (* aq bq)
                        zr (+ (* aq br) (* ar bt))
                        zt (* at bt))))
      (fn ()
        (let y (extract 3)
          (until (is y (extract 4))
            (prn zq " " zr " " zt " " k " " 4k+2 " " 2k+1)
            (comp zq zr zt (++ k) (++ 4k+2 4) (++ 2k+1 2))
            (= y (extract 3)))
          (comp 10 (* -10 y) 1 zq zr zt)
          y)))))

(def spigot ((o digits default-stop-digits*))
  (with (digits-printed 0
         next-digit     (make-digit-generator))
    (while (> digits 0)
      (if (>= digits digits-per-line*)
          (do (repeat digits-per-line* (pr (next-digit)))
              (++ digits-printed digits-per-line*))
          (do (repeat digits (pr (next-digit)))
              (sp (- digits-per-line* digits))
              (++ digits-printed digits)))
      (prn "\t:" digits-printed)
      (-- digits digits-per-line*))))

