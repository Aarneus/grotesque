(ns grotesque.util
  (:require [clojure.string :as string]))

(defn add-error
  "Adds an error message to the stack"
  [grammar message]
  (update grammar :errors conj message))

(defn throw-cljc
  "Throws the given message as either a JavaScript Error or Java Exception.
   Wraps an optional js/Error or java.lang.Exception.
   Is paired with `catch-cljc`."
  [message & [error]]
  (let [message (if (some? error)
                  (str message ":\n" #?(:cljs error
                                        :clj  (if-let [msg (.getMessage error)]
                                                msg
                                                (-> error .getClass .getCanonicalName))))
                  message)]
    #?(:cljs (throw (js/Error. message))
       :clj  (throw (Exception. ^String message)))))

(defn try-catch
  "Wraps the given function inside a try-catch block"
  [grammar message performed-fn]
  (try
    (performed-fn)
    (catch #?(:clj Exception :cljs js/Error) e
      (-> grammar
          (dissoc :selected)
          (add-error (str message "\n" #?(:clj (.getMessage e) :cljs e)))))))

(defn parse-symbol-string
  "Returns a vector representation of the terminal and non-terminal symbols in the given string.
   E.g. \"There is an #animal# here.\" => [\"There is an \" :animal \" here.\"]
   Brackets are also allowed if you prefer them over hashtags."
  [s]
  (if (string? s)
    (->> (string/split s #"(\[|\]|#)")
         (remove #{"[" "]" "#"}) ; ClojureScript split leaves the separators sometimes
         (map #(if (even? %1) %2 (keyword %2)) (range))
         (remove #(or (= nil %) (= "" %)))
         vec)
    ""))