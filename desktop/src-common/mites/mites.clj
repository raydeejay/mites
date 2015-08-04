(ns mites.mites
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.math :refer :all]
            [mites.utils :refer :all]))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn bounce
  [mite]
  (let [x (:x mite) y (:y mite) w (:width mite) h (:height mite) angle (:angle mite)]
    (if (or (< x 0)
            (> (+ x w) 800)
            (< y 0)
            (> (+ y h) 600))
      (assoc mite :angle (mod (+ 180 angle) 360))
      mite)))

(defn collide?
  [rect1 rect2]
  ;; (println "Colliding" rect1 "and" rect2)
  (and (< (:x rect1)
          (+ (:x rect2) (:width rect2)))
       (> (+ (:x rect1) (:width rect1))
          (:x rect2))
       (< (:y rect1)
          (+ (:y rect2) (:height rect2)))
       (> (+ (:height rect1) (:y rect1))
          (:y rect2))
        ;; collision detected! or is it...
       (not (= (:uuid rect1) (:uuid rect2)))))

(defmulti create-mite (fn [mite-type] mite-type))
(defmulti make-path (fn [mite screen entities] (:type mite)))
(defmulti move-mite (fn [mite screen entities] (:type mite)))


(defn advance
  [mite screen entities]
  (let [[delta-x delta-y] (polar->cartesian [1 (:angle mite)])
        new-x (+ (:x mite) delta-x)
        new-y (+ (:y mite) delta-y)]
    (let [target (bounce (assoc mite
                                :x new-x
                                :y new-y
                                :steps (dec (:steps mite))))]
      (let [other (some #(if (collide? target %) % nil) entities)]
        (if other
          (do (update! screen :dead-mites (conj (:dead-mites screen) other))
              (assoc target
                     :width 16
                     :height 16))
          target)))))

(defn turn
  [mite]
  (assoc mite :angle (inc (rand-int 360))))


;; Basic mite
(defmethod create-mite :basic
  [type]
  (assoc (texture "mite.png")
         :x (rand-int 800) :y (rand-int 600) :width 8 :height 8
         :mite true
         :moving false
         :angle 90
         :steps 10
         :type type
         :speed 1
         :uuid (uuid)))

(defmethod make-path :basic
  [mite screen entities]
  (assoc (turn mite)
         :steps (inc (rand-int 25))))

(defmethod move-mite :basic
  [mite screen entities]
  (advance (if (<= (:steps mite) 0)
             (make-path mite screen entities)
             mite)
           screen
           entities))

;; Hunter mite
(defmethod create-mite :hunter
  [type]
  (assoc (create-mite :basic)
         :width 16 :height 16
         :steps 100
         :type type))

;; this should scan around the mite and possibly change it's speed
;; search for other mites along the line of vision
;; if their coordinates intersect the line of vision, attack
;; I have a rectangle
;; can I have a line, and check if they intersect?

;; for each entity
;;   given the square formed by the entity as A
;;     if the line intersects A
;;       bail out
;;       otherwise check next

;; given a point X,Y
;;       an angle called A
;;       a distance of vision called D
;;       a second point X2,Y2
;;       a width and height W2, H2
;; there is a rectangle (X2, Y2 - H2, X2 + W2, Y2) called R
;;          a vector [XC YC] that has cartesian coordinates
;;                           which are derived from D and A
;; there is a line (X, Y, XC, YC)
;; assuming normalised coordinates:
;; for the line to intersect the rectangle:
;;   X < < XC

(defn scan [mite screen entities]
  (let [min-x (min (mapv :x (filter :mite entities)))
        max-x (max (mapv :x (filter :mite entities)))
        min-y (min (mapv :y (filter :mite entities)))
        max-y (max (mapv :y (filter :mite entities)))]
    (assoc mite :speed 5)))

(defmethod make-path :hunter
  [mite screen entities]
  (assoc (turn mite)
         :steps (inc (rand-int (* 4 25)))))

(defmethod move-mite :hunter
  [mite screen entities]
  (advance (if (<= (:steps mite) 0)
             (make-path mite screen entities)
             mite)
           screen
           entities))
