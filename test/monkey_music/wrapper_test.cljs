(ns monkey-music.wrapper-test
  (:require-macros [cemerick.cljs.test :refer (deftest is are testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.wrapper :as w]
            [monkey-music.core :as c]))

(deftest test-str->entity
  (are [x y] (= x y)
       ::c/monkey (w/str->entity ::c/layoutable "monkey")
       ::c/left (w/str->entity ::c/direction "left")
       ::c/use (w/str->entity ::c/command "use")))

(deftest test-str->entity-throw
  (is (thrown? js/Error (w/str->entity ::c/layoutable "derp"))))

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
  (is (= (w/json->level {"legend" {"m" "monkey"
                                   "s" "song"
                                   "u" "user"
                                   " " "empty"}
                         "layout" [" m"
                                   "su"]
                         "turns" 10
                         "inventorySize" 3})
         {:layout [[::c/empty ::c/monkey]
                   [::c/song ::c/user]]
          :inventory-size 3
          :turns 10})))

(deftest test-json->command
  (are [x y] (= x y)
       {:command ::c/move :team-name "1" :direction ::c/left}
       (w/json->command {"command" "move" "team" "1" "direction" "left"})
       {:command ::c/use :team-name "1" :item ::c/banana}
       (w/json->command {"command" "use" "team" "1" "item" "banana"})
       {:command ::c/move :team-name "1" :directions [::c/left ::c/right]}
       (w/json->command {"command" "move" "team" "1" "directions" ["left" "right"]})))

(deftest test-json->command-throw
  (is (thrown? js/Error
               (w/json->command {"command" "use" "team" "1" "item" "derp"}))))

(deftest test-team->json
  (is (= (w/team->json {:buffs {::c/speedy 2}
                        :position [1 1]
                        :inventory [::c/song]
                        :score 0})
         {"buffs" {"speedy" 2}
          "position" [1 1]
          "inventory" ["song"]
          "score" 0})))

(deftest test-layout->json
  (is (= (w/layout->json [[::c/monkey ::c/song]
                          [::c/album ::c/empty]])
         [["monkey" "song"]
          ["album" "empty"]])))

(deftest test-game-state->json-for-team
  (is (= (w/game-state->json-for-team
           {:layout [[::c/song ::c/monkey ::c/empty]]
            :inventory-size 3
            :remaining-turns 5
            :teams {"1" {:buffs {::c/speedy 2}
                         :position [1 1]
                         :inventory [::c/song]
                         :score 0}}}
           "1")
         {"layout" [["song" "monkey" "empty"]]
          "inventorySize" 3
          "remainingTurns" 5
          "buffs" {"speedy" 2}
          "isGameOver" false
          "inventory" ["song"]
          "position" [1 1]
          "score" 0})))

;(test-ns 'monkey-music.wrapper-test)
