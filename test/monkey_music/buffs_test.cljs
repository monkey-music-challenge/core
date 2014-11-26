(ns monkey-music.buffs-test
  (:require-macros [cemerick.cljs.test :refer (deftest is are testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

(def state
  (c/create-game-state
    ["1" "2" "3" "4" "5" "6"]
    {:layout [[::c/monkey ::c/monkey ::c/monkey]
              [::c/monkey ::c/monkey ::c/monkey]]
     :inventory-size 3
     :turns 10}))

(deftest test-add-buff
  (testing "speedy"
    (let [curr-state (c/add-buff state "1" ::c/speedy)]
      (is (= {::c/speedy (inc (c/duration-of ::c/speedy))}
             (get-in curr-state [:teams "1" :buffs]))))))

(deftest test-tick-all-buffs
  (testing "tick all buffs"
    (let [curr-state
          (-> state
              (c/add-buff "1" ::c/speedy)
              (c/add-buff "2" ::c/trapped)
              (c/tick-all-buffs))]
      (are [x y] (= x y)
           {::c/speedy (c/duration-of ::c/speedy)} (get-in curr-state [:teams "1" :buffs])
           {::c/trapped (c/duration-of ::c/trapped)} (get-in curr-state [:teams "2" :buffs])))))

(deftest test-tick-all-buffs-and-remove
  (testing "tick all buffs"
    (let [curr-state
          (-> state
              (c/add-buff "1" ::c/speedy)
              (c/add-buff "2" ::c/trapped)
              (c/tick-all-buffs)
              (c/tick-all-buffs)
              (c/tick-all-buffs)
              (c/tick-all-buffs)
              (c/tick-all-buffs)
              (c/tick-all-buffs)
              (c/tick-all-buffs)
              (c/tick-all-buffs)
              (c/tick-all-buffs))]
      (are [x y] (= x y)
           {} (get-in curr-state [:teams "1" :buffs])
           {} (get-in curr-state [:teams "2" :buffs])))))

(deftest test-apply-all-buffs
  (testing "apply multiple buffs"
    (let [curr-state
          (-> state
              (c/add-buff "1" ::c/speedy)
              (c/add-buff "3" ::c/trapped)
              (c/add-buff "4" ::c/tackled))]
      (are [x y] (= x y)
           [{:command ::c/move :team-name "1" :direction ::c/left}
            {:command ::c/move :team-name "1" :direction ::c/down}
            {:command ::c/move :team-name "1" :direction ::c/left}
            {:command ::c/move :team-name "6" :direction ::c/up}]
           (c/apply-all-buffs
             curr-state
             [{:command ::c/move :team-name "1" :directions [::c/left ::c/down]}
              {:command ::c/move :team-name "1" :direction ::c/left}
              {:command ::c/move :team-name "3" :direction ::c/right}
              {:command ::c/move :team-name "4" :direction ::c/right}
              {:command ::c/move :team-name "6" :direction ::c/up}])))))

;(test-ns 'monkey-music.buffs-test)
