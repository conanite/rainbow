(def writebs (bytes stream)
  (each b bytes (writeb b stream)))

(def to-4-byte-word (number)
  (withs (low     (mod number 256)
          number  (trunc:/ number 256)
          2nd     (mod number 256)
          number  (trunc:/ number 256)
          3rd     (mod number 256)
          high    (trunc:/ number 256))
    (list high 3rd 2nd low)))

(def to-vlf (number)
  (let conv (afn (n)
                 (if (< n 128) (list n)
                     (with (a (mod n 128)
                            b (self (trunc:/ n 128)))
                       (cons a (cons (+ 128 car.b) cdr.b)))))
    (rev:conv number)))

(def write-vlf (number str)
  (writebs (to-vlf number) str))

(def only-notes (music)
  (accum a
    (each event music
      (if (in event.1 'note-on 'note-off) (a event)))))

(let command-bytes (obj note-on 144 note-off 128)
  (def command-byte (event-type channel)
    (+ command-bytes.event-type channel)))

(def accum-midi-event (event last-tick acc)
  (let delta (- event.0 last-tick)
    (each b (to-vlf (round:* delta 120))
      (acc b))
    (acc (command-byte event.1 event.2))
    (acc event.3)
    (acc (if (> event.4 127) 127 event.4))
    event.0))

(let track-footer (list 130 0 255 47 0)
  (def write-midi-track (str byte-list)
    (writebs (to-4-byte-word (+ 5 (len byte-list))) str)
    (writebs byte-list str)
    (writebs track-footer str)))

(let midi-type-0-header
     (list 77 84 104 100  ; "MThd"
            0  0   0   6  ; header chunk always length 6
                   0   0  ; format type 0
                   0   1  ; number of tracks, always 1 for format type 0
                   2   0  ; tempo - TODO: figure this out
           77 84 114 107) ; "MTrk"
  (def write-midi-file (music str)
    (each b midi-type-0-header (writeb b str))
    (with (events (only-notes music) tick 0)
      (write-midi-track str
        (accum bytes
          (each event events
            (= tick (accum-midi-event event tick bytes))))))))

(def write-midi-to (name music)
  (w/outfile f name
    (write-midi-file music f)
    (close f)))

(def hex-dump (file)
  (w/infile f file
    (let b (readb f)
      (while b 
        (pr (coerce b 'string 16) " ")
        (assign b (readb f))))
    (close f)))

(def tst-write-midi ()
  (write-midi-to "tst.midi" (make-music 0 (s2/4/5 e4 90 80 1))))

