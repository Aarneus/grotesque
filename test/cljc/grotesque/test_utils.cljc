(ns grotesque.test-utils
  (:require [clojure.data :as data]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))

(defn test-diff
  "Tests the difference between two maps"
  [& args]
  (let [[in-first in-second _in-both] (apply data/diff args)]
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
   :set-time    [[:set.time.night]
                 [:set.time.evening]
                 [:set.time.day]]
   :set-gender  [[:set.actor.gender.he]
                 [:set.actor.gender.she]
                 [:set.actor.gender.they]]
   :story       ["#set-time##set-gender#The #sky# was looming overhead. So #they# adjusted #their# glasses."]})

(def test-rules-processed
  {:color [{:text ["black"], :bodies {:when [[:time :night]]}}
           {:text ["red"], :bodies {:when [[:time :evening]]}}
           {:text ["blue"], :bodies {:when [[:time :day]]}}],
  :sky   [{:text [:color " sky"]}]
  :they  [{:text ["he"], :bodies {:when [[:actor :gender :he]]}}
          {:text ["she"], :bodies {:when [[:actor :gender :she]]}}
          {:text ["they"], :bodies {:when [[:actor :gender :they]]}}]
  :their [{:text ["his"], :bodies {:when [[:actor :gender :he]]}}
          {:text ["her"], :bodies {:when [[:actor :gender :she]]}}
          {:text ["their"], :bodies {:when [[:actor :gender :they]]}}],
  :set-time [{:bodies {:set [[:time :night]]}}
             {:bodies {:set [[:time :evening]]}}
             {:bodies {:set [[:time :day]]}}],
  :set-gender [{:bodies {:set [[:actor :gender :he]]}}
               {:bodies {:set [[:actor :gender :she]]}}
               {:bodies {:set [[:actor :gender :they]]}}],
  :story      [{:text [:set-time :set-gender "The " :sky " was looming overhead. So " :they " adjusted " :their " glasses."]}]})