(ns monkey-music.traps-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

(def state
  (c/create-game-state
    ["1" "2"]
    {:layout [[::c/monkey ::c/trap]
              [::c/monkey ::c/empty]]
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
    (is (= [] (get-in curr-state [:teams "1" :inventory])))
    (is (= [[0 0]] (:trap-positions curr-state)))))

(deftest test-arm-trap
  (let [curr-state (-> state
                       (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}
                                        {:command ::c/idle :team-name "2"}])
                       (c/run-commands [{:command ::c/use :item ::c/trap :team-name "1"}
                                        {:command ::c/idle :team-name "2"}])
                       (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}
                                        {:command ::c/idle :team-name "2"}]))]
    (is (= [[0 0]] (:armed-trap-positions curr-state)))
    (is (= [] (:trap-positions curr-state)))))

(deftest test-trigger-trap
  (let [curr-state (-> state
                       (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}
                                        {:command ::c/idle :team-name "2"}])
                       (c/run-commands [{:command ::c/use :item ::c/trap :team-name "1"}
                                        {:command ::c/idle :team-name "2"}])
                       (c/run-commands [{:command ::c/move :direction ::c/right :team-name "1"}
                                        {:command ::c/idle :team-name "2"}])
                       (c/run-commands [{:command ::c/idle :team-name "1"}
                                        {:command ::c/move :direction ::c/up :team-name "2"}]))]
    (is (= {::c/trapped 1} (get-in curr-state [:teams "2" :buffs])))
    (is (= [] (:armed-trap-positions curr-state)))
    (is (= [0 0] (get-in curr-state [:teams "2" :position])))))

(test-ns 'monkey-music.trap-test)
