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
  {:builds [{:id "dev"
             :source-paths ["src"]
             :compiler {:output-to "lib/main.js"
                        :optimizations :whitespace}
             :libs ""}

            {:id "test"
             :source-paths ["src" "test"]
             :compiler {:output-to "target/testable.js"
                        :optimizations :simple}
             :libs ""}

            {:id "prod"
             :source-paths ["src"]
             :compiler {:output-to "lib/main.js"
                        :optimizations :advanced}
             :libs ""}]

   :test-commands {"unit-tests" ["nodejs" :node-runner "target/testable.js"]}})
