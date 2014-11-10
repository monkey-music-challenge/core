(ns monkey-music.positions
  (:require [monkey-music.units :as units]
            [monkey-music.directions :as directions]))

(defn translate [[y x] direction]
  (condp = direction
    directions/up [(dec y) x]
    directions/down [(inc y) x]
    directions/left [y (dec x)]
    directions/right [y (inc x)]
    (throw (js/Error. (str "not a direction: " direction)))))

(defn find-monkeys [layout]
  (let [height (count layout)
        width (count (get layout 0))]
    (for [y (range height)
          x (range width)
          :let [unit (get-in layout [y x])]
          :when (units/is? units/monkey unit)]
      [y x])))
