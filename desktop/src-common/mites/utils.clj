(ns mites.utils
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(defn move
  [entity direction]
  (case direction
    :down (assoc entity :y (dec (:y entity)))
    :up (assoc entity :y (inc (:y entity)))
    :right (assoc entity :x (inc (:x entity)))
    :left (assoc entity :x (dec (:x entity)))
    nil))

(defn clicked?
  [ent screen]
  ;; FIXME: sorry, I don't have time to make it pretty :)
  (let [[x_ y_] [(game :x) (game :y)]
        m (input->screen screen x_ y_)
        [x y] [(:x m) (:y m)]]
    (and
     (< (:x ent) x (+ (:x ent) (:width ent)))
     (< (:y ent) (- 480 y) (+ (:y ent) (:height ent))))))

(def debug? true) ;; umm................
(defn debug
  "I print debugging information if I should."
  [& r]
  (when debug? (apply println r)))

(defn named
  [n]
  #(when (= n (:name %)) %))

(defn hide
  [ent]
  (assoc ent :hidden true))

(defn show
  [ent]
  (dissoc ent :hidden))

(defn polar->cartesian [[distance angle]]
  [(* distance (Math/cos angle)) (* distance (Math/sin angle))])

(defn cartesian->polar [[x y]]
  [(Math/sqrt (+ (* x x) (* y y)))
   (Math/atan2 y x)])
