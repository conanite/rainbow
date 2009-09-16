(require-lib "rainbow/swing")

(mac move-cursor (place op lower upper)
  (w/uniq gv
   `(let ,gv (,op ,place 1)
      (= ,place (if (< ,gv ,lower) ,lower
                    (> ,gv ,upper) ,upper
                                   ,gv)))))

(def neighbours ((x y))
  (accum ns
    (for x1 -1 1
      (for y1 -1 1
        (if (no (is x1 y1 0))
            (ns `(,(+ x x1) ,(+ y y1))))))))

(def new-mine-field ()
  (with (mf       (table) cleared  (table) marked   (table)
         started  nil     stopped  nil
         cursor-x 5       cursor-y 5
         max-x    9       max-y    9
         this     nil
         dead     nil)
    (= this (make-obj
      (oob       ((x y))
        (or (< x 0) (> x max-x)
            (< y 0) (> y max-y)))
      (propagate (from seen)
        (unless (or (oob from)
                    (seen from))
          (set (seen from))
          (when (no (mf from))
            (set (cleared from))
            (if (is (surrounded from) 0)
              (each n (neighbours from)
                (propagate n seen))))))
      (reset      ()    (= mf      (table)
                           cleared (table)
                           marked  (table)
                           started nil
                           stopped nil)
                        (while (< (len mf) 10)
                          (set (mf `(,(rand (+ max-x))
                                        ,(rand (+ max-y)))))))
      (bomb?      (p)   (mf p))
      (marked?    (p)   (marked p))
      (ended      ()    (if stopped stopped (msec)))
      (secs       ()    (if started
                            (trunc (/ (- (ended) started) 1000.0))
                            0))
      (marks      ()    (len marked))
      (surrounded (p)   (afnwith (ns (neighbours p))
                           (if (no ns)       0
                               (mf (car ns)) (+ 1 (self (cdr ns)))
                                             (self (cdr ns)))))
      (here       ()    `(,cursor-x ,cursor-y))
      (dimension  ()    `(,(+ max-x 1) ,(+ max-y 1)))
      (clear?     (p)   (cleared p))
      (done?      ()    (or (won?) (lost?)))
      (won?       ()    (is (+ (len cleared) (len mf))
                            (apply * (dimension))))
      (lost?      ()    (catch
                          (each (p bomb) mf
                            (when (and bomb (cleared p))
                              (throw t)))
                          nil))
      (up         ()    (move-cursor cursor-y - 0 max-y))
      (down       ()    (move-cursor cursor-y + 0 max-y))
      (left       ()    (move-cursor cursor-x - 0 max-x))
      (right      ()    (move-cursor cursor-x + 0 max-x))
      (mark       ()    (unless (clear? (here))
                          (= (marked (here))
                             (no (marked (here))))))
      (clear1     (p)   (if (mf p)     (set (cleared p))
                            (clear? p) (each n (neighbours p)
                                         (unless (or (clear? n)
                                                     (marked? n))
                                           (clear1 n)))
                                       (propagate p (table))))
      (clear      ()    (clear1 (here))
                        (if (no started)
                            (= started (msec))))
      (invoke     (op)  (when (or (no (done?)) (is op 'reset))
                          ((this op))
                          (if (done?) (= stopped (msec)))
                          (redraw this)))))))

(def redraw (mf)
  (each (k v) mf!cells
    (v (if (mf!clear? k)  (if (mf!bomb? k)
                              'exploded
                              'exposed)
           (mf!marked? k) 'marked
                          'hidden)
       (iso k (mf!here))
       (mf!surrounded k))))

(def update-info (mf info)
  (info 'setText
        (string (if (mf!won?)  "finished, congrats :)"
                    (mf!lost?) "oops ..."
                               (mf!marks))
                " - #((mf!secs)) seconds"))
  (sleep 0.25)
  (unless mf!dead
    (update-info mf info)))

(def mines-setup (mf)
  (let f (frame 400 400 400 400 "arc mine-field")
    (let info (mf-view f mf)
      (thread (update-info mf info)))
    (on-key-press f
      'q        (do f!dispose (set mf!dead))
      'n        (mf!invoke 'reset)
      'b        (mf!invoke 'mark)
      'space    (mf!invoke 'clear)
      'up       (mf!invoke 'up)
      'down     (mf!invoke 'down)
      'left     (mf!invoke 'left)
      'right    (mf!invoke 'right))
    f!pack
    (f 'setSize 400 400)
    f!show
    (mf!invoke 'reset)))

(java-import "javax.swing.border.LineBorder")

(assign mine-colours '((1.0 1.0 1.0)
                    (0.5 0.4 0.4)
                    (0.5 0.5 0.1)
                    (0.1 0.1 0.5)
                    (0.5 0.3 0.1)
                    (0.1 0.5 0.1)
                    (0.5 0.1 0.1)
                    (0.5 0.1 0.5)
                    (0.2 0.2 0.2)))

(def mf-view (f mf)
  (with (info  (jlabel)
         jp    (panel)
         (w h) (mf!dimension)
         cells (table))
    (= mf!cells cells)
    (jp 'setLayout (java-new "java.awt.GridLayout" h w))
    (for row 0 (- h 1)
      (for col 0 (- w 1)
        (let cell (jlabel)
          (= (cells `(,col ,row))
             (fn (status focus count)
                 (with (fg   (mine-colours (or count 0))
                        text (if (and count (> count 0) (is status 'exposed))
                                 "<html><b>#(count)</b></html>"
                                 ""))
                   (configure-bean cell
                     'opaque t
                     'horizontalAlignment (SwingConstants CENTER)
                     'border (LineBorder new (if focus
                                                 (awt-color 'yellow)
                                                 (awt-color 0.1 0.1 0.1)) 1)
                     'background (awt-color (case status
                                                  exposed  'white
                                                  marked   'blue
                                                  exploded 'red
                                                  hidden   'gray))
                     'foreground (apply awt-color fg)
                     'text text))))
          (jp 'add cell))))
    (f!getContentPane 'add info)
    (f!getContentPane 'add jp)
    info))

(def mines ()
  (mines-setup (new-mine-field))
  nil)

