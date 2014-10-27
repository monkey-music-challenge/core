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
  ([item & items]
   (reduce + (map value-of (conj items item)))))

(defn can-pick-up-more? [state player-id]
  (< (count (states/picked-up-items state player-id))
     (states/pick-up-limit state)))

(defn game-over? [state]
  (let [all-the-things (concat (states/all-units state)
                               (states/all-picked-up-items state))
        total-remaining-points (apply value-of all-the-things)]
    (or (zero? (states/total-remaining-turns state))
        (zero? total-remaining-points))))

(defn move-player* [state player-id direction]
  (let [player-position (states/position state player-id)
        target-position (positions/translate player-position direction)
        unit-at-target-position (states/unit-at state target-position)
        picked-up-items (states/picked-up-items state player-id)
        value-of-picked-up-items (apply value-of (states/picked-up-items state player-id))]
    (case unit-at-target-position
      :empty
      (-> state
          (states/set-position player-id target-position)
          (states/set-unit-at target-position :monkey)
          (states/set-unit-at player-position :empty))
      (:song :album :playlist)
      (if (can-pick-up-more? state player-id)
        (-> state
          (states/add-to-picked-up-items player-id unit-at-target-position)
          (states/set-unit-at target-position :empty))
        state)
      :user
      (-> state
          (states/inc-score player-id value-of-picked-up-items)
          (states/set-picked-up-items player-id []))
      state)))

(defn move-player [state player-id direction]
  (if (and (pos? (states/remaining-turns state player-id))
           (not (game-over? state)))
    (-> state
        (states/dec-remaining-turns player-id)
        (move-player* player-id direction))
    state))

(defn state-for-player [state player-id]
  {:layout (states/layout state)
   :pick-up-limit (states/pick-up-limit state)
   :remaining-turns (states/remaining-turns state player-id)
   :position (states/position state player-id)
   :score (states/score state player-ids)
   :picked-up-items (states/picked-up-items state player-id)})
