(ns grotesque.util
  (:require [clojure.string :as string]))

(defn parse-symbol-string
  "Returns a vector representation of the terminal and non-terminal symbols in the given string.
   E.g. \"There is an #animal# here.\" => [\"There is an \" :animal \" here.\"]"
  [s]
  (->> (string/split s #"#")
       (map #(if (even? %1) %2 (keyword %2)) (range))
       (remove #(or (= nil %) (= "" %)))
       vec))
