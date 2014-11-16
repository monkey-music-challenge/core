(ns monkey-music.index
  (:require [monkey-music.wrapper :as wrapper]
            [monkey-music.core :as core]))

(set! *main-cli-fn* (fn []))

(defn args->clj [f] (fn [& args] (apply f (map js->clj args))))
(def return->js (partial comp clj->js))

;; Functions returning Clojure values

(aset js/exports "parseCommand"
      (args->clj wrapper/json->command))
(aset js/exports "createGameState"
      (args->clj wrapper/create-game-state))
(aset js/exports "runCommands"
      (args->clj core/run-commands))


;; Functions returning JS values

(aset js/exports "gameStateForTeam"
      (return->js (args->clj wrapper/game-state->json-for-team)))
(aset js/exports "isGameOver"
      (return->js (args->clj core/game-over?)))
(aset js/exports "valueOf"
      (return->js (args->clj core/value-of)))
