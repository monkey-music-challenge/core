(defproject monkey-music "0.1.0-SNAPSHOT"
  :description "Monkey Music"
  :url "http://github.com/odsod/monkey-music"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2356"]]

  :plugins [[lein-cljsbuild "1.0.3"]]

  :profiles {:dev {:plugins [[com.cemerick/austin "0.1.5"]]}}

  :cljsbuild {
    :builds {
      :dev
      {:source-paths ["src"]
       :compiler {:output-to "lib/main.js"
                  :optimizations :whitespace}}
      :prod
      {:source-paths ["src"]
       :compiler {:output-to "lib/main.js"
                  :optimizations :advanced}}}})
