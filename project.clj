(defproject monkey-music "0.1.0-SNAPSHOT"
  :description "Monkey Music"
  :url "http://github.com/odsod/monkey-music"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2356"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [com.cemerick/clojurescript.test "0.3.1"]]

  :profiles {:dev {:plugins [[com.cemerick/austin "0.1.5"]]}}

  :cljsbuild
  {:builds [{:id "dev"
             :source-paths ["src"]
             :compiler {:output-to "lib/main.js"
                        :optimizations :whitespace}}

            {:id "test"
             :source-paths ["src" "test"]
             :compiler {:output-to "target/testable.js"
                        :optimizations :simple}}

            {:id "prod"
             :source-paths ["src"]
             :compiler {:output-to "lib/main.js"
                        :optimizations :advanced}}]

   :test-commands {"unit-tests" ["nodejs" :node-runner "target/testable.js"]}})
