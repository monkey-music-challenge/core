(ns monkey-music.random
  (:require [cemerick.pprng :as pprng]))

(defn create [seed-string]
  (pprng/rng seed-string))

(defn weighted-selection!
  "given a map of items and their weights, returns the item selected by a
  random spin of a roulette wheel with compartments proportional to the
  weights."
  [weight-map rng]
  (let [weights (vec (vals weight-map))
        items (vec (keys weight-map))
        total (reduce + weights)
        r (pprng/int rng total)]
    (loop [i 0 sum 0]
      (if (< r (+ (weights i) sum))
        (items i)
        (recur (inc i) (+ (weights i) sum))))))

(defn weighted-shuffle! [weight-map rng]
  (loop [remaining-items weight-map
         selected-items []]
    (if (empty? remaining-items)
      selected-items
      (let [selected-item (weighted-selection! remaining-items rng)]
        (recur (dissoc remaining-items selected-item)
               (conj selected-items selected-item))))))
