
(register-test '(suite "string interpolation"
  ("interpolate atomic value"
   "there are #(10) green bottles on the wall"
   "there are 10 green bottles on the wall")

  ("interpolate symbol"
   ((fn (n)
        "there are #(n) green bottles on the wall") 9)
   "there are 9 green bottles on the wall")

  ("interpolate expression"
   ((fn (n)
        "there are #((- 10 n)) green bottles on the wall") 2)
        "there are 8 green bottles on the wall")

  ("don't interpolate when escaped"
   ((fn (n)
        "there are \#(n) green bottles on the wall") 9)
   "there are \#(n) green bottles on the wall")

  ("trailing # is ok"
   ((fn (n)
        "there are some green bottles on the wall #") 9)
   "there are some green bottles on the wall #")))


