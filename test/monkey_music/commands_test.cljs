(ns monkey-music.commands-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]
            [monkey-music.wrapper :as w]
            [monkey-music.random :as r]))

(deftest test-run-commands*
  (is (w/game-state->json-for-team
        (-> (c/create-game-state
            ["1"]
            {:layout [[::c/monkey ::c/empty ::c/empty ::c/empty]]
             :pick-up-limit 10
             :turns 5})
            (c/run-commands* [{:command-name "move" :team-name "1" :direction :right}
                              {:command-name "move" :team-name "1" :direction :right}
                              {:command-name "move" :team-name "1" :direction :right}]))
        "1"))

(deftest stuff
  (is (= (c/run-commands*))))

(test-ns 'monkey-music.commands-test)
