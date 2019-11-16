(ns grotesque.core
  (:require [grotesque.util :as util]
            [grotesque.rules :as rules]
            [grotesque.invocation :as invocation]))

(def empty-grammar {:model      {}
                    :rules      {}})

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
   Returns a tuple of the updated grammar and the generated string."
  [grammar starting-string]
  (if (string? starting-string)
    (recur grammar (util/parse-symbol-string starting-string))
    (invocation/generate grammar starting-string)))














