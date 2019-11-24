(ns grotesque.model)

(defn default-condition-when
  "Returns true if the fact has the given value.
   The fact is simply a tuple of keywords, e.g. [:car :color :red]"
  [grammar condition]
  (= (last condition)
     (get-in (:model grammar) (drop-last condition) nil)))

(defn default-condition-when-any
  "Returns true if the fact has any non-nil value.
   The fact is simply a tuple of keywords, e.g. [:car :color]"
  [grammar condition]
  (some? (get-in (:model grammar) condition)))

(defn default-effect-unset
  "Removes the given fact to the grammar's model.
   The fact is simply a tuple of keywords, e.g. [:car :color]"
  [grammar effect]
  (update-in grammar (concat [:model] (drop-last effect)) dissoc (last effect)))

(defn default-effect-set
  "Adds the given fact to the grammar's model.
   The fact is simply a tuple of keywords, e.g. [:car :color :red]"
  [grammar effect]
  (assoc-in grammar (concat [:model] (drop-last effect)) (last effect)))

(defn set-condition-validator
  "Sets a function to perform validation for a given condition type.
   Validators take two parameters, the current grammar state and the condition body and return
   true if the condition is valid, false if not.
   They are used to validate a rule when it is being selected."
  [grammar condition-type validation-fn]
  (assoc-in grammar [:conditions condition-type] validation-fn))

(defn set-effect-handler
  "Sets a function to perform as the handler for a given effect type.
   Handlers take two parameters, the current grammar state and the effect body and return
   the updated grammar state.
   They are executed after the rule has been selected."
  [grammar effect-type handler-fn]
  (assoc-in grammar [:effects effect-type] handler-fn))

(defn enable-default-model
  "Sets the default condition and effect identifiers"
  [grammar]
  (-> grammar
      (set-effect-handler :set default-effect-set)
      (set-effect-handler :unset default-effect-unset)
      (set-effect-handler :when-set default-effect-set)
      (set-condition-validator :when default-condition-when)
      (set-condition-validator :when-not (complement default-condition-when))
      (set-condition-validator :when-any default-condition-when-any)
      (set-condition-validator :when-nil (complement default-condition-when-any))
      (set-condition-validator :when-set #(or (default-condition-when %1 %2)
                                              (not (default-condition-when-any %1 (drop-last %2)))))))

(defn valid-rule?
  "Returns true if the given rule body's conditions match the current grammar state."
  [grammar rule]
  (let [condition-types        (keys (:conditions grammar))
        get-conditions-of-type (fn [type]
                                 (-> rule :bodies type))
        condition-valid?       (fn [type grammar condition]
                                 ((-> grammar :conditions type) grammar condition))]
    (every? #(every? (partial condition-valid? % grammar)
                     (get-conditions-of-type %))
            condition-types)))

(defn execute-rule
  "Returns the new grammar state after performing all effects in the given rule."
  [grammar rule]
  (reduce (fn [grammar type]             ; Iterate over all effect types
            (reduce (fn [grammar effect] ; Iterate over all effects of given type
                      ((-> grammar :effects type) grammar effect))
                    grammar
                    (-> rule :bodies type)))
          grammar
          (keys (:effects grammar))))
