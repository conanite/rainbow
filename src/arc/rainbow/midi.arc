
(java-import javax.sound.midi.MidiSystem)

(mac define-notes note-names
  (let note 15
    `(= ,@(mappend (fn (name) `(,name ,(++ note))) note-names))))

(define-notes   e0 f0 f0s g0 g0s a0 a0s b0
  c1 c1s d1 d1s e1 f1 f1s g1 g1s a1 a1s b1
  c2 c2s d2 d2s e2 f2 f2s g2 g2s a2 a2s b2
  c3 c3s d3 d3s e3 f3 f3s g3 g3s a3 a3s b3
  c4 c4s d4 d4s e4 f4 f4s g4 g4s a4 a4s b4
  c5 c5s d5 d5s e5 f5 f5s g5 g5s a5 a5s b5
  c6 c6s d6 d6s e6 f6 f6s g6 g6s a6 a6s b6
  c7 c7s d7 d7s e7 f7 f7s g7 g7s a7 a7s b7
  c8 c8s d8 d8s e8 f8 f8s g8 g8s a8 a8s b8)

(def transform (seq (o nt idfn) (o vt idfn) (o lt idfn))
  (let f (fn ((note volume length . options))
             (if (is note 'pause)
                 (list 'pause lt.volume)
                 `(,nt.note ,vt.volume ,lt.length ,@options)))
    (map [if (acons car._) (map f _) (f _)] seq)))

(defs
  tick (tick-count) (do (sleep (* tick-size sub-tick)) (++ tick-count sub-tick))
  transpose    (n) (fn (seq) (transform seq (fn (note) (+ n note))))
  stretch-note (l) (fn (seq) (transform seq idfn idfn (fn (length) (* l length))))
  amp          (n) (fn (seq) (transform seq idfn (fn (vol) (+ vol n))))
)

(= midiops    (table)
   sub-tick   1/2
   tick-size  0.119
   tick-count 0
   stop-music nil
   quiet      (amp -10)
   loud       (amp 10))

(def stop () (= stop-music t))

(mac defmidi (name args . body)
  `(= (midiops ',name) (fn ,args ,@body)))

(defmidi note-on       (channels ch note-number volume) (channels.ch 'noteOn  note-number (if (> volume 127) 127 volume)))
(defmidi note-off      (channels ch note-number)        (channels.ch 'noteOff note-number))
(defmidi instrument    (channels ch bank program)       (channels.ch 'programChange bank program))
(defmidi set-tick-size (channels new-tick-size)         (= tick-size new-tick-size))

(def merge-sequences (seqs)
  (with (nseq  nil
         sortf (fn (a b) 
                   (or (car< a b)
                       (and (is car.a car.b)
                            (any? cadr.a 'note-off 'instrument)))))
    (sort sortf (apply + seqs))))

(mac add-note-on (seq tick channel note vol)
  `(fpush (list ,tick 'note-on ,channel ,note ,vol) ,seq))

(mac add-note-off (seq tick channel note)
  `(fpush (list ,tick 'note-off ,channel ,note) ,seq))

(def create-sequence (ch events)
  (with (seq nil tick 0 process-note nil octavise nil trillo-mordant nil staccato nil pre-arpeggio nil)
    (= trillo-mordant (fn (note vol duration (upper lower))
                    (with (trillo-note  (+ note upper)
                           mordant-note (+ note lower)
                           length       (/ duration 8)
                           trill-tick   tick)
                      (let notes `(,trillo-note  ,note
                                   ,trillo-note  ,note
                                   ,trillo-note  ,note
                                   ,mordant-note ,note)
                        (each n notes
                          (add-note-on seq trill-tick ch n vol)
                          (++ trill-tick length)
                          (add-note-off seq trill-tick ch n))))
                    t)
       pre-arpeggio (fn (note vol intervals)
                    (forlen i intervals
                      (with (start (- tick (* sub-tick (- len.intervals i)))
                             n (+ note intervals.i))
                        (add-note-on seq  start ch n (- vol 5))
                        (add-note-off seq (+ start sub-tick) ch n)))
                    nil)
       staccato (fn (note vol duration)
                    (add-note-on seq tick ch note vol)
                    (add-note-off seq (+ tick (/ duration 2)) ch note)
                    t)
       octavise (fn (note vol duration)
                    (add-note-on  seq tick ch note vol)
                    (add-note-off seq (+ tick (/ duration 2)) ch note)
                    (add-note-on  seq (+ tick (/ duration 2)) ch (+ note 12) vol)
                    (add-note-off seq (+ tick duration) ch (+ note 12))
                    t)
       process-note (fn ((note vol duration . options))
                    (let already-processed nil
                      (each opt options
                        (let processed-now
                          (if (and (acons opt) (is car.opt 'arp))
                              (pre-arpeggio note vol cdr.opt)
                              (and (acons opt) (is car.opt 'trillo-mordant))
                              (trillo-mordant note vol duration cdr.opt)
                              (is opt 'staccato)
                              (staccato note vol duration)
                              (is opt 'octavise) 
                              (octavise note vol duration))
                           (or= already-processed processed-now)))
                      (unless already-processed
                        (add-note-on  seq tick ch note vol)
                        (add-note-off seq (+ tick duration) ch note)))))
    (each event events
      (if (is car.event 'instrument)
          (push (list tick 'instrument ch event.1 event.2) seq)
          (is car.event 'pause)
          (++ tick cadr.event)
          (let this-tick 999999999
            (each note-event event
              (if (is car.note-event 'pause)
                  (zap [min note-event.1 _] this-tick)
                  (do
                    (process-note note-event)
                    (zap [min note-event.2 _] this-tick))))
            (++ tick this-tick))))
    seq))

(def make-music spec
  (let channels (pair spec)
    (merge-sequences (map create-sequence 
                         (map car channels) 
                         (map cadr channels)))))

(def play-sequence (seq)
  (= stop-music nil)
  (let synth (MidiSystem getSynthesizer)
    (synth 'open)
    (with (channels   (synth 'getChannels)
           tick-count 0)
      (thread
        (while (and seq (no stop-music))
          (let (next-tick command . args) pop.seq
            (while (> next-tick tick-count) (zap [tick _] tick-count))
            (pr ".")
            (apply midiops.command channels args)))))))

(def repeat-list (n xs)
  (afnwith (n n)
    (if (is n 1) xs
        (+ xs (self (- n 1))))))

(def loud/quiet (seq)
  (+ (loud seq) (quiet seq)))

(def chord (base intervals vol duration . options)
  `((,base ,vol ,duration ,@options)
    ,@(map (fn (_) `(,(++ base _) ,vol ,duration ,@options)) intervals)))

(def apply-template (vol-len notes)
  (let f (fn (chord vl)
           (if (acons chord)
               (if (acons car.vl)
                   (map (fn (a b) (cons a b)) chord vl)
                   (map [cons _ vl] chord))
               (list (cons chord vl))))
    (map f notes vol-len)))

(def templatizer (template)
  (fn (seq) (apply-template template seq)))

(defs abbb   notes  `(0 ,notes ,notes ,notes)
      ab     notes  `(0 ,notes)
      abab   notes  `(0 ,notes 0 ,notes)
      bcac   notes  `(,(car notes) ,(cdr notes) 0 ,(cdr notes))
      octave (base) `(,base ,(+ base 12))
)

(def crescendo (vol-increment note-count)
  (let incrs (/ vol-increment note-count)
    (fn (seq)
      (withs (incr 0
              f2   (fn ((note vol duration . options))
                       `(,note ,(+ vol (int:++ incr incrs)) ,duration ,@options))
              f1   (fn (note)
                       (if (is (type:car note) 'sym) note (f2 note)))
              f    (fn (notes)
                       (if (is (type:car notes) 'sym) notes (map f1 notes))))
        (map f seq)))))

(def times (n item)
  (if (is n 0) nil
      (cons item (times (- n 1) item))))

(def staccatoise (seq)
  (let s (fn ((note vol duration . options))
             `(,note ,vol ,duration staccato ,@options))
    (let f (fn (notes) (map s notes))
      (map f seq))))

(def octavise-alt (seq)
  (let f (fn (((note vol duration . options)))
             `(((,note ,vol ,(/ duration 2) ,@options)) ((,(+ note 12) ,vol ,(/ duration 2) ,@options))))
    (mappend f seq)))

(def octavise-chord (seq)
  (let f (fn (((note vol duration . options)))
             `((,note ,vol ,duration ,@options) (,(+ note 12) ,vol ,duration ,@options)))
    (map f seq)))

(mac four-note-sequence args
  (let f (fn (seq3)
             (list (mksym 's seq3.0 '/ seq3.1 '/ seq3.2)
                   '(n v1 (o v2 75) (o dur 1))
                   `(list (list:list n v1 dur) 
                          (list:list (+ n ,seq3.0) v2 dur)
                          (list:list (+ n ,seq3.1) v2 dur)
                          (list:list (+ n ,seq3.2) v2 dur))))
    `(defs ,@(mappend f (tuples args 3)))))

(four-note-sequence
  -3 -7 -5
  -3 -7 -3
  -3 -0 -3
  -2 -4 -5
  -2 -3 -5
  -2 -3 -2
  -1 -3 -5
  -1  0 -3
  -1  0 -1
  -1  0  2
   1  0 -2
   1  3  0
   2  0 -1
   2  3  0
   2  3  5
   2  4 -3
   2  4  0
   2  4  5
   3  2  0
   3 -3  0
   3 -4  0
   5  0 -3
   5  2 -2)

(def major-scale (base v1 v2 dur)
  (+ (s2/4/5 base v1 v2 dur)
     (s2/4/5 (+ base 7) v2 v2 dur)))

(def test-programs ()
  (thread
    (= tick-size 0.04)
    (for i 0 127
      (prn "program " i)
      (play-sequence (make-music 0
        `( (instrument 0 ,i)
           ((,c4  100 8))
           ((,e4  100 8))
           ((,g4  100 8))
           ((,a4s 100 8))
           ((,a4  100 8))
           ((,f4  100 8))
           ((,d4  100 8))
           ((,b3  100 8))
           ((,c4  100 8))
        ))))))

