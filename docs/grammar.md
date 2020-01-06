[Back to overview](overview.md)

## Grammar structure
The grammar is defined as an EDN map. It has the following keys:
```clojure
{:rules       {...} ; The rules of the grammar, as above
 :conditions  {...} ; Validator functions for conditions 
 :effects     {...} ; Handlers for effects
 :data        {...} ; A map of state variables and their values
 :functions   {...} ; The functions used by the grammar
 :selected    {...} ; The rule selected by the selector-fn
 :generated   "..." ; The generated text is found here
 :errors      [...] ; Generated errors are found here
}
```

### Rule syntax
As a context-free grammar, Grotesque has rules that are divided to a *head* and *bodies* for that head.  
The bodies are contain their id, an optional string *phrase* and zero or more *tag* keywords.

Both of the following rules (and the grammar as a whole) are valid:  
```clojure
{:token1  ["The #color# sky."]
 "token2" [:when-any.sky-color, :once, :set.colored-sky]}
```

The head of the rule can be given either as a keyword or a string.  
It is internally changed to a keyword anyway.  
However, it should not contain any periods `.` or be a single capital letter e.g. `W`.  
These are reserved for future features.

A single rule body can consist of just the phrase, if there are no effects or conditions.  
Otherwise it should be a vector. 

If the first value of the vector is a string then that is considered the phrase that is generated when that rule is selected.   
Otherwise an empty string is generated.  
This is useful for meta-rules that should not affect the generated text.

The rest of the rule body should contain only effects and conditions in keyword form.  

Each rule body gets a generated id that it can be recognized by, 
with the format of `:token-x` where `token` is the `head` of the rule and `x` is a counter 
that starts from 0 and increases for every added rule.  
This is referenced in e.g. error messages so you can find the broken syntax more easily.

Note also that the order of the rules matters for the generated ids and performance may be affected
 if you e.g. load the rules in different order between sessions etc.
