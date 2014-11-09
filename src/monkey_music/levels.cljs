(ns monkey-music.levels
  (:require [monkey-music.units :as units]))

(defn parse-unit [unit-lookup unit-token]
  (if (not (contains? unit-lookup unit-token))
    (throw (js/Error. (str "unknown unit: " unit-token))))
  (units/parse (get unit-lookup unit-token)))

(defn parse-layout [layout unit-lookup]
  (->> layout
       (mapv vec)
       (mapv (partial mapv (partial parse-unit unit-lookup)))))

(defn parse
  [{unit-lookup "units"
    turns "turns"
    layout "layout"
    pick-up-limit "pickUpLimit"}]
  {:layout (parse-layout layout unit-lookup)
   :pick-up-limit pick-up-limit
   :turns turns})

(defn seed [level]
  (apply str (mapcat (partial map units/stringify) (:layout level))))
