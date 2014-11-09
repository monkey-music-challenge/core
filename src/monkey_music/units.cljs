(ns monkey-music.units
  (:refer-clojure :exclude [empty]))

(def empty ::empty)
(def monkey ::monkey)
(def song ::song)
(def album ::album)
(def playlist ::playlist)
(def user ::user)

(def item ::item)
(derive ::song ::item)
(derive ::album ::item)
(derive ::playlist ::item)

(def is? keyword-identical?)

(defn parse [s]
  (case s
    "empty" empty
    "monkey" monkey
    "song" song
    "album" album
    "playlist" playist
    "user" user
    (throw (js/Error. (str "not a unit: " s)))))

(defn stringify [units]
  (if (seq units) (map name units) (name units)))
