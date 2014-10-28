(ns monkey-music.levels)

(defn parse-layout [layout units]
  (letfn [(parse-unit [c]
            (if-let [unit (units c)]
              (keyword unit)
              (let [msg (str "unknown unit '" c "' in level layout" )]
                (throw (js/Error. msg)))))]
    (->> layout
         (mapv vec)
         (mapv (partial mapv parse-unit)))))

(defn parse
  [{units "units"
    turns "turns"
    layout "layout"
    pick-up-limit "pickUpLimit"}]
  {:layout (parse-layout layout units)
   :pick-up-limit pick-up-limit
   :turns turns})
