(ns grotesque.core-test
  (:require [grotesque.core :as grotesque]
            [grotesque.test-utils :as test-utils :refer [test-diff]]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))

(def test-grammar (grotesque/create-grammar test-utils/test-rules))

(testing "create-grammar"
  (test-diff {:rules {} :errors []}
             (grotesque/create-grammar))
  (test-diff {:rules test-utils/test-rules-processed :errors []}
             test-grammar))

(testing "generate"
  (is (= "abc" (-> {:S ["a#B#"] :B ["b#C#"] :C ["c"]}
                   grotesque/create-grammar
                   (grotesque/generate "#S#")
                   :generated)))
  (is (let [new-grammar (grotesque/generate test-grammar "#story#")
            s (:generated new-grammar)]
        (and (map? new-grammar) (string? s)))))

(testing "parse errors"
  (test-diff (-> {:S [["#a#" 12]]}
                 grotesque/create-grammar)
             {:errors #?(:cljs ["Error in 'S':\nWhile parsing rule 'S-0':\n"]
                         :clj  ["Error in 'S':\nWhile parsing rule 'S-0':\njava.lang.Long cannot be cast to clojure.lang.Named"])
              :rules  {}}))

(testing "missing rule"
  (test-diff (-> {:S ["textS #A#"]
                  :A ["textA #b# #C#"]
                  :B ["textB"]
                  :C ["textC"]}
                 grotesque/create-grammar
                 (grotesque/generate "#S#")
                 (select-keys [:errors :generated]))
             {:errors    ["Error while invoking 'b':\nNo rule 'b' found, a possible typo?"
                          "No valid rule 'b' found"]
              :generated "textS textA  textC"}))

(testing "selectors"
  (let [meta-selector-fn (fn [grammar head bodies]
                           (is (= :S head))
                           (is (= bodies [{:id :S-0, :text ["a"]}
                                          {:id :S-1, :text ["b"]}
                                          {:id :S-2, :text ["c"]}]))
                           (assoc grammar :selected {:id :meta, :text ["Meta rules!"]}))]
    (is (= "Meta rules!"
           (-> {:S ["a" "b" "c"]}
               grotesque/create-grammar
               (grotesque/set-selector meta-selector-fn)
               (grotesque/generate "[S]")
               :generated)))))