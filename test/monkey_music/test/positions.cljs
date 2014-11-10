(ns monkey-music.test.positions
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.positions :as positions]
            [monkey-music.units :as units]
            [monkey-music.directions :as directions]))

(deftest test-find-monkeys
  (testing "one monkey"
    (is (= [[1 1]]
           (positions/find-monkeys [[units/empty units/empty]
                                    [units/empty units/monkey]]))))
  (testing "many monkeys"
    (is (= [[0 0] [0 1] [1 1]]
           (positions/find-monkeys [[units/monkey units/monkey]
                                    [units/empty  units/monkey]]))))
  (testing "no players"
    (is (= []
           (positions/find-monkeys [[units/empty units/empty]
                                    [units/empty units/empty]])))))

(deftest test-translate
  (testing "up"
    (is (= [0 1] (positions/translate [1 1] directions/up))))
  (testing "down"
    (is (= [2 1] (positions/translate [1 1] directions/down))))
  (testing "left"
    (is (= [1 0] (positions/translate [1 1] directions/left))))
  (testing "right"
    (is (= [1 2] (positions/translate [1 1] directions/right))))
  (testing "string direction"
    (is (thrown? js/Error (positions/translate [1 1] "left")))))

;(test-ns 'monkey-music.test.positions)
