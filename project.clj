(defproject monkey-music "0.1.0-SNAPSHOT"
  :description "Monkey Music"
  :url "http://github.com/odsod/monkey-music"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2356"]
                 [com.cemerick/pprng "0.0.2"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [com.cemerick/clojurescript.test "0.3.1"]
            [lein-kibit "0.0.8"]]

  :profiles {:dev {:plugins [[com.cemerick/austin "0.1.5"]]}}

  :cljsbuild
  {:builds [{:id "debug"
             :source-paths ["src"]
             :compiler {:optimizations :simple
                        :output-dir "lib/debug"
                        :output-to "lib/debug/index.js"
                        :source-map "lib/debug/index.js.map"}}

            {:id "test"
             :source-paths ["src" "test"]
             :notify-command ["nodejs" :cljs.test/node-runner "target/testable.js"]
             :compiler {:output-dir "target"
                        :output-to "target/testable.js"
                        :optimizations :simple}}

            {:id "prod"
             :source-paths ["src"]
             :compiler {:optimizations :advanced
                        :output-dir "lib"
                        :output-to "lib/index.js"}}]

   :test-commands {"unit-tests" ["nodejs" :node-runner "target/testable.js"]}})
