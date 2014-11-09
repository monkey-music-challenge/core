(ns monkey-music.test.buffs
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.buffs :as buffs]))

(deftest test-tick
  (testing "tick one buff"
    (is (= {buffs/speedy {:remaining-turns 1}}
           (buffs/tick {buffs/speedy {:remaining-turns 2}}))))

  (testing "tick all buffs"
    (is (= {buffs/speedy {:remaining-turns 1}
            buffs/tackled {:remaining-turns 2}}
           (buffs/tick {buffs/speedy {:remaining-turns 2}
                        buffs/tackled {:remaining-turns 3}}))))

  (testing "tick and remove one expired buff"
    (is (= {buffs/speedy {:remaining-turns 1}}
           (buffs/tick {buffs/taclked {:remaining-turns 1}
                        buffs/speedy {:remaining-turns 2}})))))

(deftest test-add
  (testing "add speedy"
    (is (= {buffs/speedy {:remaining-turns (inc (buffs/duration buffs/speedy))}}
           (buffs/add buffs/speedy {}))))
  (testing "add tackled"
    (is (= {buffs/tackled {:remaining-turns (inc (buffs/duration buffs/tackled))}}
           (buffs/add buffs/tackled {})))))

(deftest test-has?
  (testing "speedy"
    (is (buffs/has? {buffs/speedy {:remaining-turns 2}}
                    buffs/speedy))))

(deftest test-create
  (testing "create buffs container"
    (is (= {} (buffs/create)))))

;(test-ns 'monkey-music.test.buffs)
