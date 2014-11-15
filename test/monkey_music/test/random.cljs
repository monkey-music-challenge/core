(ns monkey-music.test.random
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.random :as random]))

(deftest test-create
  (testing "should take a seed and return a random number generator"
    (is (random/create "seed"))))

(deftest test-weighted-selection!
  (testing "should return one of the list items"
    (is (#{:item1 :item2} (random/weighted-selection! (random/create "seed")
                                                      [:item1 :item2] [1 1]))))

  (testing "should return the same results for the same seed"
    (is (= (random/weighted-selection! (random/create "seed")
                                       [:item1 :item2] [1 1])
           (random/weighted-selection! (random/create "seed")
                                       [:item1 :item2] [1 1])))))

(deftest test-dissoc-vec
  (is (= (random/dissoc-vec [0 1 2 3] 2) [0 1 3])))

(deftest test-weighted-shuffle!
  (testing "should return all of the map keys"
    (is (= #{:item1 :item2}
           (into #{} (random/weighted-shuffle! (random/create "seed")
                                               [:item1 :item2] [1 1])))))
  
  (testing "should return the same results for the same seed"
    (is (= (random/weighted-shuffle! (random/create "seed")
                                     [:item1 :item2] [1 1])
           (random/weighted-shuffle! (random/create "seed")
                                     [:item1 :item2] [1 1])))))

;(test-ns 'monkey-music.test.random)
