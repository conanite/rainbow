(require-lib 'rainbow/midi/midi)
(require-lib 'lib/midi/prelude)

(def prelude ()
  (= tick-size 0.2)
  (play-sequence (make-music 0 prelude-measures)))

