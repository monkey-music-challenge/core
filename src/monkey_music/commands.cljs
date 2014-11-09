(ns monkey-music.commands
  (:refer-clojure :exclude [use]))

(def move ::move)
(def use ::use)

(defn parse [command]
  (if-not (command "team")
    (throw (js/Error. "missing team")))
  (case (command "command")
    "move" {:command move
            :direction (directions/parse (command "direction"))}
    "use" {:command use
           :item (units/parse (command "item"))}
    (throw (js/Error. (str "not a command: " (command "command"))))))
