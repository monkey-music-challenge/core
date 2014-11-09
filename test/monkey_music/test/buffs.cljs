(ns monkey-music.test.buffs
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.buffs :as buffs]))

(deftest test-tick
  (testing "tick one buff"
    (is (= {buffs/speed {:remaining-turns 1}}
           (buffs/tick {buffs/speed {:remaining-turns 2}}))))
  (testing "tick and remove one expired buff"
    (is (= {}
           (buffs/tick {buffs/speed {:remaining-turns 1}})))))

(deftest test-add
  (testing "add speed"
    (is (= {buffs/speed {:remaining-turns (inc (buffs/duration buffs/speed))}}
           (buffs/add buffs/speed {})))))

(deftest test-has?
  (testing "speed"
    (is (buffs/has? {buffs/speed {:remaining-turns 2}}
                    buffs/speed))))

(deftest test-create
  (testing "create buffs container"
    (is (= {} (buffs/create)))))

;(test-ns 'monkey-music.test.buffs)
