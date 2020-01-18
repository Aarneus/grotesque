(ns grotesque.model-test
  (:require [grotesque.core :as grotesque]
            [grotesque.model :as model]
            [grotesque.util :as util]
            [grotesque.test-utils :refer [test-diff]]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))


(defn test-set-handler
  "Test effect handler"
  [grammar effect]
  (assoc-in grammar (concat [:data :model] (drop-last effect)) (last effect)))

(defn test-when-validator
  "Test condition validator"
  [grammar condition]
  (= (last condition) (get-in grammar (concat [:data :model] (drop-last condition)))))

(deftest generate-with-model
  (is (= "ABCDEF" (-> {:S       ["#set-var##get-var##get-var#"]
                       :set-var [["" :set.banana.tree.value.A]]
                       :get-var [["DEF" :when.banana.tree.value.D]
                                 ["GHI" :when.banana.tree]
                                 ["ABC" :when.banana.tree.value.A :set.banana.tree.value.D]]}
                      grotesque/create-grammar
                      (grotesque/set-handler :set test-set-handler)
                      (grotesque/set-validator :when test-when-validator)
                      (grotesque/generate "#S#")
                      :generated))))

(deftest errors-in-model
  (test-diff (-> {:S ["s#A#s#B#s"]
                  :A [["" :test-cnd.random]]
                  :B [["" :test-fx.random]]}
                 grotesque/create-grammar
                 (model/set-condition-validator :test-cnd (fn [_ _] (util/throw-cljc "ERROR-1")))
                 (model/set-effect-handler :test-fx (fn [_ _] (util/throw-cljc "ERROR-2")))
                 (grotesque/generate "#S#")
                 (select-keys [:errors :generated]))
             {:generated "sss"
              :errors    ["Error while invoking 'A':\nCondition error in rule ':A-0':\nCondition 'test-cnd.random':\nERROR-1"
                          "No valid rule 'A' found"
                          "Error while invoking 'B':\nEffect error in rule ':B-0':\nERROR-2"
                          "No valid rule 'B' found"]}))