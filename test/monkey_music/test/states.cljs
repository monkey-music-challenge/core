(ns monkey-music.test.states
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.states :as states]
            [monkey-music.units :as units]
            [monkey-music.teams :as teams]
            [monkey-music.random :as random]
            [monkey-music.levels :as levels]))

(deftest test-create
  (is (= (dissoc
           {:layout [[units/monkey units/empty]
                   [units/song   units/monkey]]
            :teams {"p1" (teams/create [0 0])
                    "p2" (teams/create [1 1])}
            :random (random/create "monkeyemptysongmonkey")
            :pick-up-limit 3
            :remaining-turns 10}
           :random)
         (dissoc
           (states/create ["p1" "p2"]
                          {:layout [[units/monkey units/empty]
                                    [units/song   units/monkey]]
                           :pick-up-limit 3
                           :turns 10})
           :random))))

(deftest test-all-units
  (is (= [units/empty units/song units/monkey units/playlist]
         (states/all-units
           {:layout [[units/empty units/song]
                     [units/monkey units/playlist]]}))))

(deftest test-all-picked-up-items
  (testing "one picked up item"
    (is (= [units/song] 
           (states/all-picked-up-items
             {:teams {"p1" {:picked-up-items []}
                     "p2" {:picked-up-items [units/song]}}}))))
  (testing "many picked up items"
    (is (= [units/song units/playlist]
           (states/all-picked-up-items
             {:teams {"p1" {:picked-up-items [units/song]}
                      "p2" {:picked-up-items [units/playlist]}}}))))
  (testing "no picked up items"
    (is (= [] 
           (states/all-picked-up-items
             {:teams {"p1" {:picked-up-items []}
                      "p2" {:picked-up-items []}}})))))

(test-ns 'monkey-music.test.states)
