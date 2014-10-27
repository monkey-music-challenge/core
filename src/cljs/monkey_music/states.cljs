(ns monkey-music.states
  (:require [monkey-music.positions :as positions]))

(defn create-player [turns position]
  {:score 0
   :picked-up-items []
   :turns turns
   :position position})

(defn create-players [player-names player-positions turns]
  (let [players (mapv (partial create-player turns) player-positions)]
    (into {} (map vector player-names players))))

(defn create
  [player-names
   {layout :layout
    pick-up-limit :pick-up-limit
    turns :turns}]
  (let [player-positions (positions/find-players layout)
        players (create-players player-names player-positions turns)]
    {:layout layout
     :players players
     :pick-up-limit pick-up-limit}))

(def pick-up-limit :pick-up-limit)

(def layout :layout)

(def players (comp vals :players))

(defn position [state player-name]
  (get-in state [:players player-name :position]))

(defn set-position [state player-name position]
  (assoc-in state [:players player-name :position] position))

(defn score [state player-name]
  (get-in state [:players player-name :score]))

(defn inc-score [state player-name amount]
  (update-in state [:players player-name :score] + amount))

(defn remaining-turns [state player-name]
  (get-in state [:players player-name :turns]))

(defn dec-remaining-turns [state player-name]
  (update-in state [:players player-name :turns] dec))

(defn picked-up-items [state player-name]
  (get-in state [:players player-name :picked-up]))

(defn set-picked-up-items [state player-name items]
  (assoc-in state [:players player-name :picked-up] items))

(defn add-to-picked-up-items [state player-name item]
  (update-in state [:players player-name :picked-up] conj item))

(defn unit-at [state position]
  (get-in state (into [:layout] position) :out-of-bounds))

(defn set-unit-at [state position unit]
  (assoc-in state (into [:layout] position) unit))

(defn for-player [state player-name]
  {:layout (:layout state)
   :pick-up-limit (:pick-up-limit state)
   :remaining-turns (remaining-turns state player-name)
   :position (position state player-name)
   :score (score state player-name)
   :picked-up-items (picked-up-items state player-name)})
