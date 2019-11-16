(ns grotesque.schema-test
  (:require [grotesque.core :as grotesque]
            [grotesque.model :as model]
            [grotesque.schema :as schema]
            [grotesque.test-utils :as test-utils :refer [test-diff]]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])
            #?(:cljs [schema.core :as s :include-macros true]
               :clj  [schema.core :as s])
            [grotesque.model :as model]))

(def test-grammar (->> test-utils/test-rules
                       grotesque/create-grammar
                       model/enable-default-model))

(testing "schema validation after creation"
  (is (map? (s/validate schema/Grammar test-grammar))))

(testing "schema validation after generation"
  (is (map? (->> (grotesque/generate test-grammar "#story#")
                 (s/validate schema/Grammar)))))