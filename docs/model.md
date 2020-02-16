[Back to overview](overview.md)

## State handling
Usually when generating text it is not enough to have the grammar select rules totally at random.  
There are two basic problems here. 

First we would like to constrain the selection depending on some environment variable by e.g. only selecting winter-related phrases during winter. 

Secondly we would like to affect these constraints during the generation process by e.g. knowing that a previous rule resulted in it now being winter.

Grotesque allows us to avoid these problems by combining two common solutions; adding tags to rules and making each rule a storylet with conditions and effects.

### Tags
All conditions and effects are tags but they can also be used by themselves.  
You use them by attaching a keyword marker to the rule:  
```clojure
{:weather [["snowing" :winter]
           ["scorching" :summer]
           ["raining"]]}
```

You can attach as many tags as you want:  
```clojure
{:weather [["snowing" :winter, :when.rain.heavy, :mentioned.snow]
           ["scorching" :summer :mentioned.heat]
           ["windy" :mentioned.wind]]}
```

Internally tags are split into a vector with the different parts separated by a period:  
```clojure
:when.rain.heavy => [:when :rain :heavy]
```
The reason for this is that the first part of the tag is used to identify conditions and effects.  
These are detailed later on this page.

The tags can be used for rule selection, by e.g. having the selector only accept rules with the `:winter` tag during winter:  
```clojure
(defn winter-selector-fn 
  [grammar head bodies]
  (assoc grammar :selected (->> bodies
                                (filter #(contains? (:tags %) :winter))
                                rand-nth)))
```
See the [documentation for rule selection](selection.md) for more information how this works.

At first the fact that the condition has to be a keyword might seem limiting.  
But remember that you can make the validator do whatever you want based on those keywords.  
In effect you are creating a miniature language. Preferably a high-level one.  
So while you can make a tag that contains e.g. numeric information that is then parsed from the keyword, it is considered poor form to do so. 

Consider whether you really need that level of granularity in your generation.  
Does it really matter whether the protagonist has 10, 34, or 312 coins or is it enough to 
have a handful of states such as penniless, poor, and rich?

Also remember that you should do the heavy lifting behind the scenes and not inside the tags.   
If you want e.g. complex weather simulation you should not litter your rules with tags like `:temperature.below.50` but instead codify that temperature as e.g. `:winter`.

This will make writing and maintaining your grammar much, much easier.  
The following sections on conditions and effects elaborate on how exactly you can achieve this.

### Conditions
Conditions are tags that affect whether a rule can be chosen.  
All conditions of a rule must be valid or it is not given to the rule selector.  
You make a tag into a condition by adding a validator function for that tag:  
```clojure
(-> {:weather [["snowing" :season.winter]
               ["scorching" :season.summer]
               ["raining"]]}
    grotesque/create-grammar
    (grotesque/set-validator :season (fn [grammar [value]]
                                       (= value (get-in grammar [:data :season])))))
```
This will ensure that rules related to summer are not chosen in winter and vice versa.  

Note that the preferred, purely functional way for conditions to handle data is to read it from the grammar’s data value, as above.  
There is however nothing to stop you from using non-functional validators, if you so choose.  
This is useful in testing, when you might like to e.g. log a validator’s results.

The parameters of a validator are the grammar, the rule id and the condition tag being validated.  
It should always return a truthy value when the condition is valid and falsy (false or nil) when not.  

Conditions are identified and dispatched to the validator by the first part of the keyword tag.  
The rest of the parts are considered parameters for the condition.  
E.g. in the above example the first part `:season` is used to identify the validator which is sent the rest of
the condition, `[:winter]`.

Note that conditions have to all be valid for a rule to be valid.  
There is no built in way to make logical OR relationships between conditions.  
This is by design.

As with tags in general, it is not recommended to make conditions too low level.  
Instead of making conditions like `:hero.is.injured.or.sick.or.tired` and then making a validator to handle all that 
consider simplifying it by having for example a `:hero.cant-move` condition whose validator then takes those states into account.

It will make your rules much cleaner and easier to update if you, for instance, decide that the hero can also be unconscious.  
Then you would only need to update the validator and not the rules.

Another thing to keep in mind though that this doesn’t mean some ‘metaconditons’ are not useful.  
For example, you probably want some variation of the `:not.*` condition which will be true if the condition tag it
precedes is not valid:  
```clojure
(grotesque/set-validator grammar :not (fn [old-grammar rule-id [tag & args]]
                                        (not ((-> old-grammar :conditions tag) old-grammar args))))
```

An important counterpart to conditions are effects.  
They can be used to affect the results of subsequent conditions.  
Note that there is nothing that excludes a tag from being both a condition and an effect, as is elaborated upon in the next section.

### Effects
Effects are tags that update the grammar after the rule they are attached to has been chosen by the rule selector.  
This is preferred to be handled in a purely functional way (similar to conditions) but there is nothing to stop you from having side-effects for e.g. testing purposes.  

You make a tag into an effect by adding a handler function for that tag:  
```clojure
(grotesque/set-handler grammar :set (fn [old-grammar rule-id [variable value]]
                                      (assoc-in old-grammar [:data variable] value)))
```

A handler takes as parameters the current grammar, the rule id and the effect tag and returns the updated grammar. 

The `:data` key in the grammar has been reserved for communicating the world model between effects, conditions and 
rule selectors and can include any data you need.

Remember that effects, like conditions and tags in general, are abstractions and don’t need to be simple value updates. 

Remember also that effects can also be conditions. 
For example, maybe you don’t want to set up the model before generation and 
would like the first time winter is referred to also set it as the current season:  
```clojure
(-> grammar
    (grotesque/set-handler :season (fn [grammar [season]]
                                     (assoc-in grammar [:data :season] season)))
    (grotesque/set-validator :season (fn [grammar [season]]
                                       (let [current-season (-> grammar :data :season)]
                                         (or (nil? current-season)
                                             (= season current-season))))))
```
This will ensure that no matter what season we encounter first, all the following rules selected will be compatible with it.  
This is one way of simplifying complex logic by baking it in the tag handling.  
The world model for your text is probably specific to your project.  
Use this to your advantage when defining tag behaviour.
