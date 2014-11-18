(ns monkey-music.doors-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

(deftest move-to-lever
  (let [state {:teams {"1" {:position [0 0]}}
               :layout [[::c/monkey ::c/lever]
                        [::c/open-door ::c/closed-door]]
               :base-layout [[::c/open-door ::c/lever]
                             [::c/open-door ::c/closed-door]]}
        command {:command ::c/move :team-name "1" :direction ::c/right}]
    (is (= (c/run-command state command)
           {:teams {"1" {:position [0 0]}}
            :layout [[::c/monkey ::c/lever]
                     [::c/closed-door ::c/open-door]]
            :base-layout [[::c/closed-door ::c/lever]
                          [::c/closed-door ::c/open-door]]}))))

(deftest move-to-open-door
  (let [state {:teams {"1" {:position [0 0]}}
               :layout [[::c/monkey ::c/open-door]
                        [::c/closed-door ::c/empty]]
               :base-layout [[::c/empty ::c/open-door]
                             [::c/closed-door ::c/empty]]}
        move-left {:command ::c/move :team-name "1" :direction ::c/left}
        move-right {:command ::c/move :team-name "1" :direction ::c/right}
        move-down {:command ::c/move :team-name "1" :direction ::c/down}]

    (testing "move to open door"
      (is (= (c/run-command state move-right)
           {:teams {"1" {:position [0 1]}}
            :layout [[::c/empty ::c/monkey]
                     [::c/closed-door ::c/empty]]
            :base-layout [[::c/empty ::c/open-door]
                          [::c/closed-door ::c/empty]]})))

    (testing "move to and from open door"
      (is (= (-> state
                 (c/run-command move-right)
                 (c/run-command move-left))
           {:teams {"1" {:position [0 0]}}
            :layout [[::c/monkey ::c/open-door]
                     [::c/closed-door ::c/empty]]
            :base-layout [[::c/empty ::c/open-door]
                          [::c/closed-door ::c/empty]]})))

    (testing "move to closed door"
      (is (= (c/run-command state move-down)
           {:teams {"1" {:position [0 0]}}
            :layout [[::c/monkey ::c/open-door]
                     [::c/closed-door ::c/empty]]
            :base-layout [[::c/empty ::c/open-door]
                          [::c/closed-door ::c/empty]]})))))

(test-ns 'monkey-music.doors-test)
