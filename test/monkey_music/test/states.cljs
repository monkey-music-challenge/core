(ns monkey-music.test.states
  (:require-macros [cemerick.cljs.test :refer (deftest is)])
  (:require [cemerick.cljs.test]
            [monkey-music.states :as states]))

(deftest test-create-players
  (is (= {"p1" {:score 0
                :picked-up-items []
                :turns 10
                :position [0 0]}
          "p2" {:score 0
                :picked-up-items []
                :turns 10
                :position [1 1]}}
         (states/create-players ["p1" "p2"] [[0 0] [1 1]] 10))))

(deftest test-create
  (is (= {:layout [[:monkey :empty]
                   [:song   :monkey]]
          :players {"p1" {:score 0
                          :picked-up-items []
                          :turns 10
                          :position [0 0]}
                    "p2" {:score 0
                          :picked-up-items []
                          :turns 10
                          :position [1 1]}}
          :pick-up-limit 3}
         (states/create ["p1" "p2"]
                        {:layout [[:monkey :empty]
                                  [:song   :monkey]]
                         :pick-up-limit 3
                         :turns 10}))))
