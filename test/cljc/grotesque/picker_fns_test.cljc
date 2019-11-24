(ns grotesque.picker-fns-test
  (:require [grotesque.core :as grotesque]
            [grotesque.picker-fns :refer [set-picker-fn random-picker-fn variety-picker-fn ranking-picker-fn]]
            [grotesque.schema :as schema]
            [grotesque.test-utils :as test-utils :refer [validate-schema sleep]]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))

(def test-grammar (-> {:color ["blue" "red" "orange" "yellow" "green" "purple"]}
                      grotesque/create-grammar
                      (assoc :metadata {:color-0 {:picked-ts 4}
                                        :color-1 {:picked-ts 5}
                                        :color-3 {:picked-ts 3, :rank 3}
                                        :color-4 {:picked-ts 2, :rank 2}
                                        :color-5 {:picked-ts 1, :rank 1}})))

(def test-rules (-> test-grammar :rules :color))

(testing "sanity check"
  (validate-schema schema/Grammar
                   (-> test-grammar
                       (set-picker-fn random-picker-fn)
                       (grotesque/generate "#color#")
                       (set-picker-fn variety-picker-fn)
                       (grotesque/generate "#color#")
                       (set-picker-fn ranking-picker-fn)
                       (grotesque/generate "#color#"))))

(defn- pick-and-test
  "Utility that picks a rule with the given picker-fn and then checks it, returning the grammar for
   easy threading."
  [grammar picker-fn expected-id]
  ; Sleep 1 millisecond to ensure different timestamps for the variety picker-fn
  (sleep 1)
  (let [new-grammar (picker-fn grammar test-rules)]
    (is (= expected-id (:id (:picked-rule new-grammar))))
    new-grammar))

(testing "random-picker-fn" ; Just a simple sanity check since behaviour is random
  (validate-schema schema/Grammar (random-picker-fn test-grammar test-rules)))

(testing "variety-picker-fn" ; The picks cycle because the same set is picked every time
  (reduce #(pick-and-test %1 variety-picker-fn %2)
          test-grammar
          (->> [:color-2 :color-5 :color-4 :color-3 :color-0 :color-1]
               cycle
               (take 50))))

(testing "ranking-picker-fn" ; The highest ranking rule is picked every time
  (reduce #(pick-and-test %1 ranking-picker-fn %2)
          test-grammar
          (repeat 50 :color-3)))

(testing "mixed pickers do not interfere with each other"
  (-> test-grammar
      (random-picker-fn test-rules)
      (pick-and-test ranking-picker-fn :color-3)
      (pick-and-test variety-picker-fn :color-2)
      (pick-and-test variety-picker-fn :color-5)
      (pick-and-test ranking-picker-fn :color-3)
      (random-picker-fn test-rules)
      (pick-and-test variety-picker-fn :color-4)
      (pick-and-test variety-picker-fn :color-3)
      (random-picker-fn test-rules)
      (pick-and-test variety-picker-fn :color-0)
      (pick-and-test variety-picker-fn :color-1)))