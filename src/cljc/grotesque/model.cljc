(ns grotesque.model
  (:require [clojure.string :as str]
            [grotesque.util :as util]))

(defn set-condition-validator
  "Sets a function to perform validation for a given condition type.
   Validators take two parameters, the current grammar state and the condition tag and return
   true if the condition is valid, false if not.
   They are used to validate a rule when it is being selected."
  [grammar condition-type validation-fn]
  (assoc-in grammar [:conditions condition-type] validation-fn))

(defn set-effect-handler
  "Sets a function to perform as the handler for a given effect type.
   Handlers take two parameters, the current grammar state and the effect tag and return
   the updated grammar state.
   They are executed after the rule has been selected."
  [grammar effect-type handler-fn]
  (assoc-in grammar [:effects effect-type] handler-fn))

(defn- get-body-str
  [type body]
  (->> (concat [type] body)
       (map name)
       (str/join ".")))

(defn valid-rule?
  "Returns true if the given rule body's conditions match the current grammar state."
  [grammar rule]
  (try
    (let [condition-types        (keys (:conditions grammar))
          get-conditions-of-type (fn [type]
                                   (-> rule :tags type))
          condition-valid?       (fn [type grammar condition]
                                   (try
                                     ((-> grammar :conditions type) grammar condition)
                                     (catch #?(:cljs js/Error, :clj Exception) e
                                       (util/throw-cljc (str "Condition '" (get-body-str type condition) "'") e))))]
      (every? #(every? (partial condition-valid? % grammar)
                       (get-conditions-of-type %))
              condition-types))
    (catch #?(:cljs js/Error, :clj Exception) e
      (util/throw-cljc (str "Condition error in rule '" (:id rule) "'") e))))

(defn execute-rule
  "Returns the new grammar state after performing all effects in the given rule."
  [grammar]
  (try
    (if (-> grammar :selected nil?)
      grammar
      (reduce (fn [grammar type]             ; Iterate over all effect types
                (reduce (fn [grammar effect] ; Iterate over all effects of given type
                          ((-> grammar :effects type) grammar effect))
                        grammar
                        (-> grammar :selected :tags type)))
              grammar
              (keys (:effects grammar))))
    (catch #?(:cljs js/Error, :clj Exception) e
      (util/throw-cljc (str "Effect error in rule '" (-> grammar :selected :id) "'") e))))
