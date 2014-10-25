(ns monkey-music.core)

(defn value-of [thing]
  (case thing
    :playlist 3
    :album 2
    :song 1))

(defn translate [[y x] direction]
  (case direction
    :up    [(dec y) x]
    :down  [(inc y) x]
    :left  [y (dec x)]
    :right [y (inc x)]))

(defn create-player [turns position]
  {:score 0
   :picked-up []
   :turns turns
   :position position})

(defn unit-at [state position]
  (get-in state (into [:layout] position) :out-of-bounds))

(defn set-unit-at [state position unit]
  (assoc-in state (into [:layout] position) unit))

(defn player-position [state player]
  (get-in state [:players player :position]))

(defn set-player-position [state player position]
  (assoc-in state [:players player :position] position))

(defn picked-up-items [state player]
  (get-in state [:players player :picked-up]))

(defn set-picked-up-items [state player items]
  (assoc-in state [:players player :picked-up] items))

(defn add-to-picked-up-items [state player item]
  (update-in state [:players player :picked-up] conj item))

(defn get-score [state player]
  (get-in state [:players player :score]))

(defn inc-score [state player amount]
  (update-in state [:players player :score] + amount))

(defn set-score [state player score]
  (assoc-in state [:players player :score] score))

(defn decrease-turns [state]
  (update-in state [:turns] dec))

(defn set-turns [state turns]
  (assoc state :turns turns))

(defn game-over? [state]
  (let [all-units-on-map (flatten (:layout state))
        all-picked-up-items (flatten (map :picked-up (vals (:players state))))]
    (not-any? #{:song :album :playlist} (concat all-units-on-map
                                                all-picked-up-items))))
