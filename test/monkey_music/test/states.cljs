(ns monkey-music.test.states
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :as t]
            [monkey-music.states :as states]))

(deftest test-create-players
  (is (= {"p1" {:score 0
                :picked-up-items []
                :remaining-turns 10
                :position [0 0]}
          "p2" {:score 0
                :picked-up-items []
                :remaining-turns 10
                :position [1 1]}}
         (states/create-players ["p1" "p2"] [[0 0] [1 1]] 10))))

(deftest test-create
  (is (= {:layout [[:monkey :empty]
                   [:song   :monkey]]
          :players {"p1" {:score 0
                          :picked-up-items []
                          :remaining-turns 10
                          :position [0 0]}
                    "p2" {:score 0
                          :picked-up-items []
                          :remaining-turns 10
                          :position [1 1]}}
          :pick-up-limit 3}
         (states/create ["p1" "p2"]
                        {:layout [[:monkey :empty]
                                  [:song   :monkey]]
                         :pick-up-limit 3
                         :turns 10}))))

(deftest test-all-units
  (is (= [:empty :song :monkey :playlist]
         (states/all-units
           {:layout [[:empty :song]
                     [:monkey :playlist]]}))))

(deftest test-all-picked-up-items
  (testing "one picked up item"
    (is (= [:song] 
           (states/all-picked-up-items
             {:players {"p1" {:picked-up-items []}
                        "p2" {:picked-up-items [:song]}}}))))
  (testing "many picked up items"
    (is (= [:song :playlist]
           (states/all-picked-up-items
             {:players {"p1" {:picked-up-items [:song]}
                        "p2" {:picked-up-items [:playlist]}}}))))
  (testing "no picked up items"
    (is (= [] 
           (states/all-picked-up-items
             {:players {"p1" {:picked-up-items []}
                        "p2" {:picked-up-items []}}})))))

(deftest total-remaining-turns
  (testing "remaining turns"
    (is (= 3
           (states/total-remaining-turns
             {:players {"p1" {:remaining-turns 1}
                        "p2" {:remaining-turns 2}}}))))
  (testing "no remaining turns"
    (is (= 0
           (states/total-remaining-turns
             {:players {"p1" {:remaining-turns 0}
                        "p2" {:remaining-turns 0}}})))))

;(test-ns 'monkey-music.test.states)
