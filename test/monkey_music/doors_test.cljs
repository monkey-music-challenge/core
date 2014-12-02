(ns monkey-music.doors-test
  (:require-macros [cemerick.cljs.test :refer (deftest is are testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

(def state
  (c/create-game-state
    ["1"]
    {:layout [[::c/monkey ::c/lever ::c/song]
              [::c/open-door ::c/closed-door ::c/song]]
     :turns 10
     :inventory-size 3}))

(deftest test-move-to-lever
  (let [curr-state
        (-> state
            (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}]))]
    (are [x y] (= x y)
         [0 0] (get-in curr-state [:teams "1" :position])
         [[::c/monkey ::c/lever ::c/song]
          [::c/closed-door ::c/open-door ::c/song]] (:layout curr-state))))

(deftest test-move-to-open-door
  (let [curr-state
        (-> state
            (c/run-commands [{:command ::c/move :direction ::c/down :team-name "1"}]))]
    (are [x y] (= x y)
         [1 0] (get-in curr-state [:teams "1" :position])
         [[::c/empty ::c/lever ::c/song]
          [::c/monkey ::c/closed-door ::c/song]] (:layout curr-state))))

(deftest test-move-to-and-from-open-door
  (let [curr-state
        (-> state
            (c/run-commands [{:command ::c/move :direction ::c/down :team-name "1"}])
            (c/run-commands [{:command ::c/move :direction ::c/up :team-name "1"}]))]
    (are [x y] (= x y)
         [0 0] (get-in curr-state [:teams "1" :position])
         [[::c/monkey ::c/lever ::c/song]
          [::c/open-door ::c/closed-door ::c/song]] (:layout curr-state))))

(deftest test-move-to-closed-door
  (let [curr-state
        (-> state
            (c/run-commands [{:command ::c/move :direction ::c/down :team-name "1"}])
            (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}]))]
    (are [x y] (= x y)
         [1 0] (get-in curr-state [:teams "1" :position])
         [[::c/empty ::c/lever ::c/song]
          [::c/monkey ::c/closed-door ::c/song]] (:layout curr-state))))

;(test-ns 'monkey-music.doors-test)
