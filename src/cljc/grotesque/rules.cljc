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
            (if-let [text (->> body (filter string?) first)]
              {:text (util/parse-symbol-string text)}
              {})
            (filter (complement string?) body))
    {:text (util/parse-symbol-string body)}))

(defn- try-parse
  "Wraps a parse effort in a try so we can log any errors."
  [id unparsed-body]
  (try
    (assoc (parse-body unparsed-body) :id id)
    (catch #?(:cljs js/Error, :clj Exception) e
      (util/throw-cljc (str "While parsing rule '" (name id) "'") e))))

(defn- add-bodies
  "Util function for adding rules without overwriting old ones
   Every rule's id equals that rule's head appended with the rule's index.
   So for example, the indices of the rule `:weather` would be
   :weather-0, :weather-1, :weather-2, ..."
  [head old-bodies new-bodies]
  (let [new-first-index (count old-bodies)
        new-indices     (map #(+ new-first-index %) (range (count new-bodies)))
        new-ids         (map #(keyword (str (name head) "-" %)) new-indices)]
    (->> (map try-parse new-ids new-bodies)
         (concat old-bodies)
         vec)))

(defn add-rule
  "Adds the given rule to the grammar.
   See the comment on add-rules for more detail."
  [grammar [head bodies]]
  (util/try-catch-cljc grammar (str "Error in '" (name head) "':")
    (let [head (keyword head)]
      (update-in grammar
                 [:rules head]
                 #(add-bodies head % bodies)))))

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
  (reduce add-rule (assoc grammar :errors []) rules))
