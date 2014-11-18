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
      entity
      (throw-error "Unknown " (name entity) ": " s ". Known " (name entity) "s: "
                   (string/join ", " (map name (leaf-descendants entity))) "."))))

(defn json->layout [layout legend]
  (->> layout
       (mapv vec)
       (mapv (partial mapv (->> legend
                                (str->entity ::c/layoutable))))))

(defn json->level [{:strs [legend turns layout inventorySize]}]
  {:layout (json->layout layout legend)
   :pick-up-limit pickUpLimit
   :turns turns})

(defn json->command
  [{:strs [command team item direction directions]}]
  (let [base-command {:command (str->entity ::c/command command) :team-name team}]
    (condp isa? (:command base-command)
      ::move (assoc base-command :direction (str->direction direction))
      ::use (assoc base-command :item (str->item item)))))

(defmulti json->command #(str->entity ::c/command (get % "command")))

(defmulti json->command ::move [{:strs [command team direction directions]}]
  (merge {:command ::move :team-name team}
         (if directions
           {:directions (map (partial str->entity ::c/direction) directions)}
           {:direction (str->entity ::c/direction direction)})))

(defmulti json->command ::use [{:strs [command team item]}]
  {:command ::use :team-name team :item (str->entity ::c/usable item)})

(defn parse-command [state command]
  (c/validate-command state (json->command command)))

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
  [{:keys [layout pick-up-limit remaining-turns teams] :as state} team-name]
  (merge
    {"layout" (layout->json layout)
     "pickUpLimit" pick-up-limit
     "remainingTurns" remaining-turns}
     "isGameOver" (c/game-over? state)
    (team->json (teams team-name))))
