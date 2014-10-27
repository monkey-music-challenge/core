(ns monkey-music.test.states
  (:require-macros [cemerick.cljs.test :refer (deftest is)])
  (:require [cemerick.cljs.test]
            [monkey-music.rules :as rules]))

(deftest test-value-of-song
  (is (= 1 (rules/value-of :song))))

(deftest test-value-of-album
  (is (= 2 (rules/value-of :album))))

(deftest test-value-of-playlist
  (is (= 4 (rules/value-of :playlist))))

(deftest test-value-of-unknown
  (is (= 0 (rules/value-of :unknown-item))))

(deftest test-value-of-multiple
  (is (= (+ 1 2 4) (rules/value-of :song :album :playlist :unknown-item))))
