(ns mites.core.desktop-launcher
  (:require [mites.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. mites-game "mites" 800 600)
  (Keyboard/enableRepeatEvents true))
