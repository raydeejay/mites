(ns mites.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]
            [mites.utils :refer :all]
            [mites.mites :refer :all]))


(declare mites-game main-screen)
(defonce manager (asset-manager))

(defn step-mites
  [screen mites]
  (mapv (fn [mite]
          (cond (some #(= (:uuid mite) (:uuid %)) (:dead-mites screen))
                nil
                (:moving mite)
                (move-mite mite screen mites)
                :else
                mite))
        mites))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen
             :renderer (stage)
             :camera (orthographic)
             :dead-mites '())
    [(label "Mites!!!" (color :white))
     (assoc (create-mite :basic) :x 400 :y 300)])

  :on-render
  (fn [screen entities]
    (clear!)
    (let [mites (filter :mite entities)
          non-mites (remove :mite entities)]
      (update! screen :dead-mites '())
      (render! screen (concat (step-mites screen mites)
                              non-mites))))

  :on-resize
  (fn [screen entities]
    (height! screen 600))

  :on-key-down
  (fn [screen entities]
    (cond (= (:key screen) (key-code :n))
          (do (set-screen! mites-game main-screen)
              nil)
          (= (:key screen) (key-code :s))
          (map #(if (:mite %)
                  (assoc % :moving (not (:moving %)))
                  %)
               entities)
          (= (:key screen) (key-code :c))
          (do (debug (count entities))
              nil)
          (= (:key screen) (key-code :a))
          (conj entities (create-mite :basic))
          (= (:key screen) (key-code :h))
          (conj entities (create-mite :hunter))
          (= (:key screen) (key-code :q))
          (do (app! :exit)
              nil)
          :else
          nil)))

;; disaster prevention
(defscreen blank-screen
  :on-render
  (fn [screen entities]
    (clear!))

  :on-key-down
  (fn [screen entities]
    (when (= (:key screen) (key-code :n))
      (set-screen! mites-game main-screen))
    (when (= (:key screen) (key-code :q))
      (app! :exit))
    nil))


;; entry point for the game
(defgame mites-game
  :on-create
  (fn [this]
    (set-screen-wrapper! (fn [screen screen-fn]
                           (try (screen-fn)
                                (catch Exception e
                                  (.printStackTrace e)
                                  (set-screen! mites-game blank-screen)))))
    (set-asset-manager! manager)
    (set-screen! this main-screen)))
