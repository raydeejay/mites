(ns mites.mites
  (:require [play-clj.g2d :refer :all]
            [mites.utils :refer :all]))

(defn advance [mite]
  (let [[delta-x delta-y] (polar->cartesian [1 (:angle mite)])
        new-x (+ (:x mite) delta-x)
        new-y (+ (:y mite) delta-y)]
    (assoc mite :x new-x :y new-y :steps (dec (:steps mite)))))

(defn turn [mite]
  (assoc mite :angle (inc (rand-int 180))))


;; Basic mite
(defn create-mite []
  (assoc (texture "mite.png")
         :x (rand-int 800) :y (rand-int 600) :width 8 :height 8
         :mite true
         :moving false
         :angle 90
         :steps 10))

(defn make-path [mite]
  (assoc (turn mite)
         :steps (inc (rand-int 25))))

(defn move-mite [mite]
  (advance (if (< (:steps mite) 0)
             (make-path mite)
             mite)))
