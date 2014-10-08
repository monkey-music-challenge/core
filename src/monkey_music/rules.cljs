(ns monkey-music.rules
  (:require [monkey-music.core :as core]))

(defn move [state player direction]
  (let [player-position (core/player-position state player)
        target-position (core/translate player-position direction)
        unit-at-target-position (core/unit-at state target-position)
        picked-up-items (core/picked-up-items state player)
        value-of-picked-up-items (reduce + (map core/value-of picked-up-items))
        player-score (core/get-score state player)]
    (case unit-at-target-position
      :empty
      (-> state
        (core/set-player-position player target-position)
        (core/set-unit-at target-position :monkey)
        (core/set-unit-at player-position :empty))
      (:song :album :playlist)
      (-> state
        (core/set-picked-up-items player (conj picked-up-items
                                               unit-at-target-position))
        (core/set-unit-at target-position :empty))
      :user
      (-> state
        (core/set-score player (+ player-score value-of-picked-up-items))
        (core/set-picked-up-items player []))
      state)))

(defn state-for-player [state player]
  {:layout (:layout state)
   :turns (:turns state)
   :position (core/player-position state player)
   :score (core/get-score state player)
   :pickedUp (core/picked-up-items state player)})

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

(def test-state
  {:layout [[:empty  :user :playlist]
            [:monkey :song :album]]
   :turns 5
   :players {"player1" {:position [1 0]
                        :score 0
                        :picked-up []}}})

(clj->js (state-for-player test-state "player1"))

(-> test-state
    (move "player1" :right)
    (move "player1" :right)
    (move "player1" :right)
    (move "player1" :right)
    (move "player1" :up)
    (move "player1" :up)
    (move "player1" :left))

(def players-as-string "[\"player1\", \"player2\"]")

(def level-as-string "{
  \"turns\": 5,
  \"layout\": [
    \"MUp\",
    \"Msa\"
  ],
  \"units\": {
    \"M\": \"monkey\",
    \"s\": \"song\",
    \"a\": \"album\",
    \"p\": \"playlist\",
    \"U\": \"user\",
    \"#\": \"wall\",
    \" \": \"empty\"
  }
}")
