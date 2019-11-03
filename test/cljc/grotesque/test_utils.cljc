(ns grotesque.test-utils
  (:require [clojure.data :as data]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))

(defn test-diff
  "Tests the difference between two maps"
  [& args]
  (let [[in-first in-second in-both] (apply data/diff args)]
    (is (= in-first nil))
    (is (= in-second nil))))

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
   :set-time    [["" :set.time.night]
                 ["" :set.time.evening]
                 ["" :set.time.day]]
   :set-gender  [["" :set.actor.gender.he]
                 ["" :set.actor.gender.she]
                 ["" :set.actor.gender.they]]
   :story       ["#set-time##set-gender#The #sky# was looming overhead. So #they# adjusted #their# glasses."]})

(def test-rules-processed
  {:color [{:text ["black"], :when [[:time :night]]}
           {:text ["red"], :when [[:time :evening]]}
           {:text ["blue"], :when [[:time :day]]}],
  :sky   [{:text [:color " sky"]}]
  :they  [{:text ["he"], :when [[:actor :gender :he]]}
          {:text ["she"], :when [[:actor :gender :she]]}
          {:text ["they"], :when [[:actor :gender :they]]}]
  :their [{:text ["his"], :when [[:actor :gender :he]]}
          {:text ["her"], :when [[:actor :gender :she]]}
          {:text ["their"], :when [[:actor :gender :they]]}],
  :set-time [{:text [], :set [[:time :night]]}
             {:text [], :set [[:time :evening]]}
             {:text [], :set [[:time :day]]}],
  :set-gender [{:text [], :set [[:actor :gender :he]]}
               {:text [], :set [[:actor :gender :she]]}
               {:text [], :set [[:actor :gender :they]]}],
  :story      [{:text [:set-time :set-gender "The " :sky " was looming overhead. So " :they " adjusted " :their " glasses."]}]})