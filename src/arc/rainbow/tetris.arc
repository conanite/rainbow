(require-lib "rainbow/swing")

(mac set-cell (colour row col)
  (w/uniq (gk)
    `(let ,gk (key ,row ,col)
      (= (universe ,gk) ,colour)
      (= (cell-updates* ,gk) (or ,colour 'none)))))

(mac shape-cells (colour-var rotation row-var col-var . body)
  `(for ,row-var 0 3
    (for ,col-var 0 3
      (when (shape-info (shape-cell-key ,colour-var ,rotation ,row-var ,col-var))
        ,@body))))

(mac tetrisop (name args . body)
  (w/uniq (gv)
    `(atdef ,name ,args
      (if paused*
          t
          (let ,gv nil
            (assign cell-updates* (table))
            (draw-shape nil)
            (assign ,gv (do ,@body ))
            (draw-shape)
            (update-updates cell-updates*)
            ,gv)))))

(assign tetris-width*   8
        tetris-height*  20
        acceleration*   1.005
        colours         nil
        shape-info      (table)
        universe-cells* (table)
        shape-rotations (table)
        game-thread*    nil)

(def reset-game ()
  (assign delay* 0.5 score* 0 paused* nil)
  (assign universe (table) cell-updates* (table))
  (new-shape)
  (each (k v) universe-cells*
    (repaint-cell k nil))
  (update-score 0))

(def key (row col)
  (string row "-" col))

(def shape-cell-key (colour rotation row col)
  (string colour "-" rotation "-" row "-" col))

(def shape (colour cells)
  (push colour colours)
  (afnwith (info cells)
    (if info
      (let (rotation row col) (car info)
        (= shape-rotations.colour rotation)
        (set (shape-info (shape-cell-key colour rotation row col)))
        (self (cdr info))))))

(shape 'red    '((0 0 1) (0 1 1) (0 2 1) (0 3 1)
                 (1 1 0) (1 1 1) (1 1 2) (1 1 3)))
(shape 'gray   '((0 0 1) (0 1 0) (0 1 1) (0 1 2)
                 (1 0 1) (1 1 1) (1 1 2) (1 2 1)
                 (2 1 0) (2 1 1) (2 1 2) (2 2 1)
                 (3 0 1) (3 1 0) (3 1 1) (3 2 1)))
(shape 'pink   '((0 0 0) (0 1 0) (0 1 1) (0 1 2)
                 (1 0 1) (1 0 2) (1 1 1) (1 2 1)
                 (2 1 0) (2 1 1) (2 1 2) (2 2 2)
                 (3 0 1) (3 1 1) (3 2 0) (3 2 1)))
(shape 'blue   '((0 0 2) (0 1 0) (0 1 1) (0 1 2)
                 (1 0 0) (1 0 1) (1 1 1) (1 2 1)
                 (2 1 0) (2 1 1) (2 1 2) (2 2 0)
                 (3 0 1) (3 1 1) (3 2 1) (3 2 2)))
(shape 'yellow '((0 0 0) (0 0 1) (0 1 0) (0 1 1)))
(shape 'white  '((0 1 1) (0 1 2) (0 2 0) (0 2 1)
                 (1 0 1) (1 1 1) (1 1 2) (1 2 2)))
(shape 'orange '((0 1 0) (0 1 1) (0 2 1) (0 2 2)
                 (1 0 2) (1 1 1) (1 1 2) (1 2 1)))

(def tetris-view (frame h w universe-cells)
  (let jp (panel)
    (jp 'setLayout (java-new "java.awt.GridLayout" tetris-height* tetris-width*))
    (for row 0 (- h 1)
      (for col 0 (- w 1)
        (let cell (panel)
          (pr ".")
          (cell 'setBorder (java-new "javax.swing.border.LineBorder" (awt-color 0.1 0.1 0.1) 1))
          (= (universe-cells (key row col))
             (fn (colour)
                 (cell 'setBackground (awt-color colour))))
          (jp 'add cell))))
    (prn)
    (frame 'setContentPane jp)))

(def tetris-setup (universe-cells)
  (let f (frame 300 150 300 700 "Arc Tetris")
    (tetris-view f tetris-height* tetris-width* universe-cells)
    (on-key-press f
      'up    (rotate-shape)
      'down  (drop-shape)
      'left  (move-shape -1)
      'right (move-shape 1)
      'n     (new-game)
      'p     (zap [no _] paused*)
      'q     (end-tetris f))
    f!pack
    (f 'setSize 300 700)
    f!show
    (def update-score (score)
      (f 'setTitle (+ "Arc Tetris : " (coerce score 'string))))))

(def end-tetris (frame)
  frame!dispose
  (if game-thread* (kill-thread game-thread*)))

(def game-loop ()
  (sleep delay*)
  (if (advance-shape)
      (game-loop)))

(atdef full ()
  (catch
    (for x 0 (- tetris-width* 1)
      (if (universe (key 0 x))
        (throw t)))))

(def end-round ()
  (draw-shape)
  (collapse-full-rows)
  (zap [/ _ acceleration*] delay*)
  (unless (full)
    (new-shape)))

(def bottom ()
  (illegal falling!colour falling!rotation falling!x (+ 1 falling!y)))

(tetrisop advance-shape ()
  (if (bottom)
      (end-round)
      (++ falling!y)))

(tetrisop drop-shape ()
  ((afn ()
    (if (no (bottom))
      (do (++ falling!y) (self))))))

(tetrisop move-shape (direction)
  (if (no (illegal falling!colour falling!rotation (+ direction falling!x) falling!y))
    (= falling!x (+ direction falling!x))))

(tetrisop rotate-shape ()
  (let r (+ 1 falling!rotation)
    (if (> r (shape-rotations falling!colour))
        (= r 0))
    (if (no (illegal falling!colour r falling!x falling!y))
        (= falling!rotation r))))

(def collapse-full-rows ()
  (with (score 1 filled 0)
    (for row 0 (- tetris-height* 1)
      (= filled 0)
      (for col 0 (- tetris-width* 1)
        (if (universe (key row col))
            (++ filled)))
      (if (is filled tetris-width*)
        (do (collapse row)
            (= score (* score 2)))))
    (update-score (zap [+ _ score] score*))
    (= last-score* score)))

(def collapse (row)
  (loop nil (> row 0) (-- row)
    (for col 0 (- tetris-width* 1)
      (set-cell (universe (key (- row 1) col)) row col))))

(def illegal (c rot x y)
  (let prohibited nil
    (shape-cells c rot row column
      (if (or (> (+ x column 1) tetris-width*)
              (> (+ y row 1) tetris-height*)
              (< (+ x column) 0)
              (universe (key (+ y row) (+ x column))))
          (set prohibited)))
     prohibited))

(def new-shape ()
  (= falling (obj colour   (rand-elt colours)
                  x        (- (/ tetris-width* 2) 2)
                  y        0
                  rotation 0)))

(atdef draw-shape ((o colour falling!colour))
  (shape-cells falling!colour falling!rotation row col
    (set-cell colour (+ row falling!y) (+ col falling!x))))

(def update-updates (cells-to-update)
  (each (k v) cells-to-update
    (repaint-cell k (if (is v 'none) nil v))))

(def repaint-cell (k colour)
  (universe-cells*.k (or colour 'black)))

(def game-help ()
  (prn "pause      : p")
  (prn "rotate     : up")
  (prn "move left  : left")
  (prn "move right : right")
  (prn "drop       : down")
  (prn "new game   : n"))

(def tetris ()
  (tetris-setup universe-cells*)
  (game-help)
  (new-game))

(def new-game ()
  (if game-thread* (kill-thread game-thread*))
  (reset-game)
  (assign game-thread* (thread (game-loop)))
  "started")
