(require-lib 'rainbow/midi/midi)
(require-lib 'lib/midi/rondo)

(def rondo ()
  (= tick-size 0.11)
  (play-sequence rondo-music))
