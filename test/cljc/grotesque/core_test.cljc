(ns grotesque.core-test
  (:require [clojure.data :as data]
            [grotesque.core :as grotesque]
            [grotesque.test-utils :as test-utils :refer [test-diff]]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))

(def test-grammar (grotesque/create-grammar test-utils/test-rules))

(testing "create-grammar"
  (test-diff (grotesque/create-grammar)
             {:model {} :rules {}}))
  (test-diff test-grammar
             {:model {}
              :rules test-utils/test-rules-processed})

(testing "generate"
  (is (= "abc" (-> {:S ["a#B#"] :B ["b#C#"] :C ["c"]}
                    grotesque/create-grammar
                    (grotesque/generate "#S#")
                   second)))
  (is (= "ABCDEF" (-> {:S ["#set-var##get-var##get-var#"]
                       :set-var [["" :set.banana.tree.value.A]]
                       :get-var [["DEF" :when.banana.tree.value.D]
                                 ["GHI" :when.banana.tree]
                                 ["ABC" :when.banana.tree.value.A :set.banana.tree.value.D]]}
                      grotesque/create-grammar
                      (grotesque/generate "#S#")
                      second)))
  (is (let [[new-grammar s] (grotesque/generate test-grammar "#story#")]
        (println "Generated:" s)
        (and (map? new-grammar) (string? s)))))
