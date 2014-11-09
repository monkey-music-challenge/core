(ns monkey-music.index
  (:require [monkey-music.levels :as levels]
            [monkey-music.rules :as rules]
            [monkey-music.units :as units]
            [monkey-music.commands :as command]
            [monkey-music.core :as core]))

(set! *main-cli-fn* (fn []))

(defn create-game [team-names level]
  (core/create-game (js->clj team-names) (levels/parse (js->clj level))))

(defn parse-command [command]
  (commands/parse (js->clj command)))

(defn game-state-for-team [game-state team-name]
  (let [team-state (rules/game-state-for-team game-state team-name)]
    (clj->js {"layout" (:layout team-state)
              "pickUpLimit" (:pick-up-limit team-state)
              "remainingTurns" (:remaining-turns team-state)
              "position" (:position team-state)
              "score" (:score team-state)
              "pickedUpItems" (units/stringify (:picked-up-items team-state))})))

(defn value-of [item] (rules/value-of (keyword item)))

(aset js/exports "runCommands" rules/run-commands)
(aset js/exports "isGameOver" rules/game-over?)
(aset js/exports "parseCommand" parse-command)
(aset js/exports "createGame" create-game)
(aset js/exports "gameStateForTeam" state-for-team)
(aset js/exports "valueOf" value-of)
