(require-lib "rainbow/img")

(attribute script src  opstring)
(attribute script type opstring)
(attribute div    id   opstring)
(attribute input  id   opstring)
(attribute form   id   opstring)
(attribute link   rel  opstring)
(attribute link   href opstring)
(attribute link   type opstring)
(attribute img    id   opstring)
(attribute img    alt  opstring)

(mac spiral-params (req . body)
  `(with (x      (num-param ,req "x"      0)
          y      (num-param ,req "y"      0)
          x0     (num-param ,req "x0"     0)
          y0     (num-param ,req "y0"     0)
          w      (num-param ,req "w"      512)
          h      (num-param ,req "h"      384)
          ox     (num-param ,req "ox"     0)
          oy     (num-param ,req "oy"     0)
          nc     (num-param ,req "nc"     0.01)
          zoom   (num-param ,req "zoom"   2)
          zoom0  (num-param ,req "zoom0"  2)
          frames (num-param ,req "frames" 10 120))
     ,@body))

(mac spiral-head ()
  `(tag head
     (tag (script src "/spiral.js" type "text/javascript"))
     (tag (link rel "stylesheet" type "text/css" href "/spiral.css"))))

(mac spiralpage (left body right)
  `(tag html
     (spiral-head)
     (tag body
       (tag b (tag (a href "/spiral-intro.html") (pr "home")))
       (br)
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
    `(tag (input type "text" id ,id name ,input-name value ,name ,@attrs))))

(def spiral-form (x y x0 y0 zoom zoom0 ox oy nc frames)
  (tag (form method "get" action "spiral" id "spiral_form")
         (tag table
              (row (pr)
                   (pr)
                   (pr)
                   (pr)
                   (pr "animate to"))
              (row (pr "x")
                   (text-input x)
                   (clickable span "left_button" "left")
                   (clickable span "right_button" "right")
                   (text-input x0)
                   (clickable span "copy_x_button" "copy"))
              (row (pr "y")
                   (text-input y)
                   (clickable span "up_button" "up")
                   (clickable span "down_button" "down")
                   (text-input y0)
                   (clickable span "copy_y_button" "copy"))
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
                   (clickable span "zoom_out_button" "zoom out")
                   (text-input zoom0)
                   (clickable span "copy_zoom_button" "copy"))
              (row (nbsp)
                   (nbsp)
                   (nbsp)
                   (pr "frames")
                   (text-input frames))
              (row (nbsp)
                   (tag (input type "submit" value "plot this"))
                   (nbsp)
                   (nbsp)
                   (tag (input type "submit" value "animate" id "animate"))))))

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

(def spiral-img (id x y w h ox oy zoom (o display "''"))
  (tag (img
    id     id
    style  (string "display:" display ";")
    alt    (string (make-complex x y))
    src    (string "/i?fnid=" (fnid:img-generator x y w h ox oy zoom))
    border "0")))

(def spiral-neighbour-x-label (req x-offset)
  (spiral-params req
    (+ x (* x-offset nc))))

(def spiral-neighbour-y-label (req y-offset)
  (spiral-params req
    (+ y (* y-offset nc))))

(def spiral-neighbour (req x-offset y-offset)
  (spiral-params req
    (zap [+ _ (* x-offset nc)] x)
    (zap [+ _ (* y-offset nc)] y)
    (tag (a href (string "javascript:$s.moveTo(" x ", " y ");"))
         (spiral-img (string "img_" x-offset "_" y-offset) x y 100 75 ox oy zoom))))

;(assign nb-array '(-81 -9 -3 -1 0 1 3 9 81))
(assign nb-array '(-64 -8 -1 0 1 8 64))
;(assign nb-array '(-10 -1 0 1 10))
;(assign nb-array '(-1 0 1))

(def neighbours (req)
  (tag (table cellspacing 1 cellpadding 0)
       (each y (rev nb-array)
          (tag tr
            (each x nb-array
              (tag td (spiral-neighbour req x y)))
            (tag td (pr:spiral-neighbour-y-label req y))))
       (tag tr
         (each x nb-array
           (tag td (pr:spiral-neighbour-x-label req x))))))

(def num-param (req name default (o max-val))
  (alet (coerce (or (arg req name) default) 'num)
    (if max-val (min it max-val) it)))

(def join-str (token args)
  (apply + (interleave token args)))

(def qs (req)
  (join-str "&" (map [+ (car _) "=" (cadr _)] (req 'args))))

(def small (z)
  (let (x y) (complex-parts z)
    (and (< x 4) (< y 4))))

(def img-generator (x y w h ox oy zoom)
  (fn (os req)
    (prn (type-header* 'png))
    (prn "\r")
    (let (plt write-img) (plotter `(,w ,h) `(,ox ,oy) zoom (awt-color 0 0 0) (awt-color 255 255 255))
      (plot plt (make-complex x y))
      (write-img os))))

(def plot (plt c)
  (with (z 0+0i
         n 0
         repeats 0)
    (while (and (small z) (< n 10000) (< repeats 1000))
      (assign n       (+ n 1)
              z       (+ c (* z z))
              repeats (if (apply plt (complex-parts z))
                          (+ repeats 1)
                          0)))))

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
    (spiralpage (do (spiral-form x y x0 y0 zoom zoom0 ox oy nc frames)
                    (spiral-help)
                    (tag (img
                      id     "main_img"
                      src    (string "/i?fnid=" (fnid:img-generator x y w h ox oy zoom))))
                    (tag (script type "text/javascript") (pr "$s.install();$s.recentering();"))
                    (tag (div id "img_hover") (nbsp)))
                (neighbours req)
                nil)))

(def animation (x y x0 y0 zoom zoom0 ox oy nc frames)
  (with (stepx (/ (- x0 x) frames)
         stepy (/ (- y0 y) frames)
         stepz (expt (/ zoom0 zoom) (/ 1 (- frames 1))))
    (for step 1 frames
      (spiral-img (string "anim_" step) x y 512 384 ox oy zoom "none")
      (zap [* _ stepz] zoom)
      (zap [+ _ stepx] x)
      (zap [+ _ stepy] y))))

(def animation-info ()
  (tag (span id "animinfo") (nbsp))
  (nbsp)
  (tag (input id "fps" value "12" style "width:3em;font-size:66%;"))
  (pr "fps")
  (br))

(defop animate req
  (spiral-params req
    (spiralpage (spiral-form x y x0 y0 zoom zoom0 ox oy nc frames)
                (do (animation-info)
                    (br)
                    (animation x y x0 y0 zoom zoom0 ox oy nc frames))
                (tag (script type "text/javascript") (pr "$s.install();$s.animate();")))))

(def start-spiral-app ()
  (assign req-limit* 300)
  (assign threadlimit* 500 threadlife* 60)
  (assign asv-thread (thread (asv 8085))))

(map (fn ((k v)) (= (type-header* k) (gen-type-header v)))
     '((javascript "text/javascript")
       (css        "text/css")))

