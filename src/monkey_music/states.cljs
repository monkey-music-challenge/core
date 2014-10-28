(ns monkey-music.states
  (:require [monkey-music.positions :as positions]))

(defn create-player [turns position]
  {:score 0
   :picked-up-items []
   :turns turns
   :position position})

(defn create-players [player-ids player-positions turns]
  (let [players (mapv (partial create-player turns) player-positions)]
    (into {} (map vector player-ids players))))

(defn create
  [player-ids
   {layout :layout
    pick-up-limit :pick-up-limit
    turns :turns}]
  (let [player-positions (positions/find-players layout)
        players (create-players player-ids player-positions turns)]
    {:layout layout
     :players players
     :pick-up-limit pick-up-limit}))

(def pick-up-limit :pick-up-limit)

(def layout :layout)

(def players (comp vals :players))

(def player-ids (comp keys :players))

(defn position [state player-id]
  (get-in state [:players player-id :position]))

(defn set-position [state player-id position]
  (assoc-in state [:players player-id :position] position))

(defn score [state player-id]
  (get-in state [:players player-id :score]))

(defn inc-score [state player-id amount]
  (update-in state [:players player-id :score] + amount))

(defn remaining-turns [state player-id]
  (get-in state [:players player-id :turns]))

(defn dec-remaining-turns [state player-id]
  (update-in state [:players player-id :turns] dec))

(defn picked-up-items [state player-id]
  (get-in state [:players player-id :picked-up-items]))

(defn set-picked-up-items [state player-id items]
  (assoc-in state [:players player-id :picked-up-items] items))

(defn add-to-picked-up-items [state player-id item]
  (update-in state [:players player-id :picked-up-items] conj item))

(defn unit-at [state position]
  (get-in state (into [:layout] position) :out-of-bounds))

(defn set-unit-at [state position unit]
  (assoc-in state (into [:layout] position) unit))

;; Derived

(defn all-units [state]
  (flatten (layout state)))

(defn all-picked-up-items [state]
  (mapcat (partial picked-up-items state) (player-ids state)))

(defn total-remaining-turns [state]
  (reduce + (map (partial remaining-turns state) (player-ids state))))
