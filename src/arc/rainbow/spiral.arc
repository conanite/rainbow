(require-lib "rainbow/img")

(attribute script src opstring)

(mac spiral-params (req . body)
  `(with (x      (num-param ,req "x"    0)
          y      (num-param ,req "y"    0)
          x0     (num-param ,req "x0"   0) 
          y0     (num-param ,req "y0"   0)
          w      (num-param ,req "w"    512)
          h      (num-param ,req "h"    384)
          ox     (num-param ,req "ox"   0)
          oy     (num-param ,req "oy"   0)
          nc     (num-param ,req "nc"   0.01)
          zoom   (num-param ,req "zoom" 2)
          frames (num-param ,req "frames" 10))
     ,@body))

(mac spiral-head ()
  `(tag head
     (tag (script src "/spiral.js"))
     (tag (link rel "stylesheet" type "text/css" href "/spiral.css"))))

(mac spiralpage (left body right)
  `(tag html
     (spiral-head)
     (tag body 
       (tag table
         (tag tr
           (tag (td valign "top") ,left)
           (tag (td valign "top") ,body)
           (tag (td valign "top") ,right))))))

(mac clickable (tagname id text)
  `(tag (,tagname id ,id class "clickable") (pr ,text)))

(mac text-input (name . attrs)
  (withs (input-name (coerce name 'string)
          id (+ input-name "_field"))
    `(empty-elem-tag input type "text" id ,id name ,input-name value ,name ,@attrs)))

(def spiral-form (x y x0 y0 zoom ox oy nc frames)
  (tag (form method "get" action "spiral" id "spiral_form")
         (tag table
              (row (pr "x")
                   (text-input x)
                   (clickable span "left_button" "left")
                   (clickable span "right_button" "right"))
              (row (pr "y")
                   (text-input y)
                   (clickable span "up_button" "up")
                   (clickable span "down_button" "down"))
              (row (pr "x'")
                   (text-input x0)
                   (clickable span "copy_button" "copy")
                   (nbsp))
              (row (pr "y'")
                   (text-input y0)
                   (nbsp)
                   (nbsp))
              (row (pr "x origin")
                   (text-input ox)
                   (clickable span "origin_left_button" "left")
                   (clickable span "origin_right_button" "right"))
              (row (pr "y origin")
                   (text-input oy)
                   (clickable span "origin_up_button" "up") 
                   (clickable span "origin_down_button" "down"))
              (row (pr "neighbours") 
                   (text-input nc)
                   (nbsp)
                   (nbsp))
              (row (pr "zoom")
                   (text-input zoom)
                   (clickable span "zoom_in_button" "zoom in")
                   (clickable span "zoom_out_button" "zoom out"))
              (tag tr
                   (tag td (pr "frames"))
                   (tag td (text-input frames))
                   (tag (td colspan 2) (pr "for animation"))))
         (empty-elem-tag input type "submit" value "plot this")
         (nbsp)
         (empty-elem-tag input type "submit" value "animate (x+iy) -> (x'+iy')" id "animate")))

(def spiral-help ()
  (pr "<p>plot values of iterating <code>z &lt;- z<sup>2</sup> + c</code> where c is <code>x+iy</code></p>"))

(def interleave (sep ls)
  (if ls
      (cons (car ls) 
            (if (cadr ls)
                (cons sep (interleave sep (cdr ls)))))))

(def qsify args
  (let pairify (fn (x)
                   (if (atom x) 
                       `(',x "=" ,x)
                       `(',(car x) "=" ,(cadr x))))
    (if args
        (aif (cdr args)
            `(,@(pairify:car args) "&" ,@(apply qsify it))
            `(,@(pairify:car args))))))

(mac make-qs args
  `(tostring:pr ,@(apply qsify args)))

(defop-raw i (str req)
  (w/stdout str 
    (aif (fns* (sym (arg req "fnid")))
         (it str req)
         (pr dead-msg*))))

(def img-generator (x y w h ox oy zoom)
  (fn (os req)
    (w/stdout os
      (prn (header "image/png" 200))
      (prn "\r"))
    (let p (plot x y w h ox oy zoom)
      ((p 'write) os)
    )))

(def spiral-img (id x y w h ox oy zoom)
  (empty-elem-tag img
    id     id 
    alt    (string (make-complex x y))
    src    (string "/i?fnid=" (fnid:img-generator x y w h ox oy zoom))
    border "0"))

(def spiral-neighbour (req x-offset y-offset)
  (spiral-params req
    (zap [+ _ (* x-offset nc)] x)
    (zap [+ _ (* y-offset nc)] y)
    (tag (a href (string "javascript:$s.moveTo(" x ", " y ");"))
         (spiral-img (string "img_" x-offset "_" y-offset) x y 100 75 ox oy zoom))))

;(set nb-array '(-81 -9 -3 -1 0 1 3 9 81))
(set nb-array '(-64 -8 -1 0 1 8 64))
;(set nb-array '(-10 -1 0 1 10))
;(set nb-array '(-1 0 1))

(def neighbours (req)
  (tag (table cellspacing 1 cellpadding 0)
       (each y (rev nb-array)
          (tag tr
            (each x nb-array
              (tag td (spiral-neighbour req x y)))))))

(def num-param (req name default)
  (coerce (or (arg req name) default) 'int))

(def join-str (token args)
     (apply + (interleave token args)))

(def qs (req) 
  (join-str "&" (map [+ (car _) "=" (cadr _)] (req 'args))))

(def small (z)
  (let (x y) (complex-parts z)
    (and (< x 4) (< y 4))))

(def plot (x y w h ox oy zoom)
  (with (c (make-complex x y)
         p (plotter `(,w ,h) `(,ox ,oy) zoom (awt-color 0 0 0) (awt-color 1 1 1))
         z 0+0i 
         n 0
         repeats 0)
    (while (and (small z) (< n 10000) (< repeats 1000))
      (++ n)
      (set z (+ c (* z z)))
      (if (apply p!plot (complex-parts z)) (++ repeats) 
                                           (= repeats 0)))
    p))

(def draw-spiral (req)
  (spiral-params req
    (plot x y w h ox oy zoom)))

(defop-raw img (os req)
  (w/stdout os
    (prn (header "image/png" 200))
    (prn "\r"))
  (let p (draw-spiral req)
    (p!write (outfile "foo.png"))
    (p!write os)))

(defop spiral req 
  (spiral-params req
    (spiralpage (do (spiral-form x y x0 y0 zoom ox oy nc frames) 
                    (spiral-help)
                    (empty-elem-tag img 
                      id     "main_img"
                      src    (string "/i?fnid=" (fnid:img-generator x y w h ox oy zoom)))
                    (tag script (pr "$s.install();$s.recentering();"))
                    (tag (div id "img_hover") (nbsp)))
                (neighbours req)
                nil)))

(def animation (x y x0 y0 zoom ox oy nc frames)
  (with (stepx (/ (- x0 x) frames)
         stepy (/ (- y0 y) frames))
    (for step 0 frames
      (spiral-img (string "anim_" step) x y 512 384 ox oy zoom)
      (zap [+ _ stepx] x)
      (zap [+ _ stepy] y))))

(def animation-info ()
  (tag (span id "animinfo") (nbsp)))

(defop animate req
  (spiral-params req
    (spiralpage (do (spiral-form x y x0 y0 zoom ox oy nc frames)
                    (animation-info))
                (animation x y x0 y0 zoom ox oy nc frames)
                (tag script (pr "$s.install();$s.animate();")))))

(def start-spiral-app () 
  (set threadlimit* 500 threadlife* 60)
  (set asv-thread (thread (asv))))

; http://localhost:8080/spiral?x=-0.465&y=-0.543&ox=-1.04&oy=-0.74&nc=0.0008&zoom=1.2
; http://localhost:8080/spiral?x=-0.154&y=-0.644&ox=-0.733984375&oy=-0.759375&zoom=1&nc=0.006
; http://localhost:8080/spiral?x=-0.667957&y=-0.32&x0=-0.667978&y0=-0.32&ox=-0.90390625&oy=-0.5171875&nc=0.005&zoom=1&frames=10
; http://localhost:8080/animate?x=-0.662957&y=-0.32&x0=-0.662957&y0=-0.35&ox=-0.90390625&oy=-0.5171875&nc=0.005&zoom=1&frames=200
; http://localhost:8080/animate?x=0.254&y=0.003&x0=0.264&y0=0.003&ox=0.2&oy=-0.03&nc=0.003&zoom=0.589824&frames=200
; http://localhost:8080/spiral?x=0.25048&y=0&x0=0.263&y0=0.003&ox=0.20576&oy=-0.218928&nc=0.00001&zoom=0.589824&frames=200
; http://localhost:8080/animate?x=0.25048&y=-0.00081&x0=0.25048&y0=0.00081&ox=0.20576&oy=-0.218928&nc=0.00001&zoom=0.589824&frames=200



