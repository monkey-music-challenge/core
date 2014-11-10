(ns monkey-music.units
  (:refer-clojure :exclude [empty]))

(def empty ::empty)
(def monkey ::monkey)
(def song ::song)
(def album ::album)
(def playlist ::playlist)
(def user ::user)
(def banana ::banana)
(def out-of-bounds ::out-of-bounds)

(def item ::item)
(derive ::song ::item)
(derive ::album ::item)
(derive ::playlist ::item)
(derive ::banana ::item)

(def is? keyword-identical?)

(defn parse [s]
  (case s
    "empty" empty
    "monkey" monkey
    "song" song
    "album" album
    "playlist" playlist
    "user" user
    "banana" banana
    (throw (js/Error. (str "not a unit: " s)))))

(def stringify name)
