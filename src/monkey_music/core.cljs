(ns monkey-music.core
  (:require [monkey-music.random :as random]))

(defn throw-error [& msgs] (throw (js/Error. (apply str msgs))))

;; Entities - can exist on the map

(derive ::empty ::unit)
(derive ::song ::unit)
(derive ::album ::unit)
(derive ::playlist ::unit)
(derive ::user ::unit)
(derive ::monkey ::unit)
(derive ::banana ::unit)
(derive ::open-door ::unit)
(derive ::closed-door ::unit)

;; Tunnels

(derive ::tunnel-entrance-1 ::tunnel-entrance)
(derive ::tunnel-exit-1 ::tunnel-exit)

(def exit-for
  {::tunnel-entrance-1 ::tunnel-exit-1})

;; Entities that can be moved to 

(derive ::empty ::movable-to)
(derive ::open-door ::movable-to)

;; Items - can be picked up

(derive ::usable ::pick-upable)
(derive ::valuable ::pick-upable)

;; Usables - can be used

(derive ::banana ::usable)

;; Valuables - can be handed off to users for points

(derive ::song ::valuable)
(derive ::album ::valuable)
(derive ::playlist ::valuable)

(def value-of
  {::song 1
   ::album 2
   ::playlist 4})

;; Buffs

(derive ::tackled ::buff)
(derive ::asleep ::buff)
(derive ::speedy ::buff)

(def buff-duration
  {::speedy 4
   ::tackled 2
   ::asleep 3})

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

;; Game states

(defn seed [level]
  (apply str (mapcat (partial map name) (:layout level))))

(defn create-game-state
  [team-names {:keys [layout pick-up-limit turns starting-positions]}]
  {:teams (create-teams team-names starting-positions)
   :random (random/create (seed level))
   :pick-up-limit pick-up-limit
   :remaining-turns turns
   :layout layout
   :original-layout layout})

(defn game-over? [state]
  nil)

;; Commands

(defn peek-move [state team-name direction]
  (let [at-position (get-in state [:teams team-name :position])
        to-position (translate at-position direction)
        to-unit (get-in state (into [:layout] to-position) ::out-of-bounds)
        original-at-unit (get-in state (into [:original-layout] at-position))]
    {:at-position at-position
     :to-position to-position
     :to-unit to-unit
     :original-at-unit original-at-unit}))

(defn shuffle-commands [{:keys [random]} commands]
  (let [weights (range (count commands) 0 -1)]
    (random/weighted-shuffle random commands weights)))

(defn run-commands [state commands]
  (-> state
      (run-commands* commands)
      (update-in [:remaining-turns] dec)
      tick-all-buffs))

(defn find-positions [layout pred & args]
  (for [[y row] (map-indexed vector layout)
        [x unit] (map-indexed vector row)
        :when (apply pred unit args)]
    [y x]))

(defn move-team [state team-name to-position]
  (let [at-position (get-in state [:teams team-name :position])
        original-at-unit (get-in state (into [:original-layout] at-position))
        new-at-unit (if-not (isa? original-at-unit ::pick-upable)
                      original-at-unit ::empty)]
    (-> state
        (assoc-in (into [:layout] at-position) new-at-unit)
        (assoc-in (into [:layout] to-position) ::monkey)
        (assoc-in [:teams team-name :position] to-position))))

(defmulti run-command (fn [state {:keys [command-name team-name] :as command}]
  (if-not (get-in state [:teams team-name])
    (throw-error "no such team: " team-name))
  (case command-name
    "move"
    (let [{:keys [direction]} command
          {:keys [to-unit]} (peek-move state team-name direction)]
      [:move to-unit]))))

(defmethod run-command [:move ::movable-to]
  [state {:keys [team-name direction]}]
  (let [{:keys [to-position]} (peek-move state team-name direction)]
    (move-team state team-name to-position)))

(defmethod run-command [:move ::user]
  [state {:keys [team-name]}]
  (let [items (get-in state [:teams team-name :picked-up-items])
        valuables (filter #(isa? % ::valuable) items)
        non-valuables (filter #(not (isa? % ::valuable)) items)
        total-value (reduce + (map value-of valuables))]
    (-> state
        (update-in [:teams team-name :score] + total-value)
        (assoc-in [:teams team-name :picked-up-items] non-valuables))))

(defmethod run-command [:move ::pick-upable]
  [state {:keys [team-name direction]}]
  (let [{:keys [to-position to-unit]} (peek-move state team-name direction)]
    (-> state
        (assoc-in (into [:layout] to-position) ::empty)
        (update-in [:teams team-name :picked-up-items] conj to-unit))))

(defmethod run-command [:move ::tunnel-entrance]
  [{:keys [original-layout] :as state}
   {:keys [team-name direction] :as command}]
  (let [{:keys [to-unit]} (peek-move state team-name direction)
        exit (exit-for to-unit)
        exit-position (first (find-positions original-layout = exit))
        unit-at-exit-position (get-in state (into [:layout] exit-position))
        exit-is-blocked (not (isa? unit-at-exit-position ::tunnel-exit))]
    (if-not exit-is-blocked
      (move-team state team-name tunnel-exit-position)
      state)))

(defmethod run-command [:use ::banana]
  [state {team-name :team}]
  (assoc-in state [:teams team-name :buffs ::speedy] (::speedy buff-duration)))
