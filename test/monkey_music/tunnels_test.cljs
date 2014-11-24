(ns monkey-music.tunnels-test
  (:require-macros [cemerick.cljs.test :refer (deftest is are testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

(def state
  (c/create-game-state
    ["1"]
    {:layout [[::c/monkey ::c/tunnel-1 ::c/tunnel-2]
              [::c/tunnel-2 ::c/empty ::c/tunnel-1]]
     :turns 10
     :inventory-size 3}))

(deftest test-enter-tunnel-1
  (let [curr-state
        (-> state
            (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}]))]
    (are [x y] (= x y)
         [1 2] (get-in curr-state [:teams "1" :position]))))

(deftest test-enter-tunnel-2
  (let [curr-state
        (-> state
            (c/run-commands [{:command ::c/move :direction ::c/down :team-name "1"}]))]
    (are [x y] (= x y)
         [0 2] (get-in curr-state [:teams "1" :position]))))

(deftest test-enter-lots-of-tunnels
  (let [curr-state
        (-> state
            (c/run-commands [{:command ::c/move :direction ::c/down :team-name "1"}])
            (c/run-commands [{:command ::c/move :direction ::c/down :team-name "1"}])
            (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}]))]
    (are [x y] (= x y)
         [1 0] (get-in curr-state [:teams "1" :position])
         [[::c/empty ::c/tunnel-1 ::c/tunnel-2]
          [::c/monkey ::c/empty ::c/tunnel-1]] (:layout curr-state))))

;(test-ns 'monkey-music.tunnels-test)
