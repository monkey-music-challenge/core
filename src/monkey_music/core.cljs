(ns monkey-music.core
  (:require [monkey-music.random :as random]))

;; Commands

(derive ::move ::command)
(derive ::use ::command)

;; Directions

(derive ::left ::direction)
(derive ::right ::direction)
(derive ::up ::direction)
(derive ::down ::direction)

;; Entities

(derive ::out-of-bounds ::entity)

(derive ::layoutable ::entity)
(derive ::wall ::layoutable)
(derive ::user ::layoutable)
(derive ::closed-door ::layoutable)
(derive ::lever ::layoutable)
(derive ::tunnel ::layoutable)
(derive ::armed-trap ::layoutable)
(derive ::monkey ::layoutable)

; derive 10 tunnels
(for [i (range 10)]
  (derive (keyword "monkey-music.core" (str "tunnel-" i)) ::tunnel))

(derive ::movable-to ::layoutable)
(derive ::empty ::movable-to)
(derive ::open-door ::movable-to)

(derive ::carryable ::layoutable)

(derive ::usable ::carryable)
(derive ::banana ::usable)
(derive ::trap ::usable)

(derive ::valuable ::carryable)
(derive ::song ::valuable)
(derive ::album ::valuable)
(derive ::playlist ::valuable)

(def value-of
  {::song 1
   ::album 2
   ::playlist 4})

;; Buffs

(derive ::speedy ::buff)

(derive ::immobilized ::buff)
(derive ::tackled ::immobilized)
(derive ::asleep ::immobilized)
(derive ::trapped ::immobilized)

(def duration-of
  {::speedy 4
   ::tackled 2
   ::asleep 2
   ::trapped 2})

(defn add-buff [state team-name buff]
  (assoc-in state [:teams team-name :buffs buff] (duration-of buff)))

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
  (condp isa? direction
    ::up [(dec y) x]
    ::down [(inc y) x]
    ::left [y (dec x)]
    ::right [y (inc x)]))

(defn locate-entities [layout f & args]
  (let [height (count layout)
        width (count (get layout 0))]
    (for [y (range height)
          x (range width)
          :let [entity (get-in layout [y x])]
          :when (apply f entity args)]
      [y x])))

(def locate-entity (comp first locate-entities))

;; Teams

(defn create-team [monkey-position]
  {:position monkey-position
   :buffs {}
   :inventory []
   :score 0})

(defn create-teams [team-names monkey-positions]
  (let [teams (map create-team monkey-positions)]
    (into {} (map vector team-names teams))))

;; Game states

(defn seed [level]
  (apply str (mapcat (partial map name) (:layout level))))

(defn base-entity [entity]
  (if (or (isa? entity ::carryable) (isa? entity ::monkey)) ::empty entity))

(defn toggle-door [entity]
  (condp isa? entity
    ::open-door ::closed-door
    ::closed-door ::open-door
    entity))

(defn remove-one [xs x]
  (let [head (take-while (partial not= x) xs)
        tail (drop (inc (count head)) xs)]
    (into [] (concat head tail))))

(defn map-layout [f layout] (mapv (partial mapv f) layout))

(def base-layout (partial map-layout base-entity))

(defn create-game-state
  [team-names {:keys [layout inventory-size turns] :as level}]
  (let [monkey-positions (locate-entities layout isa? ::monkey)
        unused-monkey-positions (drop (count team-names) monkey-positions)
        layout-without-unused-monkeys (reduce #(assoc-in %1 %2 ::empty)
                                              layout unused-monkey-positions)]
    {:teams (create-teams team-names monkey-positions)
     :random (random/create (seed level))
     :inventory-size inventory-size
     :remaining-turns turns
     :traps []
     :layout layout-without-unused-monkeys
     :base-layout (base-layout layout-without-unused-monkeys)}))

(defn game-over? [{:keys [layout teams remaining-turns]}]
  (let [layout-entities (flatten layout)
        inventory-entities (flatten (map :inventory (vals teams)))]
    (or (zero? remaining-turns)
        (and (not-any? #(isa? % ::valuable) layout-entities)
             (not-any? #(isa? % ::valuable) inventory-entities)))))

(defn team-at [{:keys [teams]} at-position]
  (some (fn [[team-name {:keys [position]}]]
          (if (= position at-position) team-name))
        teams))

;; Commands

(defn apply-speedy-buff [command]
  (if-let [directions (:directions command)]
    (take 2 (map #(assoc (dissoc command :directions) :direction %)
                 directions))
    command))

(defn apply-buffs
  [{:keys [teams] :as state}
   {:keys [team-name command-name] :as command}]
  (let [buffs (get-in teams [team-name :buffs])
        immobilized? (some #(isa? % ::immobilized) (keys buffs))
        speedy? (some #(isa? % ::speedy) (keys buffs))]
    (cond
      immobilized? nil
      speedy? (apply-speedy-buff command)
      :else command)))

(defn apply-all-buffs [state commands]
  (remove nil? (flatten (map (partial apply-buffs state) commands))))

(defn peek-move [{:keys [teams layout]} team-name direction]
  (let [at-position (get-in teams [team-name :position])
        to-position (translate at-position direction)]
    {:at-position at-position
     :to-position to-position
     :to-unit (get-in layout to-position ::out-of-bounds)}))

(defn weighted-shuffle! [{:keys [random]} xs]
  (let [weights (range (count xs) 0 -1)]
    (random/weighted-shuffle! random xs weights)))

(defn move-team [{:keys [base-layout teams] :as state} team-name to-position]
  (let [at-position (get-in teams [team-name :position])
        new-at-unit (get-in base-layout at-position)]
    (-> state
        (assoc-in (into [:layout] at-position) new-at-unit)
        (assoc-in (into [:layout] to-position) ::monkey)
        (assoc-in [:teams team-name :position] to-position))))

(defn dispatch-command [state {:keys [command team-name direction item] :as command}]
  (condp isa? command
    ::move [::move (:to-unit (peek-move state team-name direction))]
    ::use [::use item]))

(defmulti run-command dispatch-command)

(defmethod run-command [::move ::movable-to]
  [state {:keys [team-name direction]}]
  (let [{:keys [to-position]} (peek-move state team-name direction)]
    (move-team state team-name to-position)))

(defmethod run-command [::move ::carryable]
  [{:keys [layout] :as state}
   {:keys [team-name direction] :as command}]
  (let [{:keys [to-position to-unit]} (peek-move state team-name direction)]
    (-> state
        (assoc-in (into [:layout] to-position) ::empty)
        (update-in [:teams team-name :inventory] conj to-unit))))

(defmethod run-command [::move ::user]
  [state {:keys [team-name direction]}]
  (let [items (get-in state [:teams team-name :inventory])
        {valuables true
         non-valuables false} (group-by #(isa? % ::valuable) items)
        total-value (reduce + (map value-of valuables))]
    (-> state
        (update-in [:teams team-name :score] + total-value)
        (assoc-in [:teams team-name :inventory] non-valuables))))

(defn find-first [pred coll] (first (filter pred coll)))

(defmethod run-command [::move ::tunnel]
  [{:keys [layout] :as state}
   {:keys [team-name direction] :as command}]
  (let [{:keys [to-unit at-position to-position]} (peek-move state team-name direction)
        tunnel-positions (locate-entities layout isa? to-unit)
        exit-position (find-first (partial not= to-position) tunnel-positions)]
    (if exit-position
      (move-team state team-name exit-position)
      state)))

(defmethod run-command [::move ::lever] [state command]
  (-> state
      (update-in [:layout] (partial map-layout toggle-door))
      (update-in [:base-layout] (partial map-layout toggle-door))))

(defmethod run-command [::move ::monkey]
  [{:keys [layout teams random] :as state}
   {:keys [team-name direction]}]
  (let [{:keys [to-position]} (peek-move state team-name direction)
        push-to-position (translate to-position direction)
        enemy-team-name (team-at state to-position)
        unit-at-push-to-position (get-in layout push-to-position)
        pushable-to-position (isa? unit-at-push-to-position ::movable-to)
        push-successful (random/weighted-selection! random [true false] [3 1])
        steal-successful (random/weighted-selection! random [true false] [1 1])
        enemy-items (get-in teams [team-name :inventory])
        item-to-steal (random/weighted-selection! random enemy-items (replicate 1 (count enemy-items)))]
    state))
    ;(cond->
      ;push-successful
      ;(add-buff enemy-team-name tackled)

      ;(not push-successful)
      ;(add-buff team-name tackled)

      ;(and push-successful pushable-to-position)
      ;(-> (move-team enemy-team-name push-to-position)
          ;(move-team team-name to-position))

      ;(and push-successful steal-successful)
      ;(-> (remove-item enemy-team-name item-to-steal)
          ;(update-in [:teams team-name :inventory] conj item-to-steal)))))

(defmethod run-command [::use ::banana]
  [{:keys [teams] :as state} {:keys [team-name] :as command}]
  (if (some #(isa? % ::banana) (get-in teams [team-name :inventory]))
    (-> state
        (add-buff team-name ::speedy))
    state))

;; Traps

(defn arm-traps [{:keys [trap-positions] :as state}]
  (-> state
      (update-in [:armed-trap-positions] into trap-positions)
      (assoc-in :trap-positions [])))

(defn check-trap [{:keys [teams] :as state} trap-position]
  (let [{:keys [team-name]} (some #(= trap-position (:position %)) teams)]
    (if team-name
      (-> state
          (update-in [:armed-trap-positions] #(remove #{trap-position} %))
          (add-buff team-name ::trapped)))
      state))

(defn check-armed-traps [{:keys [armed-trap-positions] :as state}]
  (reduce check-trap state armed-trap-positions))

(defmethod run-command [::use ::trap]
  [{:keys [teams] :as state}
   {:keys [team-name]}]
  (let [{:keys [inventory position]} (teams team-name)]
    (if (some (partial isa? ::trap) inventory)
      (-> state
          (update-in [:layout :trap-positions] conj position)
          (update-in [:teams team-name :inventory] remove-one ::trap)))))

;; Default - do nothing

(defmethod run-command :default [state command] state)

;; Game loop

(defn preprocess-commands [state commands]
  (->> commands
       (weighted-shuffle! state)
       (apply-all-buffs state)))

(defn run-all-commands [state commands]
  (reduce #(run-command %1 %2) state commands))

(defn decrease-turns [state]
  (update-in state [:remaining-turns] dec))

(defn sleep-all-absent-teams [{:keys [teams] :as state} commands]
  (let [team-names (into #{} (map :team-name commands))
        missing-team-names (remove team-names (map :team-name teams))]
    (reduce #(add-buff %1 %2 ::asleep) state missing-team-names)))

(defn run-commands [state commands]
  ;(let [preprocessed-commands (preprocess-commands state commands)]
  (-> state
      ;(arm-traps)
      (run-all-commands commands)
      (decrease-turns)
      ;(sleep-all-absent-teams commands)
      (tick-all-buffs)))
      ;(check-armed-traps)))
