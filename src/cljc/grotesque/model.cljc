(ns grotesque.model)

(defn- valid-condition?
  "Returns true if the state is as described.
   The condition is simply a tuple of keywords, e.g. [:car :color :red]"
  [grammar condition]
  (= (last condition)
     (get-in (:model grammar) (drop-last condition) nil)))

(defn valid-rule?
  "Returns true if the given rule body's conditions match the current grammar state."
  [grammar rule]
  (every? #(valid-condition? grammar %) (:when rule)))

(defn- execute-effect
  "Adds the given fact to the grammar's model.
   The fact is simply a tuple of keywords, e.g. [:car :color :red]"
  [grammar effect]
  (update grammar :model #(assoc-in % (drop-last effect) (last effect))))

(defn execute-rule
  "Returns the new grammar state after performing all effects in the given rule."
  [grammar rule]
  (loop [new-grammar grammar [next & rest] (:set rule)]
    (if (nil? next)
      new-grammar
      (recur (execute-effect new-grammar next) rest))))
