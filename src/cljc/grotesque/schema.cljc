(ns grotesque.schema
  (:require #?(:cljs [schema.core :as s :include-macros true]
               :clj  [schema.core :as s])))

(def RuleBody
  "A schema for a grotesque rule body"
  {(s/optional-key :text)   [(s/either s/Keyword s/Str)]
   (s/optional-key :bodies) {s/Keyword [[s/Any]]}})

(def Model
  "A schema for a grotesque model."
  {s/Keyword s/Any})

(def Grammar
  "A schema for a grotesque grammar"
  {:rules                       {s/Keyword [RuleBody]}
   :model                       Model
   (s/optional-key :conditions) {s/Keyword (s/pred fn?)}
   (s/optional-key :effects)    {s/Keyword (s/pred fn?)}
   (s/optional-key :generated)  s/Str})