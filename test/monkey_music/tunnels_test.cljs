(ns monkey-music.tunnels-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

;; TODO

(deftest test-tunnels
  (let [base-layout [[::c/empty ::c/tunnel-1 ::c/tunnel-2]
                     [::c/tunnel-2 ::c/empty ::c/tunnel-1]]
        state {:teams {"1" {:position [0 0]}}
               :base-layout [[::c/empty ::c/tunnel-1 ::c/tunnel-2]
                             [::c/tunnel-2 ::c/empty ::c/tunnel-1]]
               :layout [[::c/monkey ::c/tunnel-1 ::c/tunnel-2]
                        [::c/tunnel-2 ::c/empty ::c/tunnel-1]]}
        left {:command ::c/move :team-name "1" :direction ::c/left}
        right {:command ::c/move :team-name "1" :direction ::c/right}
        down {:command ::c/move :team-name "1" :direction ::c/down}
        up {:command ::c/move :team-name "1" :direction ::c/up}]
    
  (testing "enter tunnel 1"
    (is (= (c/run-command state right)
           {:teams {"1" {:position [1 2]}}
            :base-layout [[::c/empty ::c/tunnel-1 ::c/tunnel-2]
                          [::c/tunnel-2 ::c/empty ::c/tunnel-1]]
            :layout [[::c/empty ::c/tunnel-1 ::c/tunnel-2]
                     [::c/tunnel-2 ::c/empty ::c/monkey]]})))

  (testing "enter tunnel 2"
    (is (= (c/run-command state down)
           {:teams {"1" {:position [0 2]}}
            :base-layout [[::c/empty ::c/tunnel-1 ::c/tunnel-2]
                          [::c/tunnel-2 ::c/empty ::c/tunnel-1]]
            :layout [[::c/empty ::c/tunnel-1 ::c/monkey]
                     [::c/tunnel-2 ::c/empty ::c/tunnel-1]]})))

  (testing "enter lots of tunnels"
    (is (= (-> state
               (c/run-command down)
               (c/run-command down)
               (c/run-command right))
           {:teams {"1" {:position [1 0]}}
            :base-layout [[::c/empty ::c/tunnel-1 ::c/tunnel-2]
                          [::c/tunnel-2 ::c/empty ::c/tunnel-1]]
            :layout [[::c/empty ::c/tunnel-1 ::c/tunnel-2]
                     [::c/monkey ::c/empty ::c/tunnel-1]]})))))

(test-ns 'monkey-music.tunnels-test)
