(ns grotesque.invocation
  (:require [clojure.string :as string]))

(defn invoke-rule
  "Returns the results of the rule invocation as a vector of terminal and non-terminal symbols."
  [grammar non-terminal]
  (if-let [rule-body (get-in grammar [:rules non-terminal])]
    (:text (rand-nth rule-body))
    []))

(defn generate
  "Activates the appropriate rules for the given grammar and starting-vector.
   See the docstring for grotesque.core/create-grammar for more info on the generation of the grammar
   and the docstring for grotesque.core/generate for more info on the generation of the starting-vector"
  [grammar starting-vector]
  (loop [expanded [] [next-token & rest-tokens] starting-vector]
    (if (nil? next-token)
      (string/join expanded)
      (if (string? next-token)
        (recur (conj expanded next-token) rest-tokens)
        (recur expanded (-> (invoke-rule grammar next-token)
                            (concat rest-tokens)))))))
