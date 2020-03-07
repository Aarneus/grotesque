(ns grotesque.modifiers
  (:require [clojure.string :as string]
            [grotesque.util :as util]))

(defn set-modifier
  "Sets the function that takes in a generated string and returns a transformed version.
   See `docs/modifiers.md` for more details."
  [grammar key modifier-fn]
  (assoc-in grammar [:functions :modifier-fns key] modifier-fn))

(defn check-modifiers
  "Throws an error if the given modifiers do not have a corresponding function in the grammar"
  [grammar modifiers]
  (let [modifier-fns (-> grammar :functions :modifier-fns)
        missing-mods (remove #(contains? modifier-fns %) modifiers)]
    (when (not-empty missing-mods)
      (->> missing-mods
           (map #(str "'" (name %) "'"))
           (string/join ", ")
           (str "Modifiers not found: ")
           util/throw-cljc)
      (util/throw-cljc (str "Modifiers ")))))

(defn get-modified-text
  "Returns the text with the modifiers applied.
   See `docs/modifiers.md` for more details."
  [text grammar modifiers]
  (let [modifier-fns (-> grammar :functions :modifier-fns)]
    (reduce (fn [s function] (function s))
            (or text "")
            (map modifier-fns modifiers))))