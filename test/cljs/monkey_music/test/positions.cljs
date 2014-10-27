(ns monkey-music.test.positions
  (:require-macros [cemerick.cljs.test :refer (deftest is)])
  (:require [cemerick.cljs.test]
            [monkey-music.positions :as positions]))

(deftest test-find-players
  (is (= [[1 1]]
         (positions/find-players [[:empty :empty]
                                  [:empty :monkey]]))))

(deftest test-find-players-many
  (is (= [[0 0] [0 1] [1 1]]
         (positions/find-players [[:monkey :monkey]
                                  [:empty  :monkey]]))))

(deftest test-find-players-none
  (is (= []
         (positions/find-players [[:empty :empty]
                                  [:empty :empty]]))))

(deftest test-translate-up
  (is (= [0 1] (positions/translate [1 1] :up))))

(deftest test-translate-down
  (is (= [2 1] (positions/translate [1 1] :down))))

(deftest test-translate-left
  (is (= [1 0] (positions/translate [1 1] :left))))

(deftest test-translate-right
  (is (= [1 2] (positions/translate [1 1] :right))))

(deftest test-translate-unknown-direction
  (is (thrown? js/Error (positions/translate [1 1] :not-a-direction))))

(deftest test-translate-string-direction
  (is (thrown? js/Error (positions/translate [1 1] "left"))))
