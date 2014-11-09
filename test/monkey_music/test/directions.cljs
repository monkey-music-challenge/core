(ns monkey-music.test.directions
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.directions :as directions]))

(deftest test-parse
  (testing "up"
    (is (= directions/up (directions/parse "up"))))
  (testing "down"
    (is (= directions/down (directions/parse "down"))))
  (testing "left"
    (is (= directions/left (directions/parse "left"))))
  (testing "right"
    (is (= directions/right (directions/parse "right"))))
  (testing "wrong input"
    (is (thrown? js/Error
                 (directions/parse "nope")))))

(test-ns 'monkey-music.test.directions)
