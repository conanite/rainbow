(map require-lib
  '(pprint
    code
    html
    srv
    app
    prompt
    rainbow/profile
    lib/unit-test
    lib/parser))

(def file-join parts
  (apply + parts))

(def qualified-path (path)
  ((java-new "java.io.File" path) 'getAbsolutePath))

;(prn "self-test:")
;(run-all-tests)

(require-by-name rainbow/ welder fsb tetris mines)
(requires (rondo prelude) rainbow/midi/midi)
(requires start-spiral-app rainbow/spiral)
