(ns monkey-music.index
  (:require [monkey-music.levels :as levels]
            [monkey-music.rules :as rules]
            [monkey-music.states :as states]))

(set! *main-cli-fn* (fn []))

(defn create-state [player-ids level]
  (states/create player-ids (levels/parse (js->clj level))))

(defn move-player [state player-id direction]
  (rules/move-player state player-id (keyword direction)))

(defn state-for-player [state player-id]
  (clj->js (rules/state-for-player state player-id)))

(defn value-of [item]
  (rules/value-of (keyword item)))

(aset js/exports "movePlayer" move-player)
(aset js/exports "newGameState" create-state)
(aset js/exports "stateForPlayer" state-for-player)
(aset js/exports "valueOf" value-of)
