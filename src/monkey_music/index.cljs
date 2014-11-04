(ns monkey-music.index
  (:require [monkey-music.levels :as levels]
            [monkey-music.rules :as rules]
            [monkey-music.states :as states]))

(set! *main-cli-fn* (fn []))

(defn create-state [teams level]
  (states/create (js->clj teams) (levels/parse (js->clj level))))

(defn run-commands [state commands]
  (rules/run-commands state (js->clj commands :keywordize-keys true)))

(defn state-for-player [state player-id]
  (let [state (rules/state-for-player state player-id)]
    (clj->js {"layout" (:layout state)
              "pickUpLimit" (:pick-up-limit state)
              "remainingTurns" (:remaining-turns state)
              "position" (:position state)
              "score" (:score state)
              "pickedUpItems" (:picked-up-items state)})))

(defn value-of [item] (rules/value-of (keyword item)))

(def game-over? rules/game-over?)

(aset js/exports "runCommands" run-commands)
(aset js/exports "createState" create-state)
(aset js/exports "stateForPlayer" state-for-player)
(aset js/exports "valueOf" value-of)
(aset js/exports "isGameOver" game-over?)
