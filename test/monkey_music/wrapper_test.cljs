(ns monkey-music.wrapper-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.wrapper :as w]
            [monkey-music.core :as c]))

(deftest test-json->unit
  (is (= ::c/monkey (w/json->unit {"m" "monkey"} "m")))
  (is (thrown? js/Error (w/json->unit {"m" "monkey"} "?"))))

(deftest test-json->layout
  (is (= [[::c/empty ::c/monkey]
          [::c/song ::c/user]]
         (w/json->layout [" m"
                          "su"]
                         {"m" "monkey"
                          "s" "song"
                          "u" "user"
                          " " "empty"}))))

(deftest test-json->level
  (is (= (w/json->level {"units" {"m" "monkey"
                                  "s" "song"
                                  "u" "user"
                                  " " "empty"}
                         "layout" [" m"
                                   "su"]
                         "turns" 10
                         "pickUpLimit" 3})
         {:layout [[::c/empty ::c/monkey]
                   [::c/song ::c/user]]
          :pick-up-limit 3
          :turns 10})))

(deftest test-json->command
  (is (= (w/json->command {"command" "move" "team" "1" "direction" "left"})
         {:command-name "move" :team-name "1" :direction ::c/left}))
  (is (= (w/json->command {"command" "use" "team" "1" "item" "banana"})
         {:command-name "use" :team-name "1" :item ::c/banana}))
  (is (thrown? js/Error (w/json->command {"command" "use" "team" "1" "item" "derp"}))))

(deftest test-team->json
  (is (= (w/team->json {:buffs {::c/speedy 2}
                        :position [1 1]
                        :picked-up-items [::c/song]
                        :score 0})
         {"buffs" {"speedy" 2}
          "position" [1 1]
          "pickedUpItems" ["song"]
          "score" 0})))

(deftest test-layout->json
  (is (= (w/layout->json [[::c/monkey ::c/song]
                          [::c/album ::c/empty]])
         [["monkey" "song"]
          ["album" "empty"]])))

(deftest test-game-state->json-for-team
  (is (= (w/game-state->json-for-team
           {:layout [[::c/song ::c/monkey ::c/empty]]
            :pick-up-limit 3
            :remaining-turns 5
            :teams {"1" {:buffs {::c/speedy 2}
                         :position [1 1]
                         :picked-up-items [::c/song]
                         :score 0}}}
           "1")
         {"layout" [["song" "monkey" "empty"]]
          "pickUpLimit" 3
          "remainingTurns" 5
          "buffs" {"speedy" 2}
          "pickedUpItems" ["song"]
          "position" [1 1]
          "score" 0})))

(test-ns 'monkey-music.wrapper-test)
