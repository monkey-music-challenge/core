(ns monkey-music.directions)

(def up ::up)
(def down ::down)
(def left ::left)
(def right ::right)

(def parse [s]
  (case s
    "up" up
    "down" down
    "left" left
    "right" right
    (throw (js/Error. (str "not a direction: " s)))))
