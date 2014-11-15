(ns monkey-music.core
  (:require [monkey-music.random :as random]))

(defn throw-error [& msgs] (throw (js/Error. (apply str msgs))))

;; Parsing

(defn json->unit [unit-lookup unit-token]
  (if-let [unit (unit-lookup unit-token)]
    (keyword "monkey-music.core" unit)
    (throw-error "unknown unit: " unit-token)))

(defn json->layout [layout unit-lookup]
  (->> layout
       (mapv vec)
       (mapv (partial mapv (partial json->unit unit-lookup)))))

(defn json->level [{:strs units turns layout pickUpLimit}]
  {:layout (json->layout layout units)
   :pick-up-limit pickUpLimit
   :turns turns})

;; Units - can exist on the map

(derive ::empty ::unit)
(derive ::song ::unit)
(derive ::album ::unit)
(derive ::playlist ::unit)
(derive ::user ::unit)
(derive ::monkey ::unit)
(derive ::banana ::unit)
(derive ::open-door ::unit)
(derive ::closed-door ::unit)

;; Items - can be picked up

(derive ::usable ::item)
(derive ::valuable ::item)

;; Usables - can be used

(derive ::banana ::usable)

;; Valuables - can be handed off to users for points

(derive ::song ::valuable)
(derive ::album ::valuable)
(derive ::playlist ::valuable)

(defn value-of [valuable]
  (case valuable
    ::song 1
    ::album 2
    ::playlist 4
    0))

(defn json->unit [unit]
  (keyword "monkey-music.core" unit))

;; Buffs

(derive ::tackled ::buff)
(derive ::asleep ::buff)
(derive ::speedy ::buff)

(def buff-duration
  {::speedy 4}
  {::tackled 2}
  {::asleep 3})

;; Immobilized - cannot run any commands

(derive ::tackled ::immobilized)
(derive ::asleep ::immobilized)

(defn tick-buffs [buffs]
  (into {} (for [[buff remaining-turns] buffs
                 :when (pos? (dec remaining-turns))]
             [buff (dec remaining-turns)])))

(defn tick-team-buffs [state team-name]
  (update-in state [:teams team-name :buffs] tick-buffs))

(defn tick-all-buffs [state]
  (reduce tick-team-buffs state (keys (:teams state))))

;; Positions

(defn translate [[y x] direction]
  (case direction
    :up [(dec y) x]
    :down [(inc y) x]
    :left [y (dec x)]
    :right [y (inc x)]))

(defn find-all [layout unit]
  (let [height (count layout)
        width (count (get layout 0))]
    (for [y (range height)
          x (range width)
          :let [curr-unit (get-in layout [y x])]
          :when (= curr-unit unit)]
      [y x])))

;; Teams

(defn create-team [monkey-position]
  {:position monkey-position
   :buffs {}
   :picked-up-items []
   :score 0})

(defn create-teams [team-names monkey-positions]
  (if-not (= (count team-names) (count (distinct team-names)))
    (throw-error "duplicate team names: " team-names))
  (if-not (= (count team-names) (count monkey-positions))
    (throw-error "wrong number of teams"))
  (let [teams (map create-team monkey-positions)]
    (into {} (map vector team-names teams))))

(defn team->json [{:keys [position buffs picked-up-items score]}]
  {"buffs" (into {} (for [buff remaining-turns] [(name buff) remaining-turns]))
   "position" position
   "pickedUpItems" picked-up-items
   "score" score})

;; Game states

(defn seed [level]
  (apply str (mapcat (partial map name) (:layout level))))

(defn create-game-state
  [team-names {layout :layout
               pick-up-limit :pick-up-limit
               turns :turns}]
  {:teams (create-teams team-names (find-all layout ::monkey))
   :random (random/create (seed level))
   :pick-up-limit pick-up-limit
   :remaining-turns turns
   :layout layout})

(defn game-state-for-team->json
  [{:keys [layout pick-up-limit remaining-turns teams]} team-name]
  (merge
    {"layout" layout
     "pickUpLimit" pick-up-limit
     "remainingTurns" remaining-turns}
    (team->json (teams team-name))))

(defn game-over? [state]
  nil)

;; Commands

(defn json->command [{:strs [command team] :as json-command}]
  (let [base-command {:command-name command :team-name team}]
    (case command
      "move"
      (if-let [{:strs [direction]} json-command]
        (merge base-command {:direction (keyword direction)})
        (throw-error "missing direction"))
      "use"
      (if-let [{:strs [item]} json-command]
        (merge base-command {:item (json->unit item)})))))

(defn peek-move [state team-name direction]
  (let [at-position (get-in state [:teams team-name :position])
        to-position (translate at-position direction)
        to-unit (get-in state (into [:layout] to-position) ::out-of-bounds)]
    {:at-position at-position
     :to-position to-position
     :to-unit to-unit}))

(defn shuffle-commands [{:keys [random]} commands]
  (let [weights (range (count commands) 0 -1)]
    (random/weighted-shuffle random commands weights)))

(defn run-commands [state commands]
  (-> state
      (run-commands* commands)
      (update-in [:remaining-turns] dec)
      tick-all-buffs))

(defmulti run-command
  (fn [state {:keys [command-name team-name] :as command}]
    (if-not (get-in state [:teams team-name])
      (throw-error "no such team: " team-name))
    (case command-name
      "move"
      (let [{:keys [direction]} command
            {:keys [to-unit]} (peek-move state team-name direction)]
        [:move ::empty]))))

(defmethod run-command [:use ::banana]
  [state {team-name :team}]
  (assoc-in state [:teams team-name :buffs ::speedy] (::speedy buff-duration)))

(defmethod run-command [:move ::empty]
  [state {:keys [team-name direction]}]
  (let [{:keys [at-position to-position]} (peek-move state team-name direction)]
    (-> state
        (assoc-in (into [:layout] at-position) ::empty)
        (assoc-in (into [:layout] to-position) ::monkey)
        (assoc-in [:teams team-name :position] to-position))))

(defmethod run-command [:move ::item]
  [state {team-name :team direction :direction}]
  (let [{:keys [to-position to-unit]} (peek-move state team-name direction)]
    (-> state
        (assoc-in (into [:layout] to-position) ::empty)
        (update-in [:teams team-name :picked-up-items] conj to-unit))))

(defmethod run-command [:move ::user]
  [state {team-name :team}]
  (let [picked-up-items (get-in state [:teams team-name :picked-up-items])
        score-update (reduce + (map value-of picked-up-items))]
    (-> state
        (update-in [:teams team-name :score] + score-update)
        (assoc-in [:teams team-name :picked-up-items] []))))
