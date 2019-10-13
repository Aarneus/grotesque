(ns grotesque.util
  (:require [clojure.string :as string]))

(defn parse-symbol-string
  "Returns a vector representation of the terminal and non-terminal symbols in the given string.
   E.g. \"There is an #animal# here.\" => [\"There is an \" :animal \" here.\"]"
  [s]
  (->> (string/split s #"#")
       (zipmap (range))
       (map #(if (even? (first %))
               (second %)
               (keyword (second %))))
       (remove #(= "" %))
       vec))




