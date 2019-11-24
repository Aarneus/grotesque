(ns grotesque.schema-test
  (:require [grotesque.core :as grotesque]
            [grotesque.model :as model]
            [grotesque.schema :as schema]
            [grotesque.test-utils :as test-utils :refer [validate-schema]]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))

(def test-grammar (->> test-utils/test-rules
                       grotesque/create-grammar
                       model/enable-default-model))

(testing "schema validation after creation"
  (validate-schema schema/Grammar test-grammar))

(testing "schema validation after generation"
  (->> (grotesque/generate test-grammar "#story#")
       (validate-schema schema/Grammar)))