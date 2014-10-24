(ns monkey-music.state
  (:require [monkey-music.core :as core]))

(defn new-player [turns position]
  {:score 0
   :picked-up []
   :turns turns
   :position position})

(defn new-game-state
  [players {turns "turns"
            layout-strings "layout"
            unit-lookup "units"}]
  (let [layout (->> layout-strings
                    (mapv vec)
                    (mapv (partial mapv (comp keyword unit-lookup))))
        layout-height (count layout)
        layout-width (count (get layout 0))
        player-positions (for [y (range layout-height)
                               x (range layout-width)
                               :let [unit (get-in layout [y x])]
                               :when (= unit :monkey)]
                           [y x])
        player-data (mapv (partial assoc {:score 0 :picked-up []} :position) player-positions)
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
