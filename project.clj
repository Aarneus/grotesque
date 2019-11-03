(defproject grotesque "0.1.2"
  :description "A context-free grammar with state for text generation"
  :url "https://github.com/Aarneus/grotesque"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]]
  :plugins [[lein-cljsbuild "1.1.7"]]
  :cljsbuild {:builds []}
  :source-paths ["src" "src/cljc"]
  :test-paths ["test" "test/cljc"])
