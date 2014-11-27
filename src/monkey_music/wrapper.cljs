(ns monkey-music.wrapper
  (:require [monkey-music.core :as c]
            [clojure.string :as string]))

(defn str* [s]
  (cond
    (nil? s) "<nil>"
    (= "" s) "<empty string>"
    (= " " s) "<space>"
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

(defn lookup-legend [legend token]
  (if-let [entity-str (legend token)]
    entity-str
    (throw-error token " not in legend")))

(defn json->layout [layout legend]
  (->> layout
       (mapv vec)
       (mapv (partial mapv #(->> % (lookup-legend legend)
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

(defmethod json->command ::c/idle [{:strs [command team]}]
  {:command ::c/idle :team-name team})

(defmulti hint->json :hint)

(defmethod hint->json ::c/steal
  [{:keys [item from-team-name to-team-name]}]
  {"hint" "steal"
   "item" (name item)
   "fromTeam" from-team-name
   "toTeam" to-team-name})

(defmethod hint->json ::c/trigger-trap
  [{:keys [team-name]}]
  {"hint" "trigger-trap"
   "team" team-name})

(defmethod hint->json ::c/got-tackled
  [{:keys [team-name]}]
  {"hint" "got-tackled"
   "team" team-name})

(defmethod hint->json ::c/enter-tunnel
  [{:keys [item team-name from-position enter-position exit-position]}]
  {"hint" "enter-tunnel"
   "team" team-name
   "from" from-position
   "enter" enter-position
   "exit" exit-position})

(defmethod hint->json ::c/move-team
  [{:keys [team-name from-position to-position]}]
  {"hint" "move-team"
   "team" team-name
   "from" from-position
   "to" to-position})

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
  (when-not (some (partial = team-name) (keys teams))
    (throw-error "team not part of game: " team-name))
  (when (isa? (:command command) ::c/move)
    (when (and (:directions command) (not (get-in (teams team-name) [:buffs ::c/speedy])))
      (throw-error "can only make multiple moves with speedy buff")))
  command)

(defn parse-command [state command]
  (validate-command state (json->command command)))

(defn teams->json [teams]
  (into {} (for [[team-name team] teams] [team-name (team->json team)])))

(defn trap->json [{:keys [team-name position]}]
  {"team" team-name "position" position})

(defn game-state->json-for-renderer
  [{:keys [layout base-layout inventory-size remaining-turns
           teams rendering-hints traps armed-traps] :as state}]
  {"layout" (layout->json layout)
   "baseLayout" (layout->json base-layout)
   "teams" (teams->json teams)
   "inventorySize" inventory-size
   "traps" (map trap->json traps)
   "armedTraps" (map trap->json armed-traps)
   "remainingTurns" remaining-turns
   "isGameOver" (c/game-over? state)
   "renderingHints" (map hint->json rendering-hints)})

(defn game-state->json-for-team
  [{:keys [layout inventory-size remaining-turns teams] :as state}
   team-name]
  (merge
    {"layout" (layout->json layout)
     "inventorySize" inventory-size
     "remainingTurns" remaining-turns
     "isGameOver" (c/game-over? state)}
    (team->json (teams team-name))))
