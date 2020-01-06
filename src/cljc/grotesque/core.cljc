(ns grotesque.core
  (:require [grotesque.invocation :as invocation]
            [grotesque.model :as model]
            [grotesque.rules :as rules]
            [grotesque.util :as util]))

(def empty-grammar {:rules      {}
                    :errors     []})

(defn create-grammar
  "Initialize a new grammar, with optional rules data.
   See the docstring for grotesque.rules/add-rules for more info on the rule format."
  ([] (create-grammar nil))
  ([rules] (if (some? rules)
             (rules/add-rules empty-grammar rules)
             empty-grammar)))

(defn generate
  "Use a grammar to transform a string or a vector of keywords and strings into a fully expanded string.
   See the docstring for grotesque.core/create-grammar for more info on the grammar format.
   Examples of the same starting-string in both formats (where 'adjective' and 'animal' are non-terminal symbols):
   String:
   \"There is an #adjective# #animal#.\"
   Vector:
   [\"There is an \" :adjective \" \" :animal \".\"]
   Returns the new grammar with the generated string under the `:generated` - key."
  [grammar starting-string]
  (if (string? starting-string)
    (recur grammar (util/parse-symbol-string starting-string))
    (invocation/generate (assoc grammar :errors []) starting-string)))

(def set-handler
  "Sets the function the grammar uses to handle a tag that is an effect.
   See the `docs/model.md` for more details."
  model/set-effect-handler)

(def set-validator
  "Sets the function the grammar uses to validate a tag that is a condition.
   See the `docs/model.md` for more details."
  model/set-condition-validator)

(defn set-selector
  "Sets the function the grammar uses to select a rule from all valid options.
   See the `docs/selection.md` for more details."
  [grammar selector-fn]
  (assoc-in grammar [:functions :selector-fn] selector-fn))












