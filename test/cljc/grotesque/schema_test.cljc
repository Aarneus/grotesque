(ns grotesque.schema-test
  (:require [grotesque.core :as grotesque]
            [grotesque.schema :as schema]
            [grotesque.test-utils :as test-utils :refer [validate-schema]]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))

(def test-grammar (->> test-utils/test-rules
                       grotesque/create-grammar))

(deftest schema-validation
  (testing "before generation"
    (validate-schema schema/Grammar test-grammar))

  (testing "after generation"
    (-> test-grammar
        (grotesque/generate "#story#")
        (->> (validate-schema schema/Grammar))))

  (testing "after generation errors"
    (-> test-grammar
        (grotesque/generate "#non-existing-token#")
        (->> (validate-schema schema/Grammar))))

  (testing "with dummy functions"
    (-> test-grammar
        (grotesque/set-selector identity)
        (grotesque/set-handler :test identity)
        (grotesque/set-validator :test identity)
        (grotesque/set-modifier :test identity)
        (->> (validate-schema schema/Grammar)))))
