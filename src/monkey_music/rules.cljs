(ns monkey-music.rules
  (:require [monkey-music.states :as states]
            [monkey-music.positions :as positions]
            [monkey-music.commands :as commands]))

(defn value-of* [item]
  (case item
    :playlist 4
    :album 2
    :song 1
    0))

(defn value-of [items]
  (if (seq items)
    (reduce + (map value-of* items))
    (value-of* items)))

(defn can-pick-up-more? [state team-name]
  (< (count (states/picked-up-items state team-name))
     (states/pick-up-limit state)))

(defn game-over? [state]
  (let [all-the-things (concat (states/all-units state)
                               (states/all-picked-up-items state))
        total-remaining-points (value-of all-the-things)]
    (or (zero? (states/total-remaining-turns state))
        (zero? total-remaining-points))))

(defn shuffle-commands [state commands]
  [state commands])

(defn shuffle-and-run-commands [state commands]
  (apply (partial reduce run-command) (shuffle-commands state commands)))

(defn run-commands [state commands]
  (if (game-over? state)
    (throw (js/Error. "the game is over")))
  (-> state
      states/dec-remaining-turns
      (shuffle-and-run-commands commands)
      (update-buffs))
  state)

(defn validate-command [state {command :command team-name :team}]
  (if (states/has-team? state team-name) command :noop))

(defmulti run-command validate-command)

(defn move-team [state team-name target-position]
  )

(defn pick-up-item [state team-name item item-position]
  (if (can-pick-up-more? state team-name)
    (-> state
        (states/add-to-picked-up-items team-name item)
        (states/set-unit-at item-position :empty))
    state))

(defn unit-at-target [state {team-name :team direction :direction}]
  nil)

(defn command-dispatcher [state {command :command team-name :team direction :direction}]
  (if (states/has-team? state team-name)
    )
  (case command
    :move [:move (unit-at-target-position state team-name direction)]
    :use [:use (:item command)]
    :noop))

(defmulti run-command command-dispatcher)

(defmethod run-command [commands/use units/banana]
  [state {team-name :team}]
  nil)

(defmethod run-command [commands/use units/magic-lamp]
  [state {team-name :team}]
  nil)

(defmethod run-command [commands/move units/empty]
  [state {team-name :team direction :direction}]
  (-> state
      (states/set-unit-at (states/team-position state team-name) :empty)
      (states/set-unit-at target-position :monkey)
      (states/set-position team-name target-position)))

(defmethod run-command [commands/move units/item]
  [state {team-name :team direction :direction}]
  nil)

(defmethod run-command [commands/move units/user]
  [state {team-name :team direction :direction}]
  (-> state
          (states/inc-score team-name value-of-picked-up-items)
          (states/set-picked-up-items team-name [])))

(defn state-for-team [game-state team-name]
  {:layout (states/layout state)
   :pick-up-limit (states/pick-up-limit state)
   :remaining-turns (states/remaining-turns state)
   :position (states/position state team-name)
   :score (states/score state team-name)
   :picked-up-items (states/picked-up-items state team-name)})
