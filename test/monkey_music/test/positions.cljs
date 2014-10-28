(ns monkey-music.test.positions
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.positions :as positions]))

(deftest test-find-players
  (testing "one player"
    (is (= [[1 1]]
           (positions/find-players [[:empty :empty]
                                    [:empty :monkey]]))))
  (testing "many players"
    (is (= [[0 0] [0 1] [1 1]]
           (positions/find-players [[:monkey :monkey]
                                    [:empty  :monkey]]))))
  (testing "no players"
    (is (= []
           (positions/find-players [[:empty :empty]
                                    [:empty :empty]])))))

(deftest test-translate
  (testing "up"
    (is (= [0 1] (positions/translate [1 1] :up))))
  (testing "down"
    (is (= [2 1] (positions/translate [1 1] :down))))
  (testing "left"
    (is (= [1 0] (positions/translate [1 1] :left))))
  (testing "right"
    (is (= [1 2] (positions/translate [1 1] :right))))
  (testing "unknown direction"
    (is (thrown? js/Error (positions/translate [1 1] :not-a-direction))))
  (testing "string direction"
    (is (thrown? js/Error (positions/translate [1 1] "left")))))
