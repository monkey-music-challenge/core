(ns monkey-music.test.rules
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :as t]
            [monkey-music.rules :as rules]))

(deftest test-value-of
  (testing "song"
    (is (= 1 (rules/value-of :song))))
  (testing "album"
    (is (= 2 (rules/value-of :album))))
  (testing "playlist"
    (is (= 4 (rules/value-of :playlist))))
  (testing "unknown item"
    (is (= 0 (rules/value-of :unknown-item))))
  (testing "multiple items"
    (is (= (+ 1 2 4) (rules/value-of :song :album :playlist :unknown-item)))))

(deftest test-can-pick-up-more?
  (testing "true"
    (is (rules/can-pick-up-more?
          {:players {"p1" {:picked-up-items [:song]}}
           :pick-up-limit 2}
          "p1")))
  (testing "false"
    (is (not (rules/can-pick-up-more?
               {:players {"p1" {:picked-up-items [:song :album]}}
                :pick-up-limit 2}
               "p1")))))
  
(deftest test-game-over?
  (testing "true, because no remaining items"
    (is (rules/game-over?
          {:layout [[:empty :empty]
                    [:empty :monkey]]
           :players {"p1" {:picked-up-items []
                           :turns 3}}})))
  (testing "true, because no more turns"
    (is (rules/game-over?
          {:layout [[:empty :song]
                    [:empty :monkey]]
           :players {"p1" {:picked-up-items []
                           :turns 0}}})))
  (testing "false, because remaining items on layout"
    (is (not (rules/game-over?
               {:layout [[:empty :song]
                         [:empty :monkey]]
                :players {"p1" {:picked-up-items []
                                :turns 3}}}))))
  (testing "false, because remaining picked up items"
    (is (not (rules/game-over?
               {:layout [[:empty :empty]
                         [:empty :monkey]]
                :players {"p1" {:picked-up-items [:song]
                                :turns 3}}})))))

(t/test-ns 'monkey-music.test.rules)
