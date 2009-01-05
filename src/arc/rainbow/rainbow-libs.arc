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
    "rainbow/swing"
    "rainbow/welder"
    "rainbow/tetris"
    "lib/tests/foundation-test"
    "lib/tests/parser-test"
    "rainbow/tests/anarki-compatibility-test"
    "rainbow/tests/java-interface-test"))

(prn "self-test:")
(run-all-tests)

(let func (random-elt:keys help*)
    (prn "Documentation for " func " " (helpstr func)))
