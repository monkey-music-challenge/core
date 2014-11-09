(ns monkey-music.sandbox
  (:require [monkey-music.units :as units]))

(defmulti y identity)

(defmethod y units/item [u]
  (println "yo" u))

(units/parse "song")

(y units/song)
