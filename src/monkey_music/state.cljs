(ns monkey-music.state
  (:require [monkey-music.core :as core]))

(defn parse-layout [layout unit-lookup]
  (->> layout
       (mapv vec)
       (mapv (partial mapv (comp keyword
                                 unit-lookup
                                 keyword)))))

(defn find-player-positions [layout]
  (let [height (count layout)
        width (count (get layout 0))]
    (for [y (range height)
          x (range width)
          :let [unit (get-in layout [y x])]
          :when (= unit :monkey)]
      [y x])))

(defn create
  [players
   {turns :turns
    layout-strings :layout
    unit-lookup :units}]
  (let [layout (parse-layout layout-strings unit-lookup)
        player-positions (find-player-positions layout)
        player-data (mapv (partial core/create-player turns) player-positions)
        players (into {} (map vector players player-data))]
    {:turns turns
     :layout layout
     :players players}))

(defn state-for-player [state player]
  {:layout (:layout state)
   :turns (:turns state)
   :position (core/player-position state player)
   :score (core/get-score state player)
   :pickedUp (core/picked-up-items state player)})
