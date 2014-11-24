(ns monkey-music.random
  (:require [cemerick.pprng :as pprng]))

(def create pprng/rng)

(defn wrand!
  "given a vector of slice sizes, returns the index of the slice given
  by a random spin of a roulette wheel with compartments proportional to the
  slice sizes"
  [rng slices]
  (when-not (vector? slices)
    (throw (js/Error. "must supply a vector to wrand!")))
  (let [total (reduce + slices)
        r (pprng/int rng total)]
    (loop [i 0 sum 0]
      (if (< r (+ (slices i) sum))
        i
        (recur (inc i) (+ (slices i) sum))))))

(defn weighted-selection! [rng items slices]
  (items (wrand! rng (into [] slices))))

(defn dissoc-vec [v ix]
  (vec (concat (subvec v 0 ix) (subvec v (inc ix)))))

(defn weighted-shuffle! [rng items weights]
  (loop [remaining-items (into [] items)
         remaining-weights (into [] weights)
         selected-items []]
    (if (empty? remaining-items)
      selected-items
      (let [selected-index (wrand! rng remaining-weights)]
        (recur (dissoc-vec remaining-items selected-index)
               (dissoc-vec remaining-weights selected-index)
               (conj selected-items (remaining-items selected-index)))))))
