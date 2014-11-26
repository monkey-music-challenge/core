(ns monkey-music.tackle-test
  (:require-macros [cemerick.cljs.test :refer (deftest is are testing)])
  (:require [cemerick.cljs.test :refer (test-ns)]
            [monkey-music.core :as c]))

(def state
  (c/create-game-state
    ["1" "2" "3"]
    {:layout [[::c/empty ::c/monkey ::c/empty]
              [::c/song ::c/monkey ::c/monkey]
              [::c/empty ::c/wall ::c/empty]]
     :inventory-size 3
     :turns 10}))

(deftest test-tackle-with-move-with-steal
  (with-redefs [c/check-steal-success! (constantly true)]
    (let [curr-state
        (-> state
            ; team 2 picks up a song
            (c/run-commands [{:team-name "1" :command ::c/idle}
                             {:team-name "2" :command ::c/move :direction ::c/left}
                             {:team-name "3" :command ::c/idle}])
            ; team 3 tackles team 2 (and steals item)
            (c/run-commands [{:team-name "1" :command ::c/idle}
                             {:team-name "2" :command ::c/idle}
                             {:team-name "3" :command ::c/move :direction ::c/left}]))]
    (are [x y] (= x y)
         [::c/song] (get-in curr-state [:teams "3" :inventory])
         [] (get-in curr-state [:teams "2" :inventory])
         [1 0] (get-in curr-state [:teams "2" :position])
         [1 1] (get-in curr-state [:teams "3" :position])))))

(deftest test-tackle-with-move-without-steal
  (with-redefs [c/check-steal-success! (constantly false)]
    (let [curr-state
        (-> state
            ; team 2 picks up a song
            (c/run-commands [{:team-name "1" :command ::c/idle}
                             {:team-name "2" :command ::c/move :direction ::c/left}
                             {:team-name "3" :command ::c/idle}])
            ; team 3 tackles team 2
            (c/run-commands [{:team-name "1" :command ::c/idle}
                             {:team-name "2" :command ::c/idle}
                             {:team-name "3" :command ::c/move :direction ::c/left}]))]
    (are [x y] (= x y)
         [] (get-in curr-state [:teams "3" :inventory])
         [::c/song] (get-in curr-state [:teams "2" :inventory])
         [1 0] (get-in curr-state [:teams "2" :position])
         [1 1] (get-in curr-state [:teams "3" :position])))))

(deftest test-tackle-without-move-without-steal
  (with-redefs [c/check-steal-success! (constantly false)]
    (let [curr-state
        (-> state
            ; team 2 tackles team 3
            (c/run-commands [{:team-name "1" :command ::c/idle}
                             {:team-name "2" :command ::c/move :direction ::c/right}
                             {:team-name "3" :command ::c/idle}]))]
    (are [x y] (= x y)
         [1 1] (get-in curr-state [:teams "2" :position])
         [1 2] (get-in curr-state [:teams "3" :position])))))

;(test-ns 'monkey-music.tackle-test)
