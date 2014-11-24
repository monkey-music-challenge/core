(ns monkey-music.sleep-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

(def state
  (c/create-game-state
    ["1" "2"]
    {:layout [[::c/monkey ::c/empty]
              [::c/monkey ::c/empty]]
     :inventory-size 3
     :turns 10}))

;; TODO

(test-ns 'monkey-music.sleep-test)
