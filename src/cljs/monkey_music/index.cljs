(ns monkey-music.index
  (:require [monkey-music.levels :as levels]
            [monkey-music.rules :as rules]
            [monkey-music.states :as states]))

(set! *main-cli-fn* (fn []))

;(defn create-state [player-names level]
  ;(states/create player-names (levels/parse (js->clj level))))

;(defn move-player [state player-name direction]
  ;(rules/move-player state player-name (keyword direction)))

;(defn state-for-player [state player-name]
  ;(clj->js (states/for-player state player-name)))

;(defn value-of [item]
  ;(rules/value-of (keyword item)))

;(aset js/exports "move" move-player)
;(aset js/exports "newGameState" new-game-state)
;(aset js/exports "stateForPlayer" state-for-player)
;(aset js/exports "valueOf" value-of)
