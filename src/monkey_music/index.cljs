(ns monkey-music.index
  (:require [monkey-music.rules :as rules]))

(set! *main-cli-fn* (fn []))

(defn new-game-state [players level]
  (rules/new-game-state (js->clj players) (js->clj level)))

(defn move [state player direction]
  (rules/move state (js->clj player) (keyword direction)))

(defn state-for-player [state player]
  (clj->js (rules/state-for-player state (js->clj player))))

(aset js/exports "move" move)
(aset js/exports "newGameState" new-game-state)
(aset js/exports "stateForPlayer" state-for-player)
