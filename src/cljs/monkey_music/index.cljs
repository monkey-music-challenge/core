(ns monkey-music.index
  (:require [monkey-music.core :as core]
            [monkey-music.rules :as rules]
            [monkey-music.state :as state]))

(set! *main-cli-fn* (fn []))

(defn new-game-state [players level]
  (state/new-game-state (js->clj players) (js->clj level)))

(defn move [state player direction]
  (rules/move state (js->clj player) (keyword direction)))

(defn state-for-player [state player]
  (clj->js (state/state-for-player state (js->clj player))))

(defn value-of [item]
  (core/value-of (keyword item)))

(aset js/exports "move" move)
(aset js/exports "newGameState" new-game-state)
(aset js/exports "stateForPlayer" state-for-player)
