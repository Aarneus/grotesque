(ns grotesque.schema-test
  (:require [grotesque.core :as grotesque]
            [grotesque.model :as model]
            [grotesque.schema :as schema]
            [grotesque.test-utils :as test-utils :refer [validate-schema]]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))

(def test-grammar (->> test-utils/test-rules
                       grotesque/create-grammar))

(testing "schema validation after creation"
  (validate-schema schema/Grammar test-grammar))

(testing "schema validation after generation"
  (-> (grotesque/generate test-grammar "#story#")
      (->> (validate-schema schema/Grammar))))

(testing "schema validation with dummy functions"
  (-> test-grammar
      (grotesque/set-selector identity)
      (grotesque/set-handler :test identity)
      (grotesque/set-validator :test identity)
      (->> (validate-schema schema/Grammar))))
