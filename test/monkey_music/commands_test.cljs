(ns monkey-music.commands-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.index :as i]))

(def l (i/parse-command {} {"command" "move" "team" "1" "direction" "left"}))
(def r (i/parse-command {} {"command" "move" "team" "1" "direction" "right"}))
(def u (i/parse-command {} {"command" "move" "team" "1" "direction" "up"}))
(def d (i/parse-command {} {"command" "move" "team" "1" "direction" "down"}))
(def ll (i/parse-command {} {"command" "move" "team" "1" "directions" ["left" "left"]}))

(def use-banana (i/parse-command {} {"command" "use" "team" "1" "item" "banana"}))

(def level
  {"layout" ["m  #b"
             "##   "
             "pa us"]
   "legend" {"m" "monkey"
             "#" "wall"
             " " "empty"
             "b" "banana"
             "p" "playlist"
             "a" "album"
             "u" "user"
             "s" "song"}
   "inventorySize" 3
   "turns" 20})

(def state (i/create-game-state ["1"] level))

(-> state
    (i/run-commands [r r d r r u use-banana]))
    ;(i/run-commands [ll]))

(test-ns 'monkey-music.commands-test)
