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
      (:track :album :playlist)
      (-> state
        (core/set-picked-up-items player (conj picked-up-items
                                               unit-at-target-position))
        (core/set-unit-at target-position :empty))
      :user
      (-> state
        (core/set-score player (+ player-score value-of-picked-up-items))
        (core/set-picked-up-items player []))
      state)))

(def test-state
  {:layout [[:empty  :user  :playlist]
            [:monkey :track :album]]
   :turns 5
   :players {"player1" {:position [1 0]
                        :score 0
                        :picked-up []}}})

(-> test-state
    (move "player1" :right)
    (move "player1" :right)
    (move "player1" :right)
    (move "player1" :right)
    (move "player1" :up)
    (move "player1" :up)
    (move "player1" :left))
