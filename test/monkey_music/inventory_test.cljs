(ns monkey-music.inventory-test
  (:require-macros [cemerick.cljs.test :refer (deftest is are testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

(def state
  (c/create-game-state
    ["1"]
    {:layout [[::c/monkey ::c/song ::c/album ::c/playlist]]
     :inventory-size 2
     :turns 10}))

(deftest test-pick-up-too-much
  (let [curr-state
        (-> state
            (c/run-commands [{:team-name "1" :command ::c/move :direction ::c/right}])
            (c/run-commands [{:team-name "1" :command ::c/move :direction ::c/right}])
            (c/run-commands [{:team-name "1" :command ::c/move :direction ::c/right}])
            (c/run-commands [{:team-name "1" :command ::c/move :direction ::c/right}])
            (c/run-commands [{:team-name "1" :command ::c/move :direction ::c/right}])
            (c/run-commands [{:team-name "1" :command ::c/move :direction ::c/right}]))]
    (are [x y] (= x y)
         [0 2] (get-in curr-state [:teams "1" :position])
         [::c/song ::c/album] (get-in curr-state [:teams "1" :inventory]))))

;(test-ns 'monkey-music.inventory-test)
