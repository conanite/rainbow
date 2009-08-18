(require-lib 'rainbow/midi)

(def rondo-motif-2 ((o tail) (o minor-3rd))
  (with (last-note (if tail '(-5))
         last-vl   (if tail '(((95 4) (95 2)) (75 2)) '((95 4))))
    (let seq (if minor-3rd
                 `((0 3) (2 5) (3 7) (3 7) 8 7 5 3 (2 -1) ,@last-note)
                 `((0 4) (2 5) (4 7) (4 7) 9 7 5 4 (2 -1) ,@last-note))
      (apply-template
        `( (75 2 staccato) (75 2 staccato) (90 2 staccato) (75 2 staccato) (75 1) (75 1) (75 1) (75 1) ,@last-vl)
        seq))))

(= bass-template1 (templatizer `( (95 2) ,@(times 3 '(80 2 staccato))))
   bass-template2 (templatizer `( (95 2 staccato) ,@(times 3 '(80 2 staccato)) ))
   bfb-bass  ((transpose b2)  (bass-template1 (abbb 7 12)))
   bgb-bass  ((transpose b2)  (bass-template1 (abbb 9 12)))
   fac-bass  ((transpose f3s) (bass-template1 (abbb 3 7 )))
   gbc-bass  ((transpose g3s) (bass-template1 (abbb 3 5 )))
   egc-bass  ((transpose e3)  (bass-template1 (abbb 3 8 )))
   egd-bass  ((transpose e3)  (bass-template1 (abbb 4 10)))
   gce-bass  ((transpose g3s) (bass-template1 (abbb 5 8 )))
   gdf-bass  ((transpose g3s) (bass-template1 (abbb 7 10)))
   ace-bass  ((transpose a3)  (bass-template1 (abbb 3 7 )))
   ace-bass2 ((transpose a3)  (bass-template2 (abab 3 7 )))
   ebe-bass  ((transpose e3)  (bass-template1 (abbb 7 12)))
   ebe-bass2 ((transpose e3)  (bass-template2 (+ (ab 7 12) (octave -5))))
   gbde-bass ((transpose g3s) (bass-template2 (bcac 3 6 8)))
   fad-bass  ((transpose f3)  (bass-template1 (abbb 4 10))))

(def rondo-motif-3 ((o downward))
  `( ((4 90 2 (arp -4 -2) staccato))
     ,(chord -1 '(3) 75 2 'staccato)
     ,(chord -3 '(3) 75 2 'staccato)
     ,(chord (if downward -4 -1) '(3) 75 2 'staccato)))

(def rondo-left-motif-2 ((o minor))
  (let interval (if minor 3 4)
    `( ((0 95 2 staccato))
       ((12 85 2 staccato)) 
       ((,interval 85 2 staccato)) 
       ((,(+ interval 12) 85 2 staccato)) 
       ((7 95 4))
       ((pause 4)))))

(= rondo-passage-1-left (+
    '((pause 4))
    (repeat-list 2 ace-bass)
    ace-bass2
    ace-bass
    (repeat-list 2 ebe-bass)
    ebe-bass2
    '(((52 90 4)))))

(= rondo-passage-2-left (+
    '((pause 4))
    (loud/quiet ((transpose 48) (rondo-left-motif-2)))
    (loud/quiet ((transpose 45) (rondo-left-motif-2 t)))
    (repeat-list 2 ace-bass)
    ace-bass2
    fad-bass
    ((transpose 52) (bass-template2 (ab 5 12)))
    ((transpose 50) (bass-template2 (ab 3 9)))
    ((transpose 48) (bass-template2 (ab 4 9)))
    ((transpose 50) (bass-template2 (ab 3 9)))
    (bass-template2 '( (52 57) (52 57) (52 56) (52 56) ))
    '(((45 100 4) (57 100 4)))))

(def rondo-passage-3-left-motif (note count (o minor))
  (let third (if minor -9 -8)
    `(((,note 95 2 staccato (arp -12 ,third -5)))
      ,@(times (- count 1) `((,note 75 2 staccato))))))

(def left-3 (coda?)
  (let ending (if coda? (rondo-passage-3-left-motif a3 2) `(((,a2 95 4))))
    (+
      `((pause 4))
      (repeat-list 2 (rondo-passage-3-left-motif 57 4))
      (rondo-passage-3-left-motif 50 2)
      (rondo-passage-3-left-motif 51 2 t)
      (rondo-passage-3-left-motif 52 4)
      (repeat-list 2 (rondo-passage-3-left-motif 57 4))
      (rondo-passage-3-left-motif 50 2)
      (rondo-passage-3-left-motif 52 2)
      ending)))

(= rondo-passage-6-left (+ (loud:left-3 nil) (quiet:left-3 t)))
(= rondo-passage-3-left (left-3 nil))

(= rondo-passage-5-left
  (+
    '((pause 4))
    ace-bass
    gbde-bass
    ace-bass
    egd-bass
    ace-bass
    gbde-bass
    (s-3/-7/-5 a3 90 75 2)
    `(((,a2 95 2 staccato)) ((,a3 75 2 staccato)))
    '((pause 4))
    fac-bass
    gbc-bass
    fac-bass
    (bass-template1 `(,c3s (,g3s ,c4s) (,g3 ,c4s) (,f3s ,c4s)))
    bfb-bass
    bgb-bass
    (bass-template2 `(,c3s (,f3s ,a3) ,c3s (,g3s ,b3)))
    `(,(chord f3s '(3) 100 4))
))

(= rondo-passage-4-opening (+
  (s1/0/-2  c6s 75)
  (s2/0/-1  a5  90)
  (s3/2/0   f5s 75)
  (s1/3/0   f5  90)
  (s2/4/0   c5s 75)
  ((crescendo 16 8) (+
    (s-1/0/2  f5s 90)
    (s-1/0/2  a5  75)))))

(= rondo-passage-4-left
  (+
    '((pause 4))
    fac-bass
    gbc-bass
    fac-bass
    egc-bass
    fac-bass
    gce-bass
    gdf-bass
    `(,(chord c4s '(3) 95 4)) ))

(def rondo-theme-3 (first?)
  (with (tf (if first? staccatoise octavise-alt)
         o1 (list (if first? 'staccato 'octavise)))
    (+
      `( ((,a4  75 2 ,@o1))
       ((,b4  75 2 ,@o1))
       ,@(if first? `(((,c5s 95 4))) `(((,c5s 95 2 octavise)) ((pause 2))) )
       ((,a4  75 2 ,@o1))
       ((,b4  75 2 ,@o1)) )
       (tf:s-2/-4/-5 c5s 95 75 2) )))

(def rondo-theme-3-end-1 (first?)
  (with (tf (if first? staccatoise octavise-alt))
    (+
      (tf:s2/3/5 f4s 85 75 2)
      `( ((,g4s 90 2 ,@(if first? nil '(octavise)))) 
         ((,e4  75 2 ,@(if first? '(staccato) '(octavise)))) ) )))

(def rondo-theme-3-end-2 (first?)
  (with (tf (if first? staccatoise octavise-alt))
    (+
      (tf:s5/2/-2 f4s 85 75 2)
      `(((,a4  95 4) ,@(if first? nil `((,a5 95 4))))) )))

(def rondo-theme (cresc) (+
    (s-2/-3/-2 b4 80)
    `(((,c5 100 2 staccato)) ((pause 2)))
    (s-2/-3/-2 d5 80)
    `(((,e5 100 2 staccato)) ((pause 2)))
    (s-2/-3/-2 f5 80)
    ((if cresc (crescendo 20 8) idfn) (repeat-list 2 (s-2/-3/-2 b5 80)))
    `(((,c6 ,(if cresc 120 100) 4)))))

(= rondo-passage-6
  (+
    (rondo-theme-3 nil)
    (rondo-theme-3-end-1 nil)
    (rondo-theme-3 nil)
    (rondo-theme-3-end-2 nil)
  ))

(= rondo-passage-5-motif (+
  (s-2/-3/-5 e5 75)
  (major-scale a4 90 75 1)
  (s-1/-3/-5 a5 90)
  (s-2/-3/-5 e5 75) ))

(= rondo-passage-5 (+ 
   (loud:+
     rondo-passage-5-motif
     (major-scale a4 90 75 1)
     '( ((82 95 2)) ((83 55 2 staccato)) )
     rondo-passage-5-motif
     (s3/-4/0 c5s 90)
     (s3/-3/0 b4  90)
     `(((,a4 95 4))))
   (quiet:+
     rondo-passage-4-opening
     ((compose (amp 16) (crescendo 16 8)) (+
      (s-1/0/-1  c6s 90)
      (s-1/0/-3  c6s 75)))
     ((compose (amp 32) (crescendo -32 16)) (+
      (s-1/0/-1  d6  100)
      (s-1/0/-1  d6  75)))
     (s-1/-3/-5 d6  90)
     (s1/3/0    80  75)
     (s2/4/-3   a5  75)
     (s1/3/0    77  75)
     `(((,f5s 95 4))))))

(= rondo-passage-4 (quiet:+ 
   rondo-passage-4-opening
   ((compose (crescendo -16 8) (amp 16)) (+ 
    (s-1/0/-1 c6s 90)
    (s1/0/-2  c6s 75)))
   (s2/0/-1  a5  90)
   (s3/2/0   f5s 75)
   (s2/4/0   e5  90)
   (s2/3/0   c5s 75)
   (s1/3/0   d5s 90)
   (s1/3/0   c5  75)
   `(((,c5s 95 4)))  ))

(= rondo-passage-3
  (loud:octavise-chord:+
    (rondo-theme-3 t)
    (rondo-theme-3-end-1 t)
    (rondo-theme-3 t)
    (rondo-theme-3-end-2 t)
  ))

(= rondo-passage-2 (+ 
   (loud  ((transpose c5) (rondo-motif-2 t)     ))
   (quiet ((transpose c5) (rondo-motif-2)       ))
   (loud  ((transpose a4) (rondo-motif-2 t t)   ))
   (quiet ((transpose a4) (rondo-motif-2 nil t) ))
   (rondo-theme t)
   (quiet 
     `(((,a5  75  2 staccato))
       ((,b5  75  2 staccato))
       ((,c6  100 2 staccato))
       ((,b5  75  2 staccato))
       ((,a5  75  2 staccato))
       ((,g5s 75  2 staccato))
       ((,a5  90  2 staccato))
       ((,e5  75  2 staccato))
       ((,f5  75  2 staccato))
       ((,d5  75  2 staccato))
       ((,c5  95  4))
       ((,b4  75  4 (trillo-mordant 1 -2)))
       ((,a4  95  4))) )))

(= rondo-passage-1 (quiet:+
    (rondo-theme nil)
    `(((,a5 80 2 staccato)) ((,c6 80 2 staccato)))
    (repeat-list 2 ((transpose g5) (rondo-motif-3)))
    ((transpose g5) (rondo-motif-3 t))
    `(((,e5 100 4)))
))

(= rondo-right-hand 
   (mappend loud/quiet (list 
      rondo-passage-1 rondo-passage-2 
      rondo-passage-3 rondo-passage-4 
      rondo-passage-5 rondo-passage-3 
      rondo-passage-1 rondo-passage-2
      rondo-passage-6) ))

(= rondo-left-hand
   (+ (mappend loud/quiet (list 
        rondo-passage-1-left rondo-passage-2-left
        rondo-passage-3-left rondo-passage-4-left 
        rondo-passage-5-left rondo-passage-3-left
        rondo-passage-1-left rondo-passage-2-left)) 
      rondo-passage-6-left))

(def rondo ()
  (play-sequence (make-music 
    0 rondo-right-hand
    0 rondo-left-hand)))

(def rondo-test ()
  (= tick-size 0.119)
  (play-sequence (make-music 
    0 ((transpose g5) (rondo-motif-3)))))
