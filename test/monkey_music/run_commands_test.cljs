(ns monkey-music.run-commands-test
  (:require-macros [cemerick.cljs.test :refer (deftest is are testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

(def state
  (c/create-game-state
    ["1"]
    {:layout [[::c/monkey]]
     :turns 1
     :inventory-size 1}))

(deftest test-run-commands-when-game-is-over
  (let [state-with-game-over (c/run-commands state [])]
    (are [x y] (= x y)
         (:remaining-turns state-with-game-over)
         (:remaining-turns (c/run-commands state-with-game-over [])))))

;(test-ns 'monkey-music.run-commands-test)
