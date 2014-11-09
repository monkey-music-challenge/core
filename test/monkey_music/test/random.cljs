(ns monkey-music.test.random
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.random :as random]))

(deftest test-create
  (testing "should take a seed and return a random number generator"
    (is (random/create "seed"))))

(deftest test-weighted-selection!
  (testing "should return one of the map keys"
    (is (#{:key1 :key2} (random/weighted-selection!
                          {:key1 0.5 :key2 0.5}
                          (random/create "seed")))))

  (testing "should return the same results for the same seed"
    (is (= (random/weighted-selection!
             {:key1 0.5 :key2 0.5}
             (random/create "seed"))
           (random/weighted-selection!
             {:key1 0.5 :key2 0.5}
             (random/create "seed"))))))

(deftest test-weighted-shuffle!
  (testing "should return all of the map keys"
    (is (= #{:key1 :key2}
           (into #{} (random/weighted-shuffle!
                       {:key1 0.5 :key2 0.5}
                       (random/create "seed"))))))
  
  (testing "should return the same results for the same seed"
    (is (= (random/weighted-shuffle!
            {:key1 0.5 :key2 0.5}
            (random/create "seed"))
           (random/weighted-shuffle!
             {:key1 0.5 :key2 0.5}
             (random/create "seed"))))))

;(test-ns 'monkey-music.test.random)
