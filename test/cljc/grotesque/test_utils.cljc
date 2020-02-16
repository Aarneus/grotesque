(ns grotesque.test-utils
  (:require [clojure.data :as data]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])
            #?(:cljs [schema.core :as s :include-macros true]
               :clj  [schema.core :as s])))

(defn sleep
  "Sleeps the given number of milliseconds.
   ClojureScript variant is untested."
  [ms]
  #?(:cljs (reduce + (range 1000000)) ; Do random stuff to make time pass
     :clj  (Thread/sleep ms)))

(defn test-diff
  "Tests the difference between two maps"
  [& args]
  (let [[in-first in-second _in-both] (apply data/diff args)]
    (is (nil? in-first))
    (is (nil? in-second))))

(defn validate-schema
  "Validates the given value against the given schema"
  [schema value]
  (is (map? (s/validate schema value))))


(def test-rules
  "A set of mock rules for unit testing.
   Unpacked here for clarity."
  {:color       [["black" :when.time.night]
                 ["red" :when.time.evening]
                 ["blue" :when.time.day]]
   :sky         ["#color# sky"]
   :they        [["he" :when.actor.gender.he]
                 ["she" :when.actor.gender.she]
                 ["they" :when.actor.gender.they]]
   :their       [["his" :when.actor.gender.he]
                 ["her" :when.actor.gender.she]
                 ["their" :when.actor.gender.they]]
   :set-time    [[:set.time.night]
                 [:set.time.evening]
                 [:set.time.day]]
   :set-gender  [[:set.actor.gender.he]
                 [:set.actor.gender.she]
                 [:set.actor.gender.they]]
   :story       ["#set-time##set-gender#The #sky# was looming overhead. So #they# adjusted #their# glasses."]})

(def test-rules-processed
  {:color [{:id :color-0, :text ["black"], :tags [[:when :time :night]]}
           {:id :color-1, :text ["red"], :tags [[:when :time :evening]]}
           {:id :color-2, :text ["blue"], :tags [[:when :time :day]]}],
  :sky   [{:id :sky-0, :text [:color " sky"]}]
  :they  [{:id :they-0, :text ["he"], :tags [[:when :actor :gender :he]]}
          {:id :they-1, :text ["she"], :tags [[:when :actor :gender :she]]}
          {:id :they-2, :text ["they"], :tags [[:when :actor :gender :they]]}]
  :their [{:id :their-0, :text ["his"], :tags [[:when :actor :gender :he]]}
          {:id :their-1, :text ["her"], :tags [[:when :actor :gender :she]]}
          {:id :their-2, :text ["their"], :tags [[:when :actor :gender :they]]}],
  :set-time [{:id :set-time-0, :tags [[:set :time :night]]}
             {:id :set-time-1, :tags [[:set :time :evening]]}
             {:id :set-time-2, :tags [[:set :time :day]]}],
  :set-gender [{:id :set-gender-0, :tags [[:set :actor :gender :he]]}
               {:id :set-gender-1, :tags [[:set :actor :gender :she]]}
               {:id :set-gender-2, :tags [[:set :actor :gender :they]]}],
  :story      [{:id :story-0,
                :text [:set-time :set-gender "The " :sky " was looming overhead. So " :they " adjusted " :their " glasses."]}]})