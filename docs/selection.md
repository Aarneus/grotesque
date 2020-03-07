[Back to overview](overview.md)

## Rule selection
The value of `:selector-fn` in the grammar map is used for selecting a rule.  
The function takes three arguments, the current grammar, the selected for rule head and a vector of valid rule bodies.  
It returns a new grammar with the selected rule body `assoc`:ed under the `:selected` key:  
```clojure
(defn selector-fn [grammar head bodies]
  (assoc grammar :selected (rand-nth bodies)))
```

The default selector function above just picks a rule at random.

Note that you can use the `:data` key in the grammar to store metadata about the rules.  
For example, you can save the rule id of every rule selected and prefer those that have been selected least:  
```clojure
(grotesque.core/set-selector 
  grammar 
  (fn [old-grammar head bodies]
    (let [selected (->> bodies
                        (sort-by #(-> old-grammar :data :times-selected ((:id %))))
                        first)]
      (-> old-grammar
          (update-in [:data :times-selected (:id selected)] #(if (nil? %) 1 (inc %)))
          (assoc :selected selected)))))
```

### Metaselectors
If you wanted to e.g. have a character’s name appear in the text, 
you could generate a rule for the character that has their name on it.  
But you could also make a selector that does the job for you:  
```clojure
(-> grammar
    (grotesque.core/set-selector (fn [old-grammar head bodies]
                                  (let [names (-> old-grammar :data :names)]
                                    (if (contains? names head)
                                      (assoc old-grammar :selected {:id   :meta
                                                                    :text [(get names head)]})
                                      (assoc old-grammar :selected (rand-nth bodies))))))
    (assoc-in [:data :names] {:hero "Ilsa", :villain "Rodric", :donor "Mustiff"}))
```
Note that rule heads are keywords without dots.  
So `:story` is a valid head but `:dance.time` is not.  
This is because it would interfere with [text output modifiers](modifiers.md).

There might be a more convenient way to define metaselectors in the future. 