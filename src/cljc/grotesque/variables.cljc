(ns grotesque.variables
  (:require [clojure.walk :as walk]
            [clojure.math.combinatorics :as combo]
            [grotesque.model :as model]))

(defn set-variable
  "Sets the function that generates candidates for replacing the given tag part.
   The function takes two arguments, the current grammar and the tag part itself.
   The function should return a list of keywords that can be used to replace the given tag part
   in rule tags.
   See `docs/variables.md` for more details."
  [grammar tag-part candidate-generation-fn]
  (assoc-in grammar [:functions :variable-fns tag-part] candidate-generation-fn))

(defn replace-variables
  "Returns a version of the given rule where the variables have been replaced
   with the given values"
  [rule-body vars values]
  (let [var-to-value (->> (map vector vars values)
                          (into {}))]
    (walk/prewalk #(cond->> %
                            (contains? var-to-value %)
                            var-to-value)
                  rule-body)))

(defn get-valid-bodies
  "Returns a coll of valid bodies of the given rule.
   All valid combinations of the variables and their available values are returned."
  [grammar rule-body]
  (if-let [fns (-> grammar :functions :variable-fns)]
    (let [part-set (-> fns keys set)
          vars     (->> rule-body :tags flatten distinct (filter part-set))]
      (->> vars
           (map #((get fns %) grammar %))
           (apply combo/cartesian-product)
           (map #(replace-variables rule-body vars %))
           (filter #(model/valid-rule? grammar %))))
    (when (model/valid-rule? grammar rule-body)
      [rule-body])))