(ns grotesque.util-test
  (:require [grotesque.util :as util]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))

(deftest parse-symbol-string
  (are [s v] (= v (util/parse-symbol-string s))
             "" []
             "abc" ["abc"]
             "Hello #world#" ["Hello " [:world]]
             "Hello [world.state]" ["Hello " [:world :state]]
             "#A-1##B#cde#FG#hi" [[:A-1] [:B] "cde" [:FG] "hi"]))

(deftest try-catch
  (is (= {:errors ["catched-msg\nthrown-msg"]}
         (util/try-catch {} "catched-msg"
           #(util/throw-cljc "thrown-msg")))))