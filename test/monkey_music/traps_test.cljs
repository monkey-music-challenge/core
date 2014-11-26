(ns monkey-music.traps-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing are)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

(def state
  (c/create-game-state
    ["1" "2"]
    {:layout [[::c/monkey ::c/trap]
              [::c/monkey ::c/song]]
     :inventory-size 3
     :turns 10}))

(deftest test-pick-up-trap
  (let [curr-state (-> state
                       (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}
                                        {:command ::c/idle :team-name "2"}]))]
    (is (= [::c/trap] (get-in curr-state [:teams "1" :inventory])))))

(deftest test-lay-trap
  (let [curr-state (-> state
                       (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}
                                        {:command ::c/idle :team-name "2"}])
                       (c/run-commands [{:command ::c/use :item ::c/trap :team-name "1"}
                                        {:command ::c/idle :team-name "2"}]))]
    (are [x y] (= x y)
         [] (get-in curr-state [:teams "1" :inventory])
         [[0 0]] (:trap-positions curr-state))))

(deftest test-arm-trap
  (let [curr-state 
        (-> state
            (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}
                             {:command ::c/idle :team-name "2"}])
            (c/run-commands [{:command ::c/use :item ::c/trap :team-name "1"}
                             {:command ::c/idle :team-name "2"}])
            (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}
                             {:command ::c/move :direction ::c/right :team-name "2"}]))]
    (are [x y] (= x y)
         [[0 0]] (:armed-trap-positions curr-state)
         [] (:trap-positions curr-state)
         [::c/song] (get-in curr-state [:teams "2" :inventory]))))

(deftest test-trigger-trap
  (let [curr-state
        (-> state
            (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}
                             {:command ::c/idle :team-name "2"}])
            (c/run-commands [{:command ::c/use :item ::c/trap :team-name "1"}
                             {:command ::c/idle :team-name "2"}])
            (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}
                             {:command ::c/move :direction ::c/right :team-name "2"}])
            (c/run-commands [{:command ::c/idle :team-name "1"}
                             {:command ::c/move :direction ::c/up :team-name "2"}]))]
    (are [x y] (= x y)
         {::c/trapped (c/duration-of ::c/trapped)} (get-in curr-state [:teams "2" :buffs])
         [] (:armed-trap-positions curr-state)
         [0 0] (get-in curr-state [:teams "2" :position])
         [] (get-in curr-state [:teams "2" :inventory]))))

(deftest test-trigger-trap-with-empty-inventory
  (let [curr-state
        (-> state
            (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}
                             {:command ::c/idle :team-name "2"}])
            (c/run-commands [{:command ::c/use :item ::c/trap :team-name "1"}
                             {:command ::c/idle :team-name "2"}])
            (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}
                             {:command ::c/idle :team-name "2"}])
            (c/run-commands [{:command ::c/idle :team-name "1"}
                             {:command ::c/move :direction ::c/up :team-name "2"}]))]
    (are [x y] (= x y)
         {::c/trapped (c/duration-of ::c/trapped)} (get-in curr-state [:teams "2" :buffs])
         [] (:armed-trap-positions curr-state)
         [0 0] (get-in curr-state [:teams "2" :position])
         [] (get-in curr-state [:teams "2" :inventory]))))

(test-ns 'monkey-music.traps-test)
