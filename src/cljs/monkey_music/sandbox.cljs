(ns monkey-music.sandbox
  (:require [monkey-music.core :as core]
            [monkey-music.rules :as rules]
            [monkey-music.state :as state]))

(def test-state
  {:layout [[:empty  :user :playlist]
            [:monkey :song :album]]
   :turns 10
   :players {"player1" {:position [1 0]
                        :score 0
                        :picked-up []}}})

(clj->js (state/state-for-player test-state "player1"))

(-> test-state
    (rules/move "player1" :right)
    (rules/move "player1" :right)
    (rules/move "player1" :right)
    (rules/move "player1" :right)
    (rules/move "player1" :up)
    (rules/move "player1" :up)
    (rules/move "player1" :left))
