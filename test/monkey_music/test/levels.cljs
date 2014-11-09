(ns monkey-music.test.levels
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.levels :as levels]
            [monkey-music.units :as units]))

(deftest test-parse-layout
  (testing "valid layout"
    (is (= [[units/empty units/monkey]
            [units/song  units/playlist]]
           (levels/parse-layout
             [" M"
              "sp"]
             {" " "empty"
              "s" "song"
              "p" "playlist"
              "M" "monkey"}))))

  (testing "layout with unknown unit"
    (is (thrown? js/Error
                 (levels/parse-layout
                   ["? "
                    " M"]
                   {" " "empty"
                    "M" "monkey"})))))

(deftest test-parse
  (is (= {:layout [[units/empty units/monkey]
                   [units/song  units/album]]
          :pick-up-limit 3
          :turns 10}
         (levels/parse
           {"units" {" " "empty"
                     "M" "monkey"
                     "s" "song"
                     "a" "album"}
            "layout" [" M"
                      "sa"]
            "pickUpLimit" 3
            "turns" 10}))))

(deftest test-seed
  (is (= "monkeysongemptyplaylist"
         (levels/seed
           {:layout [[units/monkey units/song]
                     [units/empty units/playlist]]}))))

(test-ns 'monkey-music.test.levels)
