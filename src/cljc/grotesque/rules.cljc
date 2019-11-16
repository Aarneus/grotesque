(ns grotesque.rules
  (:require [clojure.string :as string]
            [grotesque.util :as util]))

(defn parse-body-part
  "Accepted body parts are in vector, keyword or string forms.
   Vectors are the preferred form that the other forms are converted to.
   Example inputs (condition first, effect second):
   1. Vector: [:when :weather :snowing], [:set :mood :chill]
   2. Keyword: :when.weather.snowing, :set.mood.chill
   3. String: \"when.weather.snowing\", \"set.mood.chill\"
   All the above examples would result in the following (when body is {}):
   {:when [[:when :weather :snowing]], :set [[:set :mood :chill]]}"
  [rule part]
  (if (vector? part)
    (update-in rule [:bodies (first part)] #(conj (vec %) (vec (rest part))))
    (-> (name part)
        (string/split #"\.")
        (->> (mapv keyword)
             (recur rule)))))

(defn parse-body
  "Accepts bodies in vector or string forms.
   A string is converted to a simple body with no state conditions or effects."
  [body]
  (if (vector? body)
    (reduce parse-body-part
            {:text (util/parse-symbol-string (first body))}
            (rest body))
    {:text (util/parse-symbol-string body)}))

(defn- add-bodies
  "Util function for adding rules without overwriting old ones."
  [old-bodies new-bodies]
  (->> new-bodies
       (map parse-body)
       (concat old-bodies)
       vec))

(defn add-rule
  "Adds the given rule to the grammar.
   See the comment on add-rules for more detail."
  [grammar [head bodies]]
  (update-in grammar
             [:rules (keyword head)]
             #(add-bodies % bodies)))

(defn add-rules
  "Takes a map of rules.
   The preferred form is a mapping of rule head to a vector of body text,
   followed by optional conditions and effects:
   {:animal [[\"okapi\" :when.animal.type.okapi]
             [\"giraffe\" :when.animal.length.tall]
             \"antilope\"
    :job    [\"doctor\" \"lawyer\" \"gambler\"]}
   Tracery style rule map with just string keys to a vector of string values is also accepted:
   {\"animal\" [\"okapi\", \"giraffe\", \"antilope\",
    \"job\" [\"doctor\", \"lawyer\", \"gambler\"]}"
  [grammar rules]
  (reduce add-rule grammar rules))
