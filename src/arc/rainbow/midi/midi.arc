(java-import javax.sound.midi.MidiSystem)

(= midiops (table))

(def stop () (= stop-music t))

(mac defmidi (name args . body)
  `(= (midiops ',name) (fn ,args ,@body)))

(defmidi note-on       (channels ch note-number volume) 
                       (channels.ch 'noteOn note-number (if (> volume 127) 127 volume))
                       (pr "."))
(defmidi note-off      (channels ch note-number velocity-ignored)        
                       (channels.ch 'noteOff note-number))
(defmidi instrument    (channels ch bank program)       
                       (channels.ch 'programChange bank program))
(defmidi set-tick-size (channels new-tick-size)         
                       (= tick-size new-tick-size))
(defmidi speed         (channels change)
                       (zap [/ _ change] tick-size))

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
            (apply midiops.command channels args)))))))

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
           ((,c4  100 8))))))))


(def prelude ()
  (require-lib 'lib/midi/prelude)
  (= tick-size 0.2)
  (play-sequence (make-music 0 prelude-measures)))

(def rondo ()
  (require-lib 'lib/midi/rondo)
  (= tick-size 0.11)
  (play-sequence rondo-music))
