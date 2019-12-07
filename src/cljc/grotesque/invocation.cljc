(ns grotesque.invocation
  (:require [clojure.string :as string]
            [grotesque.model :as model]
            [grotesque.picker-fns :refer [random-picker-fn]]
            [grotesque.util :as util]))

(defn get-picker
  "Returns the chosen picker for the grammar."
  [grammar]
  (or (-> grammar :functions :picker-fn)
      random-picker-fn))

(defn picked-some?
  "Returns true if a rule has been picked"
  [grammar]
  (-> grammar :picked-rule some?))

(defn- invoke-rule
  "Returns a tuple with the grammar and the results of the rule invocation
   (as a vector of terminal and non-terminal symbols)."
  [grammar non-terminal]
  (let [grammar     (dissoc grammar :picked-rule)
        new-grammar (util/try-catch-cljc
                      grammar
                      (str "Error while invoking '" (name non-terminal) "':")
                      (when-not (contains? (:rules grammar) non-terminal)
                        (util/throw-cljc (str "No rule '" (name non-terminal) "' found, a possible typo?")))
                      (->> (get-in grammar [:rules non-terminal] [])
                           (filter #(model/valid-rule? grammar %))
                           ((get-picker grammar) grammar)
                           model/execute-rule))]
    (if (picked-some? new-grammar)
       [new-grammar (or (-> new-grammar :picked-rule :text) [""])]
       [(util/add-error new-grammar (str "No valid rule '" (name non-terminal) "' found")) [""]])))

(defn generate
  "Activates the appropriate rules for the given grammar and starting-vector.
   See the docstring for `grotesque.core/create-grammar` for more info on the generation of the grammar
   and the docstring for `grotesque.core/generate` for more info on the generation of the starting-vector."
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
