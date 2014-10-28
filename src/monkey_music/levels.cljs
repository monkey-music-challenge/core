(ns monkey-music.levels)

(defn parse-unit [units c]
  (if (not (contains? units c))
    (throw (js/Error. (str "unknown unit: " c))))
  (keyword (units c)))

(defn parse-layout [layout units]
  (->> layout
       (mapv vec)
       (mapv (partial mapv (partial parse-unit units)))))

(defn parse
  [{units "units"
    turns "turns"
    layout "layout"
    pick-up-limit "pickUpLimit"}]
  {:layout (parse-layout layout units)
   :pick-up-limit pick-up-limit
   :turns turns})
