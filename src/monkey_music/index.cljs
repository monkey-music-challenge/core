(ns monkey-music.index
  (:require [monkey-music.core :as core]))

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

(defn json->unit [unit]
  (keyword "monkey-music.core" unit))

(defn json->level-unit [unit-lookup unit-token]
  (if-let [unit (unit-lookup unit-token)]
    (keyword "monkey-music.core" unit)
    (throw-error "unknown unit: " unit-token)))

(defn json->layout [layout unit-lookup]
  (->> layout
       (mapv vec)
       (mapv (partial mapv (partial json->unit unit-lookup)))))

(defn json->level [{:strs units turns layout pickUpLimit startingPositions}]
  {:layout (json->layout layout units)
   :starting-postions startingPositions
   :pick-up-limit pickUpLimit
   :turns turns})

(defn json->command [{:strs [command team] :as json-command}]
  (let [base-command {:command-name command :team-name team}]
    (case command
      "move"
      (if-let [{:strs [direction]} json-command]
        (merge base-command {:direction (keyword direction)})
        (throw-error "missing direction"))
      "use"
      (if-let [{:strs [item]} json-command]
        (merge base-command {:item (json->unit item)})))))

(defn team->json [{:keys [position buffs picked-up-items score]}]
  {"buffs" (into {} (for [buff remaining-turns] [(name buff) remaining-turns]))
   "position" position
   "pickedUpItems" picked-up-items
   "score" score})

(defn game-state-for-team->json
  [{:keys [layout pick-up-limit remaining-turns teams]} team-name]
  (merge
    {"layout" layout
     "pickUpLimit" pick-up-limit
     "remainingTurns" remaining-turns}
    (team->json (teams team-name))))

(aset js/exports "runCommands" rules/run-commands)
(aset js/exports "isGameOver" rules/game-over?)
(aset js/exports "parseCommand" parse-command)
(aset js/exports "createGame" create-game)
(aset js/exports "gameStateForTeam" state-for-team)
(aset js/exports "valueOf" value-of)
