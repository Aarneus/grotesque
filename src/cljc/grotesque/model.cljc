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
  [[head & tail]]
  (->> (map name tail)
       (concat [(str head)])
       (str/join ".")))

(defn- get-error-str [prefix rule tag]
  (str prefix " error in rule '" (:id rule) "' in tag '" (get-body-str tag) "'"))

(defn valid-rule?
  "Returns true if the given rule body's conditions match the current grammar state.
   Tags that are conditions are checked in their original order.
   This means that you should place the most restrictive tags first and non-conditions last."
  [grammar rule]
  (every? (fn [tag]
            (try
              (if-let [validator-fn (-> grammar :conditions (get (first tag)))]
                (validator-fn grammar tag)
                true)
              (catch #?(:cljs js/Error, :clj Exception) e
                (util/throw-cljc (get-error-str "Condition" rule tag) e))))
          (:tags rule)))

(defn execute-rule
  "Returns the new grammar state after performing all effects in the selected rule.
   Tags effects are executed in their original order."
  [grammar]
  (if (-> grammar :selected nil?)
      grammar
      (reduce (fn [grammar tag]
                (try
                  (if-let [handler-fn (-> grammar :effects (get (first tag)))]
                    (handler-fn grammar tag)
                    grammar)
                  (catch #?(:cljs js/Error, :clj Exception) e
                    (util/throw-cljc (get-error-str "Effect" (:selected grammar) tag) e))))
              grammar
              (-> grammar :selected :tags))))
