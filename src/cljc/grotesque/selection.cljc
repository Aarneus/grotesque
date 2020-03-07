(ns grotesque.selection)

(defn default-selector-fn
  [grammar head bodies]
  (assoc grammar :selected (rand-nth bodies)))

(defn set-selector
  "Sets the function the grammar uses to select a rule from all valid options.
   See `docs/selection.md` for more details."
  [grammar selector-fn]
  (assoc-in grammar [:functions :selector-fn] selector-fn))

(defn get-selector-fn
  "Returns the chosen picker for the grammar."
  [grammar]
  (or (-> grammar :functions :selector-fn)
      default-selector-fn))

(defn picked-some?
  "Returns true if a rule has been picked"
  [grammar]
  (-> grammar :selected some?))