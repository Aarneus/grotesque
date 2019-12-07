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

(defmacro try-catch-cljc
  "Catches JavaScript Errors or Java Exceptions depending on the language and extracts their message.
   Appends it to the given message and stores it in the grammar's `:errors` vector.
   Is paired with `throw-cljc`."
  [grammar message & body]
  (if (:ns &env) ; Check if macro in cljs
    `(try
       (do ~@body)
       (catch js/Error e#
         (-> ~grammar
             (dissoc :picked-rule)
             (add-error (str ~message "\n" e#)))))
    `(try
       (do ~@body)
       (catch Exception e#
         (-> ~grammar
             (dissoc :picked-rule)
             (add-error (str ~message "\n" (.getMessage e#))))))))

(defn parse-symbol-string
  "Returns a vector representation of the terminal and non-terminal symbols in the given string.
   E.g. \"There is an #animal# here.\" => [\"There is an \" :animal \" here.\"]"
  [s]
  (if (string? s)
    (->> (string/split s #"#")
         (map #(if (even? %1) %2 (keyword %2)) (range))
         (remove #(or (= nil %) (= "" %)))
         vec)
    ""))