(ns monkey-music.sleep-test
  (:require-macros [cemerick.cljs.test :refer (deftest is are testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

(def state
  (c/create-game-state
    ["1" "2"]
    {:layout [[::c/monkey ::c/empty]
              [::c/monkey ::c/empty]]
     :inventory-size 3
     :turns 10}))

(deftest test-sleep-inactive-team
  (let [curr-state
        (-> state
            (c/run-commands [{:team-name "1" :command ::c/idle}]))]
    (are [x y] (= x y)
         {} (get-in curr-state [:teams "1" :buffs])
         {::c/asleep 1} (get-in curr-state [:teams "2" :buffs]))))

(deftest test-move-while-asleep
  (let [curr-state
        (-> state
            (c/run-commands [{:team-name "1" :command ::c/idle}])
            (c/run-commands [{:team-name "1" :command ::c/move :direction ::c/right}
                             {:team-name "2" :command ::c/move :direction ::c/right}]))]
    (are [x y] (= x y)
         {} (get-in curr-state [:teams "2" :buffs]) ; sleep buff removed
         [1 0] (get-in curr-state [:teams "2" :position]) ; could not move
         [0 1] (get-in curr-state [:teams "1" :position])))) ; could move

;(test-ns 'monkey-music.sleep-test)
