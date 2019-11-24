(ns grotesque.picker-fns)

(defn set-picker-fn
  "Set the function for the grammar that picks the best rule out of all valid ones.
   A picker function takes two arguments, grammar and a coll of rules.
   It returns the grammar with the picked rule assoced in the `:picked-rule` key.

   You can store data the picker function uses under the `:metadata` key in the grammar like so:
   {:metadata {:rule-0 {...}
               :rule-1 {...}
               ...}}

   If no picker-fn is set the default picker picks a random rule."
  [grammar picker-fn]
  (assoc-in grammar [:functions :picker-fn] picker-fn))

(defn random-picker-fn
  "Picks a rule at random."
  [grammar rules]
  (assoc grammar :picked-rule (rand-nth rules)))

(defn variety-picker-fn
  "Picks the rule that has been picked least recently.
   Uses the standard timestamp methods for Java/JavaScript."
  [grammar rules]
  (let [picked    (apply min-key #(or (-> grammar :metadata ((:id %)) :picked-ts) 0) rules)
        timestamp #?(:cljs (.getTime (js/Date.))
                     :clj  (System/currentTimeMillis))]
    (-> grammar
        (assoc :picked-rule picked)
        (assoc-in [:metadata (:id picked) :picked-ts] timestamp))))

(defn ranking-picker-fn
  "Picks the rule with the highest ranking.
   Only numeric rankings are allowed.
   Rules with no :rank values in the grammar metadata are considered rank 0."
  [grammar rules]
  (->> rules
       (apply max-key #(or (-> grammar :metadata ((:id %)) :rank) 0))
       (assoc grammar :picked-rule)))