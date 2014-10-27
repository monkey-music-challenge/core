(ns monkey-music.rules
  (:require [monkey-music.states :as states]
            [monkey-music.positions :as positions]))

(defn value-of 
  ([item]
   (case item
     :playlist 4
     :album 2
     :song 1
     0))
  ([item & items] (reduce + (map value-of (conj items item)))))

(defn game-over? [state]
  (let [all-units-on-map (flatten (states/layout state))
        all-picked-up-items (mapcat :picked-up-items (states/players state))]
    (not-any? #{:song :album :playlist} (concat all-units-on-map
                                                all-picked-up-items))))

(defn can-pick-up-more? [state player-name]
  (< (count (states/picked-up-items state player-name))
     (states/pick-up-limit state)))

(defn move-player* [state player-name direction]
  (let [player-position (states/position state player-name)
        target-position (positions/translate player-position direction)
        unit-at-target-position (states/unit-at state target-position)
        picked-up-items (states/picked-up-items state player-name)
        value-of-picked-up-items (apply value-of (states/picked-up-items state player-name))]
    (case unit-at-target-position
      :empty
      (-> state
          (states/set-position player-name target-position)
          (states/set-unit-at target-position :monkey)
          (states/set-unit-at player-position :empty))
      (:song :album :playlist)
      (if (can-pick-up-more? state player-name)
        (-> state
          (states/add-to-picked-up-items player-name unit-at-target-position)
          (states/set-unit-at target-position :empty))
        state)
      :user
      (-> state
          (states/inc-score player-name value-of-picked-up-items)
          (states/set-picked-up-items player-name []))
      state)))

(defn move-player [state player-name direction]
  (if (and (pos? (states/remaining-turns state player-name))
           (not (game-over? state)))
    (-> state
        (states/dec-remaining-turns player-name)
        (move-player* player-name direction))
    state))
