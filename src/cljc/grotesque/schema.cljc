(ns grotesque.schema
  (:require #?(:cljs [schema.core :as s :include-macros true]
               :clj  [schema.core :as s])))

(def RuleBody
  "A schema for a grotesque rule body."
  {:id                    s/Keyword
   (s/optional-key :text) [(s/pred #(or (string? %) (keyword? %)))]
   (s/optional-key :tags) {s/Keyword [[s/Any]]}})

(def Grammar
  "A schema for a grotesque grammar."
  {:rules                       {s/Keyword [RuleBody]}
   (s/optional-key :data)       s/Any
   (s/optional-key :conditions) {s/Keyword (s/pred fn?)}
   (s/optional-key :effects)    {s/Keyword (s/pred fn?)}
   (s/optional-key :functions)  {(s/optional-key :selector-fn) (s/pred fn?)}
   (s/optional-key :selected)   RuleBody
   (s/optional-key :generated)  s/Str
   (s/optional-key :errors)     [s/Str]})