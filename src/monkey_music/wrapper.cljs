(ns monkey-music.wrapper
  (:require [monkey-music.core :as c]
            [clojure.string :as string]))

(defn str* [s]
  (cond
    (nil? s) "<nil>"
    (= "" s) "<empty string>"
    :else s))

(defn throw-error [& msgs]
  (throw (js/Error. (apply str (map str* msgs)))))

(defn leaf-descendants [entity]
  (filter (comp nil? descendants) (descendants entity)))

(defn str->entity [entity s]
  (let [parsed-entity (keyword "monkey-music.core" s)]
    (if (isa? parsed-entity entity)
      parsed-entity
      (throw-error "Unknown " (name entity) ": " s ". Known " (name entity) "s: "
                   (string/join ", " (map name (leaf-descendants entity))) "."))))

(defn json->layout [layout legend]
  (->> layout
       (mapv vec)
       (mapv (partial mapv #(->> % (legend)
                                   (str->entity ::c/layoutable))))))

(defn json->level [{:strs [legend turns layout inventorySize]}]
  {:layout (json->layout layout legend)
   :inventory-size inventorySize
   :turns turns})

(defmulti json->command #(str->entity ::c/command (get % "command")))

(defmethod json->command ::c/move [{:strs [command team direction directions]}]
  (merge {:command ::c/move :team-name team}
         (if directions
           {:directions (map (partial str->entity ::c/direction) directions)}
           {:direction (str->entity ::c/direction direction)})))

(defmethod json->command ::c/use [{:strs [command team item]}]
  {:command ::c/use :team-name team :item (str->entity ::c/usable item)})

(defn team->json [{:keys [position buffs inventory score]}]
  {"buffs" (into {} (for [[buff remaining-turns] buffs] [(name buff) remaining-turns]))
   "position" position
   "inventory" (map name inventory)
   "score" score})

(defn layout->json [layout]
  (map (partial map name) layout))

;;; Exports

(defn create-game-state [team-names json-level]
  (c/create-game-state team-names (json->level json-level)))

(defn validate-command
  [{:keys [teams]}
   {:keys [team-name] :as command}]
  (if (some (partial = team-name) (vals teams))
    command
    (throw-error "team not part of game: " team-name)))

(defn parse-command [state command]
  (validate-command state (json->command command)))

(defn game-state->json-for-renderer
  [{:keys [layout base-layout inventory-size remaining-turns
           teams rendering-hints] :as state}]
  {"layout" (layout->json layout)
   "baseLayout" (layout->json base-layout)
   "teams" (mapv team->json (vals teams))
   "inventorySize" inventory-size
   "remainingTurns" remaining-turns
   "isGameOver" (c/game-over? state)
   "renderingHints" rendering-hints})

(defn game-state->json-for-team
  [{:keys [layout inventory-size remaining-turns teams] :as state}
   team-name]
  (merge
    {"layout" (layout->json layout)
     "inventorySize" inventory-size
     "remainingTurns" remaining-turns
     "isGameOver" (c/game-over? state)}
    (team->json (teams team-name))))
