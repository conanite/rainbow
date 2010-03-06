; W.A.Mozart KV331 "Rondo a la turca"

(require-lib 'lib/midi/midi)

(def rondo-motif-2 ((o tail) (o minor-3rd))
  (with (last-note (if tail '(-5))
         last-vl   (if tail '(((95 4) (95 2)) (75 2)) '((95 4))))
    (let seq (if minor-3rd
                 `((0 3) (2 5) (3 7) (3 7) 8 7 5 3 (2 -1) ,@last-note)
                 `((0 4) (2 5) (4 7) (4 7) 9 7 5 4 (2 -1) ,@last-note))
      (apply-template
        `( (75 2 staccato) (75 2 staccato) (90 2 staccato) (75 2 staccato) 
           (75 1) (75 1) (75 1) (75 1) ,@last-vl)
        seq))))

(= bass-template1 (templatizer `( (95 2) ,@(times 3 '(80 2 staccato))))
   bfb-bass  ((transpose b2)  (bass-template1 (abbb 7 12)))
   ebe-bass  ((transpose e3)  (bass-template1 (abbb 7 12)))
   bgb-bass  ((transpose b2)  (bass-template1 (abbb 9 12)))
   fac-bass  ((transpose f3s) (bass-template1 (abbb 3 7 )))
   ace-bass  ((transpose a3)  (bass-template1 (abbb 3 7 )))
   gbc-bass  ((transpose g3s) (bass-template1 (abbb 3 5 )))
   egc-bass  ((transpose e3)  (bass-template1 (abbb 3 8 )))
   egd-bass  ((transpose e3)  (bass-template1 (abbb 4 10)))
   gce-bass  ((transpose g3s) (bass-template1 (abbb 5 8 )))
   gdf-bass  ((transpose g3s) (bass-template1 (abbb 7 10)))
   fad-bass  ((transpose f3)  (bass-template1 (abbb 4 10)))
   bass-template2 (templatizer `( (95 2 staccato) ,@(times 3 '(80 2 staccato)) ))
   ace-bass2 ((transpose a3)  (bass-template2 (abab 3 7 )))
   ebe-bass2 ((transpose e3)  (bass-template2 (+ (ab 7 12) (octave -5))))
   gbde-bass ((transpose g3s) (bass-template2 (bcac 3 6 8))))

(def rondo-motif-3 ((o downward))
  (list (list:b5 90 2 '(arp -4 -2) 'staccato)
        (chord f5s '(3) 75 2 'staccato)
        (chord e5 '(3) 75 2 'staccato)
        (chord (if downward d5s f5s) '(3) 75 2 'staccato)))

(def rondo-left-motif-2 ((o minor))
  (let interval (if minor 3 4)
    `( ((0 95 2 staccato))
       ((12 85 2 staccato)) 
       ((,interval 85 2 staccato)) 
       ((,(+ interval 12) 85 2 staccato)) 
       ((7 95 4))
       (pause 4))))

(defseq rondo-passage-1-left
    '((pause 4))
    (repeat-list 2 ace-bass)
    ace-bass2
    ace-bass
    (repeat-list 2 ebe-bass)
    ebe-bass2
    (mono (e3 90 4)))

(defseq rondo-passage-2-left
    '((pause 4))
    (loud/quiet ((transpose c3) (rondo-left-motif-2)))
    (loud/quiet ((transpose a2) (rondo-left-motif-2 t)))
    (repeat-list 2 ace-bass)
    ace-bass2
    fad-bass
    ((transpose e3) (bass-template2 (ab 5 12)))
    ((transpose d3) (bass-template2 (ab 3 9)))
    ((transpose c3) (bass-template2 (ab 4 9)))
    ((transpose d3) (bass-template2 (ab 3 9)))
    ((transpose e3) (bass-template2 '( (0 5) (0 5) (0 4) (0 4) )))
    (list:chord a2 '(12) 100 4))

(def rondo-passage-3-left-motif (note count (o minor))
  (let third (if minor -9 -8)
    (apply mono 
      (note 95 2 'staccato `(arp -12 ,third -5))
      (times (- count 1) (note 75 2 'staccato)))))

(def left-3 (coda?)
  (let ending (if coda? (rondo-passage-3-left-motif a3 2) (mono (a2 95 4)))
    (+
      '((pause 4))
      (repeat-list 2 (rondo-passage-3-left-motif a3 4))
      (rondo-passage-3-left-motif d3 2)
      (rondo-passage-3-left-motif d3s 2 t)
      (rondo-passage-3-left-motif e3 4)
      (repeat-list 2 (rondo-passage-3-left-motif a3 4))
      (rondo-passage-3-left-motif d3 2)
      (rondo-passage-3-left-motif e3 2)
      ending)))

(defseq rondo-passage-6-left (loud:left-3 nil) (quiet:left-3 t))

(= rondo-passage-3-left (left-3 nil))

(defseq rondo-passage-5-left
    '((pause 4))
    ace-bass gbde-bass
    ace-bass egd-bass
    ace-bass gbde-bass
    (s-3/-7/-5 a3 90 75 2)
    (mono (a2 95 2 'staccato) (a3 75 2 'staccato))
    '((pause 4))
    fac-bass gbc-bass
    fac-bass 
    ((transpose c3s) (bass-template1 '(0 (7 12) (6 12) (5 12))))
    bfb-bass bgb-bass
    ((transpose c3s) (bass-template2 '(0 (5 8) 0 (7 10))))
    (list:chord f3s '(3) 100 4))

(def rondo-passage-7-left-motif-1 () 
  (mappend [rondo-passage-3-left-motif _ 4] (list a3 a3 a3 d3 a3 e3)))

(def rondo-passage-7-left-semiqv-seq () (quiet
  (repeat-list 4 (s7/4/7 a3 80) (s7/4/7 a3 70))
  (s9/6/9 a3 80)   (s9/6/9 a3 70)
  (s7/4/7 a3 80)   (s7/4/7 a3 70)
  (s12/4/12 e3 80) (s12/4/12 e3 70)))

(defseq rondo-passage-7-left
  (mono (a3 75 2 'staccato) (a3 75 2 'staccato))
  (rondo-passage-7-left-motif-1)
  (rondo-passage-7-left-motif-1)
  (rondo-passage-7-left-semiqv-seq)
  (loud
    (rondo-passage-7-left-motif-1)
    (rondo-passage-3-left-motif a3 4)
    (rondo-passage-3-left-motif a3 4)
    (rondo-passage-3-left-motif a3 4)
    (rondo-passage-3-left-motif a3 2)
    (rondo-passage-3-left-motif a3 2))
  (mono:a3 100 4 'staccato)
  (list (chord a3 '(4 7 12) 127 4)
        (chord a3 '(4 7 12) 127 4)))

(defseq rondo-passage-4-opening
  (s1/0/-2  c6s 70) (s2/0/-1  a5  96)
  (s3/2/0   f5s 70) (s1/3/0   f5  96)
  (s2/4/0   c5s 70) 
  ((crescendo 16 8) (+
    (s-1/0/2  f5s 96) (s-1/0/2  a5  70))))

(defseq rondo-passage-4-left
    '((pause 4))
    fac-bass gbc-bass
    fac-bass egc-bass
    fac-bass gce-bass
    gdf-bass `(,(chord c4s '(3) 95 4)) )

(def rondo-theme-3 (first?)
  (with (tf (if first? staccatoise octavise-alt)
         o1 (if first? 'staccato 'octavise))
    (+ (mono (a4  70 2 o1) (b4  70 2 o1))
       (if first? (mono (c5s 100 4)) 
                  (mono (c5s 100 2 'octavise) '(pause 2)))
       (mono (a4  70 2 o1) (b4  70 2 o1))
       (tf:s-2/-4/-5 c5s 100 75 2))))

(def rondo-theme-3-end-1 (first?)
  (with (tf (if first? staccatoise octavise-alt)) (+
    (tf:s2/3/5 f4s 85 75 2)
    (mono (g4s 90 2 (if first? nil 'octavise)) 
          (e4  75 2 (if first? 'staccato 'octavise))))))

(def rondo-theme-3-end-2 (first?)
  (with (tf (if first? staccatoise octavise-alt)) (+
    (tf:s5/2/-2 f4s 85 75 2)
    (list:chord a4 '(12) 95 4))))

(def rondo-theme (cresc) (+
    (s-2/-3/-2 b4 80)
    (mono (c5 100 2 'staccato) '(pause 2))
    (s-2/-3/-2 d5 80)
    (mono (e5 100 2 'staccato) '(pause 2))
    (s-1/-2/-1 f5 80)
    ((if cresc (crescendo 20 8) idfn) (repeat-list 2 (s-2/-3/-2 b5 80)))
    (mono (c6 (if cresc 120 100) 4))))

(def rondo-theme-7-transition ()
  (loud/quiet:s-1/-3/-1 d6 96 64 1))

(def rondo-theme-7 () (+
  `(,(chord-arp c6s '(-4 -9 -12) 100 '(8 4 4 4) '(-12 -9 -4))
    (pause 4)
    ,(chord-arp c6s '(-4 -9 -12) 100 '(8 4 4 4) '(-12 -9 -4))
    (pause 4))
  (rondo-theme-7-transition)
  `(,(chord d6 '(-5 -8) 100 8))))

(def rondo-theme-7-a5-chord (vol length)
  (chord-arp a5 '(4 -5) vol length '(5) 'staccato))

(def rondo-theme-7-e5-chord (vol)
  (chord-arp e5 '(4 7) vol 2 '(9) 'staccato))

(def rondo-theme-7-a5-e5-chord-sequence ()
  (cons (rondo-theme-7-a5-chord 64 8)
        (map rondo-theme-7-e5-chord '(96 64 64 64))))

(defseq rondo-passage-7
    (list (chord c5s '(12) 64 '(4 2.75))
          (list:c6s 64 1.25))
    (rondo-theme-7)
    (map [rondo-theme-7-a5-chord _ 2] '(96 64 64 64))
    (list (chord e5 '(4 7) 96 '(8 8 6))
          (list:e6 64 2 'staccato))
    (rondo-theme-7)
    (rondo-theme-7-a5-e5-chord-sequence)
    (quiet
      (mono (a5 80 4)
        (c6s 64 2.75 '(arp -9 -4))
        (c6s 64 1.25)
        (c6s 64 8 '(arp -9 -4))
        (c6s 64 8 '(arp -9 -4)))
      (rondo-theme-7-transition)
      (mono (d6 96 8)
        (c6s 80 2 '(arp 1))
        (c6s 64 2 '(arp 1))
        (c6s 64 2 '(arp 1))
        (c6s 64 2 '(arp 1))
        (b5 80 6)
        (e6 64 2)))
    (loud 
      (rondo-theme-7)
      (rondo-theme-7-a5-e5-chord-sequence)
      ((crescendo 10 12) (+
        (list (chord a4 '(4 7 12) 96 6))
        (loud:octavise-chord:mono
          (c5s 70 2 'staccato)
          (a4 96 6)
          (e5 70 2 'staccato)
          (a4 96 6)
          (c5s 70 2 'staccato)
          (a4 96 2 'staccato)
          (c5s 70 2 'staccato)
          (a4 80 2 'staccato)
          (e5 70 2 'staccato)
          '(speed 4/5)
          (a4 97 4 'staccato)))))
    (list '(speed 2/3) (chord a4 '(4 7 12) 127 4)
          '(speed 1/2) (chord a4 '(4 7 12) 127 4)))

(defseq rondo-passage-6
  (rondo-theme-3 nil)
  (rondo-theme-3-end-1 nil)
  (rondo-theme-3 nil)
  (rondo-theme-3-end-2 nil))

(defseq rondo-passage-5-motif
  (s-2/-3/-5 e5 75)
  (major-scale a4 90 75 1)
  (s-1/-3/-5 a5 90)
  (s-2/-3/-5 e5 75))

(defseq rondo-passage-5
   (loud
     rondo-passage-5-motif
     (major-scale a4 90 75 1)
     (mono (a5s 95 2) (b5 55 2 'staccato))
     rondo-passage-5-motif
     (s3/-4/0 c5s 90)
     (s3/-3/0 b4  90)
     (mono (a4 95 4)))
   (quiet
     rondo-passage-4-opening
     ((compose (amp 20) (crescendo 20 8))
      (s-1/0/-1  c6s 96)
      (s-1/0/-3  c6s 70))
     ((compose (amp 40) (crescendo -40 16))
      (s-1/0/-1  d6  100)
      (s-1/0/-1  d6  75))
     (s-1/-3/-5 d6  90)
     (s1/3/0    g5s  75)
     (s2/4/-3   a5  75)
     (s1/3/0    f5  75)
     (mono (f5s 95 4))))

(= rondo-passage-4 (quiet
   rondo-passage-4-opening
   ((compose (crescendo -20 8) (amp 20))
    (s-1/0/-1 c6s 96)
    (s1/0/-2  c6s 70))
   (s2/0/-1  a5  96)
   (s3/2/0   f5s 70)
   (s2/4/0   e5  96)
   (s2/3/0   c5s 70)
   (s1/3/0   d5s 96)
   (s1/3/0   c5  70)
   (mono (c5s 100 4))))

(= rondo-passage-3
  (octavise-chord:loud
    (rondo-theme-3 t)
    (rondo-theme-3-end-1 t)
    (rondo-theme-3 t)
    (rondo-theme-3-end-2 t)))

(defseq rondo-passage-2 
   (loud  ((transpose c5) (rondo-motif-2 t)     ))
   (quiet ((transpose c5) (rondo-motif-2)       ))
   (loud  ((transpose a4) (rondo-motif-2 t t)   ))
   (quiet ((transpose a4) (rondo-motif-2 nil t) ))
   (rondo-theme t)
   (quiet
     (mono (a5  75  2 'staccato)
       (b5  75  2 'staccato)
       (c6  100 2 'staccato)
       (b5  75  2 'staccato)
       (a5  75  2 'staccato)
       (g5s 75  2 'staccato)
       (a5  90  2 'staccato)
       (e5  75  2 'staccato)
       (f5  75  2 'staccato)
       (d5  75  2 'staccato)
       (c5  95  4)
       (b4  75  4 '(trillo-mordant 1 -2))
       (a4  95  4)) ))

(= rondo-passage-1 (quiet
    (rondo-theme nil)
    (mono (a5 80 2 'staccato) (c6 80 2 'staccato))
    (rondo-motif-3)
    (rondo-motif-3)
    (rondo-motif-3 t)
    (mono (e5 100 4))))

(defseq rondo-right-hand 
  (mappend loud/quiet (list
    rondo-passage-1 rondo-passage-2 
    rondo-passage-3 rondo-passage-4 
    rondo-passage-5 rondo-passage-3 
    rondo-passage-1 rondo-passage-2
    rondo-passage-6) )
  rondo-passage-7)

(defseq rondo-left-hand
  (mappend loud/quiet (list 
    rondo-passage-1-left rondo-passage-2-left
    rondo-passage-3-left rondo-passage-4-left 
    rondo-passage-5-left rondo-passage-3-left
    rondo-passage-1-left rondo-passage-2-left))
  rondo-passage-6-left
  rondo-passage-7-left)

(= rondo-music
   (make-music 0 rondo-right-hand
               0 ((amp -5) rondo-left-hand)))
