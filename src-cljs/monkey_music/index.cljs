(ns monkey-music.index
  (:require [monkey-music.wrapper :as wrapper]
            [monkey-music.core :as core]))

(set! *main-cli-fn* (fn []))

(defn args->clj [f] (fn [& args] (apply f (map js->clj args))))
(def return->js (partial comp clj->js))

;; Functions returning Clojure values

(aset js/exports "parseCommand"
      (fn [command] (clj->js (wrapper/json->command (js->clj command)))))
(aset js/exports "createGameState"
      (fn [player-names level] (wrapper/create-game-state (js->clj player-names) (js->clj level))))
(aset js/exports "runCommands" core/run-commands)

;; Functions returning JS values

(aset js/exports "gameStateForTeam"
      (fn [state team-name] (clj->js (wrapper/game-state->json-for-team state team-name))))
(aset js/exports "isGameOver" core/game-over?)
