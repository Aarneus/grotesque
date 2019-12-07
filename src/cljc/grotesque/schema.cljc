(ns grotesque.schema
  (:require #?(:cljs [schema.core :as s :include-macros true]
               :clj  [schema.core :as s])))

(def RuleBody
  "A schema for a grotesque rule body."
  {:id                      s/Keyword
   (s/optional-key :text)   [(s/pred #(or (string? %) (keyword? %)))]
   (s/optional-key :bodies) {s/Keyword [[s/Any]]}})

(def Model
  "A schema for a grotesque model."
  {s/Keyword s/Any})

(def Grammar
  "A schema for a grotesque grammar."
  {:rules                         {s/Keyword [RuleBody]}
   (s/optional-key :model)        Model
   (s/optional-key :metadata)     {s/Keyword {s/Keyword s/Any}}
   (s/optional-key :conditions)   {s/Keyword (s/pred fn?)}
   (s/optional-key :effects)      {s/Keyword (s/pred fn?)}
   (s/optional-key :functions)    {(s/optional-key :picker-fn) (s/pred fn?)}
   (s/optional-key :picked-rule)  RuleBody
   (s/optional-key :generated)    s/Str
   (s/optional-key :errors)       [s/Str]})