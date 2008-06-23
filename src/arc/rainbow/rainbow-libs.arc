(map require-lib
  '("pprint"
    "code"
    "files"
    "html"
    "srv"
    "app"
    "prompt"
    "ffi"
    "rainbow/unit"
    "rainbow/parser"
    "rainbow/swing"
    "rainbow/welder"
    "rainbow/tetris"
    "rainbow/tests/foundation-test"
    "rainbow/tests/anarki-compatibility-test"
    "rainbow/tests/java-interface-test"))

(prn "self-test:")
(run-all-tests)