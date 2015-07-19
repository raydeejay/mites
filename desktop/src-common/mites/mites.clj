(ns mites.mites
  (:require [play-clj.g2d :refer :all]
            [mites.utils :refer :all]))

(defn advance [mite]
  (let [[delta-x delta-y] (polar->cartesian [1 (:angle mite)])
        new-x (+ (:x mite) delta-x)
        new-y (+ (:y mite) delta-y)]
    (assoc mite :x new-x :y new-y :steps (dec (:steps mite)))))

(defn turn [mite]
  (assoc mite :angle (inc (rand-int 360))))


(defmulti create-mite (fn [mite-type] mite-type))
(defmulti make-path (fn [mite] (:type mite)))
(defmulti move-mite (fn [mite] (:type mite)))


;; Basic mite
(defmethod create-mite :basic
  [type]
  (assoc (texture "mite.png")
         :x (rand-int 800) :y (rand-int 600) :width 8 :height 8
         :mite true
         :moving false
         :angle 90
         :steps 10
         :type type))

(defmethod make-path :basic
  [mite]
  (assoc (turn mite)
         :steps (inc (rand-int 25))))

(defmethod move-mite :basic
  [mite]
  (advance (if (<= (:steps mite) 0)
             (make-path mite)
             mite)))

;; Hunter mite
(defmethod create-mite :hunter
  [type]
  (assoc (texture "mite.png")
         :x (rand-int 800) :y (rand-int 600) :width 16 :height 16
         :mite true
         :moving false
         :angle 90
         :steps 100
         :type type))

(defmethod make-path :hunter
  [mite]
  (assoc (turn mite)
         :steps (inc (rand-int 25))))

(defmethod move-mite :hunter
  [mite]
  (advance (if (<= (:steps mite) 0)
             (make-path mite)
             mite)))
