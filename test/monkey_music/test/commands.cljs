(ns monkey-music.test.commands
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.commands :as commands]
            [monkey-music.directions :as directions]
            [monkey-music.units :as units]))

(deftest test-parse
  (testing "invalid command"
    (is (thrown? js/Error
                 (commands/parse {"command" "nope"})))))

(deftest test-parse-move
    (is (= {:command commands/move
            :direction directions/left}
           (commands/parse {"team" "team1"
                            "command" "move"
                            "direction" "left"})))

    (testing "missing team"
      (is (thrown? js/Error 
                   (commands/parse {"command" "move"
                                    "direction" "left"}))))

    (testing "invalid direction"
      (is (thrown? js/Error 
                   (commands/parse {"command" "move"
                                    "direction" "nope"})))))

(deftest test-parse-use
    (is (= {:command commands/use
            :item units/banana}
           (commands/parse {"team" "team1"
                            "command" "use"
                            "item" "banana"})))

    (testing "invalid item"
      (is (thrown? js/Error 
                   (commands/parse {"command" "use"
                                    "item" "nope"})))))

(test-ns 'monkey-music.test.commands)
