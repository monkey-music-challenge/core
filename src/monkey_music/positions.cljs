(ns monkey-music.positions)

(defn translate [[y x] direction]
  (case direction
    :up    [(dec y) x]
    :down  [(inc y) x]
    :left  [y (dec x)]
    :right [y (inc x)]
    (throw (js/Error. (str "unknown direction: " direction)))))

(defn find-players [layout]
  (let [height (count layout)
        width (count (get layout 0))]
    (for [y (range height)
          x (range width)
          :let [unit (get-in layout [y x])]
          :when (= unit :monkey)]
      [y x])))
