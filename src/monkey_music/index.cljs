(ns monkey-music.index
  (:require [monkey-music.wrapper :as wrapper]
            [monkey-music.core :as core]))

(set! *main-cli-fn* (fn []))
(enable-console-print!)

;; Functions returning Clojure values

(defn parse-command [state command]
  (wrapper/parse-command state (js->clj command)))

(defn create-game-state [player-names level]
  (wrapper/create-game-state (js->clj player-names) (js->clj level)))

(defn run-commands [state commands]
  (core/run-commands state (js->clj commands)))

;; Functions returning JS values

(defn game-state-for-team [state team-name]
  (clj->js (wrapper/game-state->json-for-team state team-name)))

(defn game-state-for-renderer [state]
  (clj->js (wrapper/game-state->json-for-renderer state)))

(aset js/exports "gameStateForTeam" game-state-for-team)
(aset js/exports "gameStateForRenderer" game-state-for-renderer)
(aset js/exports "isGameOver" core/game-over?)
(aset js/exports "runCommands" run-commands)
(aset js/exports "createGameState" create-game-state)
(aset js/exports "parseCommand" parse-command)
