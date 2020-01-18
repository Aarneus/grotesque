(defproject grotesque "0.6.0"
  :description  "A context-free grammar with state for text generation"
  :url          "https://github.com/Aarneus/grotesque"
  :license      {:name "MIT License"
                 :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [prismatic/schema "1.1.12"]]
  :plugins      [[lein-cljsbuild "1.1.7"]
                 [lein-doo "0.1.10"]]
  :cljsbuild    {:builds [{:id           "test-build"
                           :source-paths ["src" "test"]
                           :compiler     {:output-to     "target/js/test.js"
                                          :main          grotesque.test-runner
                                          :optimizations :none}}]}
  :source-paths ["src" "src/cljc"]
  :test-paths   ["test" "test/cljc"]
  :doo {:build "test-build"
        :alias {:default [:chrome-headless]}}
  )
