(ns monkey-music.commands-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.index :as i]))

(def left-1 (i/parse-command {"command" "move" "team" "1" "direction" "left"}))
(def right-1 (i/parse-command {"command" "move" "team" "1" "direction" "right"}))
(def up-1 (i/parse-command {"command" "move" "team" "1" "direction" "up"}))
(def down-1 (i/parse-command {"command" "move" "team" "1" "direction" "down"}))

(def level
  {"layout" ["m  #b"
             "##   "
             "pa us"]
   "units" {"m" "monkey"
            "#" "wall"
            " " "empty"
            "p" "playlist"
            "a" "album"
            "u" "user"
            "s" "song"}})

(test-ns 'monkey-music.commands-test)
