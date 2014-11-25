(ns monkey-music.parse-command-test
  (:require-macros [cemerick.cljs.test :refer (deftest is are testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.wrapper :as w]
            [monkey-music.core :as c]))

(def state
  (c/create-game-state
    ["1" "2"]
    {:layout [[::c/monkey ::c/empty]
              [::c/monkey ::c/empty]]
     :inventory-size 3
     :turns 10}))

(deftest test-parse-valid-command
  (is (= {:command ::c/move :directions [::c/left ::c/right] :team-name "1"}
         (w/parse-command state {"command" "move"
                                 "directions" ["left" "right"]
                                 "team" "1"}))))

(deftest test-parse-command-with-unknown-team
  (is (thrown? js/Error (w/parse-command state {"command" "move"
                                                "direction" "left"
                                                "team" "3"}))))

;(test-ns 'monkey-music.parse-command-test)
