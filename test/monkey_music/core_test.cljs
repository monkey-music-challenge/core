(ns monkey-music.core-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

(deftest test-move-to-empty
  (is (= {:layout [[::c/empty ::c/monkey]]
          :teams {"1" {:position [0 1]}}} 
         (c/run-command
           {:layout [[::c/monkey ::c/empty]]
            :teams {"1" {:position [0 0]}}}
           {:team-name "1" :command-name "move" :direction :right}))))

(test-ns 'monkey-music.core-test)
