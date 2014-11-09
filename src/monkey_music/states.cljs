(ns monkey-music.states
  (:require [monkey-music.positions :as positions]
            [monkey-music.teams :as teams]
            [monkey-music.random :as random]))

(defn create [team-names level]
  {:teams (teams/create-all team-names (positions/find-monkeys (:layout level)))
   :random (random/create (levels/seed level))
   :pick-up-limit (:pick-up-limit level)
   :remaining-turns (:turns level)
   :layout (:layout level)})

(def remaining-turns :remaining-turns)
(def pick-up-limit :pick-up-limit)
(def team-names (comp keys :teams))
(def teams (comp vals :teams))
(def layout :layout)
(def random :random)

(defn dec-remaining-turns [state]
  (update-in state [:remaining-turns] dec))

(defn has-team? [state team-name]
  (contains? (:teams state) team-name))

(defn position [state team-name]
  (get-in state [:teams team-name :position]))

(defn set-position [state team-name position]
  (assoc-in state [:teams team-name :position] position))

(defn score [state team-name]
  (get-in state [:teams team-name :score]))

(defn inc-score [state team-name amount]
  (update-in state [:teams team-name :score] + amount))

(defn picked-up-items [state team-name]
  (get-in state [:teams team-name :picked-up-items]))

(defn set-picked-up-items [state team-name items]
  (assoc-in state [:teams team-name :picked-up-items] items))

(defn add-to-picked-up-items [state team-name item]
  (update-in state [:teams team-name :picked-up-items] conj item))

(defn unit-at [state position]
  (get-in state (into [:layout] position) :out-of-bounds))

(defn set-unit-at [state position unit]
  (assoc-in state (into [:layout] position) unit))

;; Derived

(defn all-units [state]
  (flatten (layout state)))

(defn all-picked-up-items [state]
  (mapcat (partial picked-up-items state) (team-names state)))

(defn total-remaining-turns [state]
  (reduce + (map (partial remaining-turns state) (team-names state))))
