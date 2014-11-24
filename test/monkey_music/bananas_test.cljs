(ns monkey-music.bananas-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

;; TODO

(deftest test-remove-one
  (is (= (c/remove-one [1 2 3 4 1 2 3 4] 3) [1 2 4 1 2 3 4]))
  (is (= (c/remove-one [1 2 1 2] 3) [1 2 1 2]))
  (is (= (c/remove-one [] 3) [])))

(def state
  (c/create-game-state
    ["1"]
    {:layout [[::c/monkey ::c/banana ::c/empty ::c/song]
             [::c/user ::c/album ::c/empty ::c/wall]]
     :inventory-size 3
     :turns 10}))

(deftest test-weighted-shuffle
  (is (= [:test] (c/weighted-shuffle! state [:test]))))

(deftest pick-up-banana
  (let [curr-state (-> state
                       (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}]))]
    (is (= [::c/banana] (get-in curr-state [:teams "1" :inventory])))))

(deftest use-banana
  (let [curr-state (-> state
                       (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}])
                       (c/run-commands [{:command ::c/use :item ::c/banana :team-name "1"}]))]
    (is (= {::c/speedy 3} (get-in curr-state [:teams "1" :buffs])))))

(deftest move-speedily
  (let [curr-state (-> state
                       (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}])
                       (c/run-commands [{:command ::c/use :item ::c/banana :team-name "1"}])
                       (c/run-commands [{:command ::c/move :directions [::c/right ::c/right ::c/right] :team-name "1"}]))]
    (is (= [0 2] (get-in curr-state [:teams "1" :position])))))

(deftest move-speedily-thrice-then-buff-disappears
  (let [curr-state (-> state
                       (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}])
                       (c/run-commands [{:command ::c/use :item ::c/banana :team-name "1"}])
                       (c/run-commands [{:command ::c/move :directions [::c/right ::c/right] :team-name "1"}])
                       (c/run-commands [{:command ::c/move :directions [::c/right ::c/down] :team-name "1"}])
                       (c/run-commands [{:command ::c/move :directions [::c/left ::c/left] :team-name "1"}]))]
    (is (= [::c/song ::c/album] (get-in curr-state [:teams "1" :inventory])))
    (is (= [1 1] (get-in curr-state [:teams "1" :position])))
    (is (= {} (get-in curr-state [:teams "1" :buffs])))))

(test-ns 'monkey-music.bananas-test)
