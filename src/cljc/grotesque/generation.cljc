(ns grotesque.generation
  (:require [clojure.string :as string]
            [clojure.walk :as walk]
            [grotesque.model :as model]
            [grotesque.modifiers :as modifiers]
            [grotesque.selection :as selection]
            [grotesque.util :as util]))

(defn- check-rule
  "Throws an error if the given rule does not exist in the grammar"
  [grammar rule]
  (when-not (contains? (:rules grammar) rule)
    (util/throw-cljc (str "No rule '" (name rule) "' found, a possible typo?"))))

(defn- invoke-rule
  "Returns the results of a rule invocation (a map)."
  [grammar non-terminal]
  (let [invoked             (name non-terminal)
        [head & modifiers] (->> (string/split invoked #"\.")
                                (map keyword))
        selector-fn         (selection/get-selector-fn grammar)
        grammar             (dissoc grammar :selected)
        updated-grammar     (util/try-catch
                              grammar
                              (str "Error while invoking '" invoked "':")
                              (fn []
                                (check-rule grammar head)
                                (modifiers/check-modifiers grammar modifiers)
                                (->> (get-in grammar [:rules head] [])
                                     (filter #(model/valid-rule? grammar %))
                                     (selector-fn grammar head)
                                     model/execute-rule)))
        picked-something?   (selection/picked-some? updated-grammar)
        final-grammar       (if picked-something?
                              updated-grammar
                              (util/add-error updated-grammar (str "No valid rule '" invoked "' found")))]
    {:selected  (-> final-grammar :selected :id)
     :output    (-> final-grammar :selected :text)
     :modifiers modifiers
     :rule-head head
     :grammar   final-grammar}))


(defn make-generation-tree
  "Creates a tree of the given vector of terminals and non-terminals with the generated text.
   Returns the updated grammar and the tree in a tuple."
  [grammar tokens]
  (loop [grammar                    grammar
         [next-token & rest-tokens] tokens
         processed-tokens           []]
    (if (nil? next-token)
      [grammar processed-tokens]
      (if (string? next-token)
        (recur grammar rest-tokens (conj processed-tokens next-token))
        (let [invocation     (invoke-rule grammar next-token)
              [grammar tree] (make-generation-tree (:grammar invocation) (:output invocation))
              invocation-map (-> invocation
                                 (dissoc :grammar)
                                 (assoc :output tree))]
          (recur grammar rest-tokens (conj processed-tokens invocation-map)))))))

(defn join-generation-tree
  "Concatenates all generated text in the generation tree into a single string."
  [grammar node]
  (if (string? node)
    node
    (let [is-map?  (map? node)
          tokens   (if is-map? (:output node) node)
          raw-text (->> (map #(join-generation-tree grammar %) tokens)
                        string/join)]
      (cond-> raw-text
        is-map? (modifiers/get-modified-text grammar (:modifiers node))))))


(defn generate
  "Activates the appropriate rules for the given grammar and starting-vector.
   See the docstring for `grotesque.core/create-grammar` for more info on the generation of the grammar
   and the docstring for `grotesque.core/generate` for more info on the generation of the starting-vector."
  [grammar starting-vector]
  (let [[grammar root] (make-generation-tree grammar starting-vector)
        output-text    (join-generation-tree grammar root)]
    (assoc grammar :generated output-text)))
