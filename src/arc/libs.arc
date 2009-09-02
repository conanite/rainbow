(map load '("strings.arc"
            "pprint.arc"
            "code.arc"
            "html.arc"
            "srv.arc"
            "app.arc"
            "prompt.arc"
            "lib/bag-of-tricks.arc"))

(map require-lib '(
  lib/unit-test
  lib/parser
))