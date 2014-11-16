(ns monkey-music.commands-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]
            [monkey-music.random :as r]))

(deftest test-run-commands*
  (is (= (c/run-commands*
           {:layout [[::c/monkey ::c/empty ::c/empty ::c/empty]]
            :original-layout [[::c/empty ::c/empty ::c/empty ::c/empty]]
            :teams {"1" {:position [0 0]}}}
           [{:command-name "move" :team-name "1" :direction :right}
            {:command-name "move" :team-name "1" :direction :right}
            {:command-name "move" :team-name "1" :direction :right}])
         1)))

(test-ns 'monkey-music.commands-test)
