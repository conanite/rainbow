;; contributed by fallintothis at http://arclanguage.org/item?id=10503
;; adapted slightly for use as rainbow benchmark test

(= pi             (* 4 (atan 1))
   days-per-year* 365.24
   solar-mass*    (* 4.0 pi pi))

(def make-body (x y z vx vy vz mass)
  (obj x x y y z z vx vx vy vy vz vz mass mass))

(= jupiter* (make-body 4.84143144246472090e0
                       -1.16032004402742839e0
                       -1.03622044471123109e-1
                       (* 1.66007664274403694e-3 days-per-year*)
                       (* 7.69901118419740425e-3 days-per-year*)
                       (* -6.90460016972063023e-5  days-per-year*)
                       (* 9.54791938424326609e-4 solar-mass*)))

(= saturn* (make-body 8.34336671824457987e0
                      4.12479856412430479e0
                      -4.03523417114321381e-1
                      (* -2.76742510726862411e-3 days-per-year*)
                      (* 4.99852801234917238e-3 days-per-year*)
                      (* 2.30417297573763929e-5 days-per-year*)
                      (* 2.85885980666130812e-4 solar-mass*)))

(= uranus* (make-body 1.28943695621391310e1
                      -1.51111514016986312e1
                      -2.23307578892655734e-1
                      (* 2.96460137564761618e-03 days-per-year*)
                      (* 2.37847173959480950e-03 days-per-year*)
                      (* -2.96589568540237556e-05 days-per-year*)
                      (* 4.36624404335156298e-05 solar-mass*)))

(= neptune* (make-body 1.53796971148509165e+01
                       -2.59193146099879641e+01
                       1.79258772950371181e-01
                       (* 2.68067772490389322e-03 days-per-year*)
                       (* 1.62824170038242295e-03 days-per-year*)
                       (* -9.51592254519715870e-05 days-per-year*)
                       (* 5.15138902046611451e-05 solar-mass*)))

(= sun* (make-body 0.0 0.0 0.0 0.0 0.0 0.0 solar-mass*))

(def apply-forces (a b dt)
  (withs (dx    (- (a 'x) (b 'x))
          dy    (- (a 'y) (b 'y))
          dz    (- (a 'z) (b 'z))
          d     (sqrt (+ (expt dx 2) (expt dy 2) (expt dz 2)))
          mag   (/ dt (expt d 3))
          dxmag (* dx mag)
          dymag (* dy mag)
          dzmag (* dz mag))
    (-- (a 'vx) (* dxmag (b 'mass)))
    (-- (a 'vy) (* dymag (b 'mass)))
    (-- (a 'vz) (* dzmag (b 'mass)))
    (++ (b 'vx) (* dxmag (a 'mass)))
    (++ (b 'vy) (* dymag (a 'mass)))
    (++ (b 'vz) (* dzmag (a 'mass)))))

(def advance (solar-system dt)
  (on a solar-system
    (for i (+ index 1) (- (len solar-system) 1)
      (apply-forces a (solar-system i) dt)))
  (each b solar-system
    (++ (b 'x) (* dt (b 'vx)))
    (++ (b 'y) (* dt (b 'vy)))
    (++ (b 'z) (* dt (b 'vz)))))

(def energy (solar-system)
  (let e 0.0
    (on a solar-system
      (++ e (* 0.5
               (a 'mass)
               (+ (expt (a 'vx) 2)
                  (expt (a 'vy) 2)
                  (expt (a 'vz) 2))))
      (for i (+ index 1) (- (len solar-system) 1)
        (withs (b  (solar-system i)
                dx (- (a 'x) (b 'x))
                dy (- (a 'y) (b 'y))
                dz (- (a 'z) (b 'z))
                d  (sqrt (+ (expt dx 2) (expt dy 2) (expt dz 2))))
          (-- e (/ (* (a 'mass) (b 'mass)) d)))))
    e))

(def offset-momentum (solar-system)
  (with (px 0.0e0
         py 0.0e0
         pz 0.0e0)
    (each p solar-system
      (++ px (* (p 'vx) (p 'mass)))
      (++ py (* (p 'vy) (p 'mass)))
      (++ pz (* (p 'vz) (p 'mass))))
    (= ((car solar-system) 'vx) (/ (- px) solar-mass*)
       ((car solar-system) 'vy) (/ (- py) solar-mass*)
       ((car solar-system) 'vz) (/ (- pz) solar-mass*))))

(def nbody (n)
  (let solar-system (list sun* jupiter* saturn* uranus* neptune*)
    (offset-momentum solar-system)
    (let nrg1 (energy solar-system)
      (repeat n (advance solar-system 0.01))
      (list nrg1 (energy solar-system))
    )))
