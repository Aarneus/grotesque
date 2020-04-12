[Back to overview](overview.md)

## Wildcard variables
Variables allow the grammar creator to mark some parts of tags as dynamic.  
This means that during generation these parts are replaced with keywords provided by the creator.  
This is very useful when you don't know what keywords will be in use at runtime or don't want to 
write rules for each individual case.

An example grammar of using variables for checking if there are siblings:
```clojure
{:description [["Siblings exist.", :X.parent.Z, :Y.parent.Z, :not.X.Y]]}
```

Where we store the children under the grammar key `:data` as a map:
```clojure
{:mary    {:parent :jones}
 :harriet {:parent :jones}
 :jones   {:parent :bartholomew}
 :matthew {:parent :jones}
 :isaac   {:parent :matthew}}
```

In the above example, we want to replace instances of `X`, `Y` and `Z` with the keywords 
identifying the people in the world model, so some valid versions of the description rule above would be e.g.:  
```clojure
["Siblings exist.", :mary.parent.jones, :matthew.parent.jones, :not.mary.matthew]
["Siblings exist.", :matthew.parent.jones, :mary.parent.jones, :not.matthew.mary]
["Siblings exist.", :harriet.parent.jones, :matthew.parent.jones, :not.harriet.matthew]
["Siblings exist.", :mary.parent.jones, :harriet.parent.jones, :not.mary.harriet]
```
Note that because the conditions are symmetrical, the results have each symmetrical pair of values as well.

For this we need to first set the [validators](model.md#conditions) for checking parentage and non-equality:
```clojure
(-> grammar
    (grotesque/set-validator :parent (fn [grammar _ [_ kid parent]]
                                       (= parent (get-in grammar [:data kid :parent]))))
    (grotesque/set-validator :not (fn [grammar _ [_ & args]]
                                    (apply distinct? args))))
```

And finally the actual variable functions.  
The variable functions take two parameters, the grammar and the variable keyword, and return a list of possible (keyword)
values for that variable:
```clojure
(reduce (fn [grammar tag-part]
          (grotesque/set-variable grammar tag-part (fn [grammar var]
                                                     (-> grammar :data keys))))
        grammar
        [:X :Y :Z])
```

Note that the variables apply to all keywords in the grammar.  
This includes the [rule heads](selection.md) and [modifiers](modifiers.md).  
This means that you can e.g. attach a pirate accent to certain characters etc.
