(map require-lib
  '("pprint"
    "code"
    "files"
    "html"
    "srv"
    "app"
    "prompt"
    "ffi"
    "lib/unit-test"
    "lib/parser"
    "lib/tests/foundation-test"
    "lib/tests/core-errors-continuations-test"
    "lib/tests/core-evaluation-test"
    "lib/tests/core-lists-test"
    "lib/tests/core-macros-test"
    "lib/tests/core-maths-test"
    "lib/tests/core-predicates-test"
    "lib/tests/core-special-forms-test"
    "lib/tests/core-typing-test"
    "lib/tests/parser-test"
    "rainbow/tests/anarki-compatibility-test"
    "rainbow/tests/extra-math-test"
    "rainbow/tests/java-interface-test"))

(prn "self-test:")
(run-all-tests)

(let func (random-elt:keys help*)
    (prn "Documentation for " func " " (helpstr func)))

(def welder ()
  (require-lib "rainbow/swing")
  (require-lib "rainbow/welder")
  (welder))

(def path-browser ()
   (require-lib "rainbow/swing")
   (require-lib "rainbow/welder")
   (require-lib "rainbow/fs-browser")
   (path-browser))

(def tetris ()
   (require-lib "rainbow/swing")
   (require-lib "rainbow/tetris")
   (tetris))


