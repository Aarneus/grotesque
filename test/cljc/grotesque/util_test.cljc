(ns grotesque.util-test
  (:require [grotesque.util :as util]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))

(testing "parse-symbol-string"
  (is (= (util/parse-symbol-string "#A-1##B#cde#FG#hi")
         [:A-1 :B "cde" :FG "hi"])))