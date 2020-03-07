[Back to overview](overview.md)

## Text output modifiers
These are the same as modifiers in [Tracery](https://www.tracery.io/).  
Basically these are middleware functions that take the output of the generated text and modify it somehow.

An example would be e.g. capitalization of the first letter:
```
{:name   ["Lisa" "Tom" "Marie"]
 :animal ["wombat" "giraffe" "bear"]
 :home   ["#name# the #animal.capitalize# lives here."]}
```
Which would then output:
```
Tom the Wombat lives here.
Lisa the Bear lives here.
Marie the Giraffe lives here.
```
This allows the user much more control over the generation.

Unlike Tracery, Grotesque does not come with a built-in library of modifiers, but they are instead added by the user.  
The above example would be added as follows:
```clojure
(grotesque.core/set-modifier grammar :capitalize clojure.string/capitalize)
```

Which would then capitalize the first letter of the generated text.

Modifiers can also be combined, as in the following example:
```clojure 
(ns example.core
  (:require [grotesque.core :as g]))

(defn piratize [s]
  (clojure.string/replace s #"[aeiouy]r" "$0rr"))

(defn pluralize 
  "Only works for basic cases"
  [s]
  (str s "s"))

(-> {:food ["banana" "apple" "pear" "orange"]
     :pirate-recipe ["Mash three #food.pirate.plural#."]}
    (g/create-grammar)
    (g/set-modifier :pirate piratize)
    (g/set-modifier :plural pluralize)
    (g/generate "#pirate-recipe#")
    :generated)
```
The above would generate one of the following:
```
Mash three orrranges.
Mash three bananas.
Mash three pearrrs.
Mash three apples.
```

When there are multiple modifiers, they are processed in the order given.

Another thing to keep in mind with modifiers is making sure that they behave as expected.  
For instance, in the above example the `plural` modifier would not work if one of the food options was
e.g. `fish` as it would produce `fishs` not `fish`.  
