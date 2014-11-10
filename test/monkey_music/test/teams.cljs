(ns monkey-music.test.teams
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.teams :as teams]
            [monkey-music.buffs :as buffs]))

(deftest test-create
  (testing "create team"
    (is (= {:position [0 0]
            :buffs (buffs/create)
            :picked-up-items []
            :score 0}
           (teams/create [0 0])))))

(deftest test-create-all
  (testing "create multiple teams"
    (is (= {"team1" {:position [0 0]
                     :buffs (buffs/create)
                     :picked-up-items []
                     :score 0}
            "team17" {:position [1 1]
                      :buffs (buffs/create)
                      :picked-up-items []
                      :score 0}}
           (teams/create-all ["team1" "team17"]
                             [[0 0] [1 1]]))))
  
  (testing "create duplicate teams"
    (is (thrown? js/Error (teams/create-all ["team1" "team17" "team1"]
                                            [[0 0] [1 1] [2 2]])))))

;(test-ns 'monkey-music.test.teams)
