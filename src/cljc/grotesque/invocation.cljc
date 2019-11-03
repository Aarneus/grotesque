(ns grotesque.invocation
  (:require [clojure.string :as string]
            [grotesque.model :as model]))

(defn get-picker
  "Returns the chosen picker for the grammar"
  [grammar]
  (get-in grammar [:functions :picker] #(when-not (empty? %) (rand-nth %))))

(defn invoke-rule
  "Returns the results of the rule invocation as a vector of terminal and non-terminal symbols."
  [grammar non-terminal]
  (if-let [rule-body (->> (get-in grammar [:rules non-terminal] [])
                          (filter #(model/valid-rule? grammar %))
                          ((get-picker grammar)))]
    [(model/execute-rule grammar rule-body) (:text rule-body)]
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
      [current-grammar (string/join expanded)]
      (if (string? next-token)
        (recur current-grammar (conj expanded next-token) rest-tokens)
        (let [[new-grammar coll] (invoke-rule current-grammar next-token)]
          (recur new-grammar expanded (concat coll rest-tokens)))))))
