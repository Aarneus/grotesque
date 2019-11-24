(ns grotesque.invocation
  (:require [clojure.string :as string]
            [grotesque.model :as model]
            [grotesque.picker-fns :refer [random-picker-fn]]))

(defn get-picker
  "Returns the chosen picker for the grammar"
  [grammar]
  (or (-> grammar :functions :picker-fn)
      random-picker-fn))

(defn- invoke-rule
  "Returns the results of the rule invocation as a vector of terminal and non-terminal symbols."
  [grammar non-terminal]
  (if-let [new-grammar (->> (get-in grammar [:rules non-terminal] [])
                            (filter #(model/valid-rule? grammar %))
                            ((get-picker grammar) grammar))]
    [(model/execute-rule new-grammar) (-> new-grammar :picked-rule :text)]
    [grammar [""]]))

(defn generate
  "Activates the appropriate rules for the given grammar and starting-vector.
   See the docstring for grotesque.core/create-grammar for more info on the generation of the grammar
   and the docstring for grotesque.core/generate for more info on the generation of the starting-vector"
  [grammar starting-vector]
  (loop [current-grammar            grammar
         expanded                   []
         [next-token & rest-tokens] starting-vector]
    (if (nil? next-token)
      (assoc current-grammar :generated (string/join expanded))
      (if (string? next-token)
        (recur current-grammar (conj expanded next-token) rest-tokens)
        (let [[new-grammar coll] (invoke-rule current-grammar next-token)]
          (recur new-grammar expanded (concat coll rest-tokens)))))))
