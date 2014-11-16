(ns monkey-music.core-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

(deftest test-move-to-empty
  (is (= {:layout [[::c/open-door ::c/monkey]]
          :original-layout [[::c/open-door ::c/empty]]
          :teams {"1" {:position [0 1]}}} 
         (c/run-command
           {:layout [[::c/monkey ::c/empty]]
            :original-layout [[::c/open-door ::c/empty]]
            :teams {"1" {:position [0 0]}}}
           {:team-name "1" :command-name "move" :direction :right}))))

(deftest test-value-of
  (is (= 1 (c/value-of ::c/song))))

(deftest test-move-to-user
  (is (= {:layout [[::c/monkey ::c/user]]
          :teams {"1" {:position [0 0]
                       :picked-up-items [::c/banana]
                       :score (+ (c/value-of ::c/song)
                                 (c/value-of ::c/album))}}} 
         (c/run-command
           {:layout [[::c/monkey ::c/user]]
            :teams {"1" {:position [0 0]
                         :picked-up-items [::c/banana ::c/song ::c/album]
                         :score 0}}}
           {:team-name "1" :command-name "move" :direction :right}))))

(deftest find-positions
  (is (= (c/find-positions
           [[::c/monkey ::c/empty ::c/song]
            [::c/empty ::c/monkey ::c/monkey]]
           isa? ::c/monkey))))

(deftest test-move-to-tunnel-entrance
  (is (= (c/run-command
           {:layout [[::c/tunnel-exit-1 ::c/monkey ::c/tunnel-entrance-1]]
            :original-layout [[::c/tunnel-exit-1 ::c/empty ::c/tunnel-entrance-1]]
            :teams {"1" {:position [0 1]}}}
           {:team-name "1" :command-name "move" :direction :right})
         {:layout [[::c/monkey ::c/empty ::c/tunnel-entrance-1]]
          :original-layout [[::c/tunnel-exit-1 ::c/empty ::c/tunnel-entrance-1]]
          :teams {"1" {:position [0 0]}}})))

(deftest test-pick-up-item
  (is (= {:layout [[::c/monkey ::c/empty]]
          :teams {"1" {:position [0 0]
                       :picked-up-items [::c/song]}}} 
         (c/run-command
           {:layout [[::c/monkey ::c/song]]
            :teams {"1" {:position [0 0]
                         :picked-up-items []}}}
           {:team-name "1" :command-name "move" :direction :right}))))

(deftest test-apply-sleep-buff
  (is (nil? (c/apply-buffs
              {:teams {"1" {:buffs {::c/asleep 1}}}}
              {:command-name "move" :team-name "1" :direction "right"}))))

(deftest test-apply-no-buff
  (is (= (c/apply-buffs
           {:teams {"1" {:buffs {}}}}
           {:command-name "move" :team-name "1" :direction "right"})
         {:command-name "move" :team-name "1" :direction "right"})))

(deftest test-apply-speedy-buff
  (is (= (c/apply-buffs
           {:teams {"1" {:buffs {::c/speedy 1}}}}
           {:command-name "move" :team-name "1" :directions ["right", "left"]})
         [{:command-name "move" :team-name "1" :direction "right"}
          {:command-name "move" :team-name "1" :direction "left"}])))

(deftest test-apply-all-buffs
  (is (= (c/apply-all-buffs
           {:teams {"1" {:buffs {::c/speedy 1}}
                    "2" {:buffs {::c/asleep 1}}}}
           [{:command-name "move" :team-name "1" :directions ["right", "left"]}
            {:command-name "move" :team-name "2" :direction "right"}])
         [{:command-name "move" :team-name "1" :direction "right"}
          {:command-name "move" :team-name "1" :direction "left"}])))

(test-ns 'monkey-music.core-test)
