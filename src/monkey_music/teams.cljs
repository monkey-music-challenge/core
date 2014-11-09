(ns monkey-music.teams
  (:require [monkey-music.buffs :as buffs]))

(defn duplicates? [xs]
  (not= (count xs) (count (distinct xs))))

(defn create [monkey-position]
  {:position monkey-position
   :buffs (buffs/create)
   :picked-up-items []
   :score 0})

(defn create-all [team-names monkey-positions]
  (if (duplicates? team-names)
    (throw (js/Error. (str "duplicate team names: " team-names))))
  (let [teams (map create monkey-positions)]
    (into {} (map vector team-names teams))))
