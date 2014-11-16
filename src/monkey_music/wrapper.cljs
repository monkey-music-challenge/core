(ns monkey-music.wrapper
  (:require [monkey-music.core :as c]))

(defn str->unit [s]
  (let [unit (keyword "monkey-music.core" s)]
    (if (isa? unit ::c/unit) unit (throw-error "not a unit " s))))

(defn str->item [s]
  (let [item (keyword "monkey-music.core" s)]
    (if (isa? item ::c/usable) item (c/throw-error "not an item: " s))))

(defn json->unit [unit-lookup unit-token]
  (if-let [str-unit (unit-lookup unit-token)]
    (str->unit str-unit)
    (c/throw-error "unknown unit: " unit-token)))

(defn json->layout [layout unit-dict]
  (->> layout
       (mapv vec)
       (mapv (partial mapv (partial json->unit unit-dict)))))

(defn json->level [{:strs [units turns layout pickUpLimit]}]
  {:layout (json->layout layout units)
   :pick-up-limit pickUpLimit
   :turns turns})

(defn json->command [{:strs [command team] :as json-command}]
  (let [base-command {:command-name command :team-name team}]
    (case command
      "move"
      (if-let [{:strs [direction]} json-command]
        (merge base-command {:direction (keyword direction)})
        (c/throw-error "missing direction"))
      "use"
      (if-let [{:strs [item]} json-command]
        (merge base-command {:item (str->item item)})
        (c/throw-error "missing item")))))

(defn create-game-state [team-names json-level]
  (c/create-game-state team-names (json->level json-level)))

(defn team->json [{:keys [position buffs picked-up-items score]}]
  {"buffs" (into {} (for [[buff remaining-turns] buffs] [(name buff) remaining-turns]))
   "position" position
   "pickedUpItems" (map name picked-up-items)
   "score" score})

(defn layout->json [layout]
  (map (partial map name) layout))

(defn game-state->json-for-team
  [{:keys [layout pick-up-limit remaining-turns teams]} team-name]
  (merge
    {"layout" (layout->json layout)
     "pickUpLimit" pick-up-limit
     "remainingTurns" remaining-turns}
    (team->json (teams team-name))))
