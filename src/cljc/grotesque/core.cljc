(ns grotesque.core
  (:require [grotesque.rules :as rules]))

(defn create-grammar
  "Initialize a new grammar, with optional rules data."
  ([] (create-grammar nil))
  ([rules] (if (some? rules)
             (rules/add-rules {} rules)
             {})))