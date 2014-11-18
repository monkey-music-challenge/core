(ns monkey-music.buffs-test
  (:require-macros [cemerick.cljs.test :refer (deftest is testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

(deftest test-add-buff
  (testing "speedy"
    (is (= (c/add-buff {:teams {"1" {:buffs {}}}} "1" ::c/speedy)
           {:teams {"1" {:buffs {::c/speedy (c/duration-of ::c/speedy)}}}}))))

(deftest test-tick-all-buffs
  (testing "tick all buffs"
    (is (= (c/tick-all-buffs {:teams {"1" {:buffs {::c/speedy 1}}
                                  "2" {:buffs {::c/trapped 2}}
                                  "3" {:buffs {::c/asleep 1}}}})
           {:teams {"1" {:buffs {}}
                    "2" {:buffs {::c/trapped 1}}
                    "3" {:buffs {}}}}))))

(deftest test-apply-all-buffs
  (testing "apply multiple buffs"
    (let [state {:teams {"1" {:buffs {::c/speedy 1}}
                         "2" {:buffs {::c/speedy 1}}
                         "3" {:buffs {::c/asleep 1}}
                         "4" {:buffs {::c/trapped 1}}
                         "5" {:buffs {::c/tackled 1}}
                         "6" {:buffs {}}}}
          commands [{:command ::c/move :team-name "1" :directions [::c/left ::c/down]}
                    {:command ::c/move :team-name "2" :direction ::c/left}
                    {:command ::c/use :team-name "3" :item ::c/banana}
                    {:command ::c/move :team-name "4" :direction ::c/right}
                    {:command ::c/move :team-name "5" :direction ::c/right}
                    {:command ::c/move :team-name "6" :direction ::c/up}]]
      (is (= (c/apply-all-buffs state commands)
             [{:command ::c/move :team-name "1" :direction ::c/left}
              {:command ::c/move :team-name "1" :direction ::c/down}
              {:command ::c/move :team-name "2" :direction ::c/left}
              {:command ::c/move :team-name "6" :direction ::c/up}])))))

(test-ns 'monkey-music.buffs-test)
