(ns grotesque.core
  (:require [grotesque.generation :as generation]
            [grotesque.model :as model]
            [grotesque.modifiers :as modifiers]
            [grotesque.rules :as rules]
            [grotesque.selection :as selection]
            [grotesque.util :as util]
            [grotesque.variables :as variables]))

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
    (generation/generate (assoc grammar :errors []) starting-string)))

(def set-handler
  "Sets the function the grammar uses to handle a tag that is an effect.
   See `docs/model.md` for more details."
  model/set-effect-handler)

(def set-validator
  "Sets the function the grammar uses to validate a tag that is a condition.
   See `docs/model.md` for more details."
  model/set-condition-validator)

(def set-selector
  "Sets the function the grammar uses to select a rule from all valid options.
   See `docs/selection.md` for more details."
  selection/set-selector)

(def set-modifier
  "Sets the function that takes in a generated string and returns a transformed version.
   See `docs/modifiers.md` for more details."
  modifiers/set-modifier)

(def set-variable
  "Sets the function that generates candidates for replacing the given tag part.
   The function takes two arguments, the current grammar and the tag part itself.
   The function should return a list of keywords that can be used to replace the given tag part
   in rule tags.
   See `docs/variables.md` for more details."
  variables/set-variable)