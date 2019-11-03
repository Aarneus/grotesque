(ns grotesque.util-test
  (:require [grotesque.util :as util]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))

(testing "parse-symbol-string"
  (are [s v] (= v (util/parse-symbol-string s))
             "" []
             "abc" ["abc"]
             "Hello #world#" ["Hello " :world]
             "#A-1##B#cde#FG#hi" [:A-1 :B "cde" :FG "hi"]))