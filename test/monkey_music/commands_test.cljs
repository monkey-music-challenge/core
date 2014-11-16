(ns monkey-music.commands-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]
            [monkey-music.index :as i]
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

(def state (i/create-game-state
             ["glenn", "ada"]
             {"turns" 50
              "layout" ["M s#M "]
              "pickUpLimit" 3
              "units" {"M" "monkey"
                       "s" "song"
                       "#" "wall"
                       "p" "playlist"
                       " " "empty"}}))

(def cmd1 (i/parse-command {"command" "move" "direction" "left" "team" "glenn"}))
(def cmd2 (i/parse-command {"command" "move" "direction" "left" "team" "ada"}))

(i/run-commands state [cmd1 cmd2])

(deftest stuff
  (is (= (c/run-commands*))))


(with-redefs [monkey-music.random/create (constantly :mock)]
  (test-ns 'monkey-music.commands-test))
