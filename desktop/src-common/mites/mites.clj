(ns mites.mites
  (:require [play-clj.g2d :refer :all]
            [mites.utils :refer :all]))

;; Basic mite
(defn create-mite []
  (assoc (texture "mite.png")
         :x (rand-int 800) :y (rand-int 600) :width 8 :height 8
         :mite true
         :moving false))

(defn make-path [mite]
  ;; (debug "CREATING PATH")
  (let [d (rand-nth [:up :down :left :right])
        n (inc (rand-int 25))]
    (assoc mite :path (repeat n d))))

(defn move-mite [mite]
  ;; (debug "STEPPING MITE" mite)
  (let [mite (if (empty? (:path mite))
               (make-path mite)
               mite)
        path (:path mite)]
    ;; (debug "THE PATH IS" path)
    (assoc (move mite (first path))
           :path (rest path))))
