(map require-lib
  '("pprint"
    "code"
    "html"
    "srv"
    "app"
    "prompt"
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
    "rainbow/tests/chained-ssexpand-test"
    "rainbow/tests/string-interpolation-test"
    "rainbow/tests/java-interface-test"))

(def file-join parts
  (apply + parts))

(def qualified-path (path)
  ((java-new "java.io.File" path) 'getAbsolutePath))

(prn "self-test:")
(run-all-tests)

(def welder ()
  (require-lib "rainbow/welder")
  (welder))

(def path-browser ()
  (require-lib "rainbow/fs-browser")
  (path-browser))

(def tetris ()
  (require-lib "rainbow/tetris")
  (tetris))

(def mines ()
  (require-lib "rainbow/mines")
  (mines))


