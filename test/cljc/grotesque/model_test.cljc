(ns grotesque.model-test
  (:require [grotesque.core :as grotesque]
            [grotesque.model :as model]
            [grotesque.util :as util]
            [grotesque.test-utils :refer [test-diff]]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))


(defn test-set-handler
  "Test effect handler"
  [grammar _ [_ & tag-body]]
  (assoc-in grammar (concat [:data :model] (drop-last tag-body)) (last tag-body)))

(defn test-when-validator
  "Test condition validator"
  [grammar _ [_ & tag-body]]
  (= (last tag-body) (get-in grammar (concat [:data :model] (drop-last tag-body)))))

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
                 (model/set-condition-validator :test-cnd (fn [_ _ _] (util/throw-cljc "ERROR-1")))
                 (model/set-effect-handler :test-fx (fn [_ _ _] (util/throw-cljc "ERROR-2")))
                 (grotesque/generate "#S#")
                 (select-keys [:errors :generated]))
             {:errors    ["Error while invoking 'A':\nCondition error in rule ':A-0' in tag ':test-cnd.random':\nERROR-1"
                          "No valid rule 'A' found"
                          "Error while invoking 'B':\nEffect error in rule ':B-0' in tag ':test-fx.random':\nERROR-2"
                          "No valid rule 'B' found"]}))