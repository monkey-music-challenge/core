(ns monkey-music.rules
  (:require [monkey-music.core :as core]))

(defn set-turns-to-zero-if-game-over [state]
  (if (core/game-over? state) (core/set-turns state 0) state))

(defn exec-move [state player direction]
  (let [player-position (core/player-position state player)
        target-position (core/translate player-position direction)
        unit-at-target-position (core/unit-at state target-position)
        picked-up-items (core/picked-up-items state player)
        value-of-picked-up-items (reduce + (map core/value-of picked-up-items))]
    (case unit-at-target-position
      :empty
      (-> state
          (core/set-player-position player target-position)
          (core/set-unit-at target-position :monkey)
          (core/set-unit-at player-position :empty))
      (:song :album :playlist)
      (-> state
          (core/add-to-picked-up-items player unit-at-target-position)
          (core/set-unit-at target-position :empty))
      :user
      (-> state
          (core/inc-score player value-of-picked-up-items)
          (core/set-picked-up-items player []))
      state)))

(defn move [state player direction]
  (-> state
      (core/decrease-turns)
      (exec-move player direction)
      (set-turns-to-zero-if-game-over)))
