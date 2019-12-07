# grotesque

A Clojure(script) library for text generation.

Uses a context-free grammar similar to [Tracery](https://tracery.io/) but with added 
[state conditions and effects on rules](https://emshort.blog/2019/11/29/storylets-you-want-them/).

Honestly if I didn't want to to use this library with Clojure this would most likely be a Tracery extension.

### Warning!
This library is still a work-in-progress and hasn't e.g. been fully tested with ClojureScript.

### Guiding principles:
- Full support for both Clojure and ClojureScript
- Purely functional and data-driven
- Compatibility with Tracery (to some extent)

### Features to be done for 1.0
- [x] Rule parsing
- [x] Text generation
- [x] Effects and conditions
- [x] Unit testing & schema validation
- [x] User documentation
- [ ] Variable names
- [ ] Final testing and upload

## Usage
Grotesque works by generating text according to a grammar defined by you.

### An example grammar
```clojure
{"color"    ["red" "blue" "green"]
 "vehicle"  [["car"   :set.vehicle.car] 
             ["boat"  :set.vehicle.boat] 
             ["plane" :set.vehicle.plane]]
 "terrain"  [["on the road"  :when.vehicle.car]
             ["in the water" :when.vehicle.boat]
             ["in the sky"   :when.vehicle.plane]]
 "position" ["A #color# #vehicle# is #terrain#."]}
```
### Example output
``` 
A red car is on the road.
A green boat is in the water.
A blue plane is in the sky.
A red boat is in the water.
```
The basic functionality of the grammar is that a word in the text that is surrounded by hashtags # is replaced with a chosen rule of the given type. Here `"The #color# ball."` is replaced with e.g. `"The blue ball."`

This simple example also demonstrates how the state handling works. The `:vehicle` rules set the type of vehicle and the `:terrain` checks it to make sure that the text `A red boat is in the sky.` is never generated.

These are the most basic state handling operators, but the system is designed so you can add easily add your own. 
See the documentation below for more examples.

## Quickstart
The simplest way to start playing with Grotesque is to generate a simple grammar with the default model enabled:
```clojure
(ns example.core
  (:require [grotesque.core :as grotesque]
            [grotesque.model :as model]))

(-> {"color"    ["red" "blue" "green"]
     "vehicle"  [["car"   :set.vehicle.car] 
                 ["boat"  :set.vehicle.boat] 
                 ["plane" :set.vehicle.plane]]
     "terrain"  [["on the road"  :when.vehicle.car]
                 ["in the water" :when.vehicle.boat]
                 ["in the sky"   :when.vehicle.plane]]
     "position" ["A #color# #vehicle# is #terrain#."]}
    (grotesque/create-grammar)
    (model/enable-default-model)
    (grotesque/generate "#position#")
    :generated)
```

## Overview of the system
There are four main parts in the Grotesque text generation system:
Grammar structure
Rule syntax
Conditions and effects
Rule selection

### Grammar structure
The grammar is defined as an EDN map. It has the following keys:
```clojure
:rules       {...}  ; The rules of the grammar, as above
:conditions  {...}  ; Validator functions for conditions 
:effects     {...}  ; Handlers for effects
:model       {...}  ; A map of state variables and their values
:picker-fn   #(...) ; The function that picks a rule to apply
:picked-rule [...]  ; The rule picked by the picker-fn
:metadata    {...}  ; Metadata for the rule bodies, e.g. when last run
:generated   "..."  ; The generated text is found here
:errors      [...]  ; Generated errors are found here
```
The `:rules`, `:conditions`, `:effects`, and `:picker-fn` deserve their own sections below. The `:model` is tied to the conditions and effects as the `:metadata` is to the `:picker-fn`. The `:generated` and `:errors` are pretty self-evident.

### Rule syntax
As a context-free grammar, Grotesque has rules that are divided to a **head** consisting of 
a *non-terminal symbol* and a **body** for that head which is composed of a vector 
of *terminal* and *non-terminal symbols*.

None of this is necessary to know and is streamlined away if you are not interested in the academic applications.
It is also of limited use because effects and conditions are not part of the context-free grammar model anyway.

What is relevant is the practical syntax for the rules.

Both of the following rules (and the grammar as a whole) are valid:
```clojure
{:token1  ["The #color# sky."]
 "token2" [:when-any.sky-color, :once, :set.colored-sky]}
```
The head of the rule can be given either as a keyword or a string. It is internally changed to a keyword anyway. However, it should not contain any periods `.` or be a single capital letter e.g. `W`. These are reserved for future features.

A single rule body can consist of just the string (if there are no effects or conditions). 
Otherwise it should be a vector. 
If the first value of the vector is a string then that is considered the text that is generated when 
that rule is selected. Otherwise an empty string is generated. 
This is useful for meta-rules that should not affect the generated text.
 The rest of the rule body should contain only effects and conditions in keyword form.

Each rule body gets a generated id that it can be recognized by, 
with the format of `:token-x` where `token` is the `head` of the rule and `x` is a counter that starts 
from 0 and increases for every added rule. 

So for example the rules
```clojure
{:color ["red" "green" "blue" ...]}
```
Would have ids red = `:color-0`, green = `:color-1`, blue = `:color-2`, etc.

This is referenced in error messages so you can find the broken syntax more easily.

Note also that the order of the rules matters for the generated id’s and performance may be affected if you
e.g. load the rules in different order between sessions etc.

Note that this is the syntax for inputting new rules. 
If you wish to delve deeper into the data structure, the docstrings in the library will tell you what rules
look like after they are processed into the grammar.

### Conditions and effects
Conditions and effects provide the grammar rules with the power of a Storylet. 

The conditions are clauses that must be valid or the rule can’t be picked. The effects are clauses that come into effect after the rule is picked. Both always consist of a keyword with parts separated by periods `.`.

There is a default model provided but it is not required for the grammar to function. It has the following conditions (with a car color example):
```clojure
:when.car.color.red     ; Valid when the car variable’s color is red
:when-any.car.color     ; Valid when the car has any color
:when-not.car.color.red ; Valid when the car’s color is not red
:when-nil.car.color     ; Valid when the car doesn’t have a color
```
It also has the following effects:
```clojure
:set.car.color.red      ; Sets the car’s color to red
:unset.car              ; Removes the car variable entirely
```
And the following that are both conditions and effects:
```clojure
:when-set.car.color.red ; Sets the color to red if it was nil or red
```
Please note that the opportunities for different conditions and effects are not limited to simple state manipulation. One could also make effects that cause sounds, conditions that increment a variable by a certain amount or check the system clock etc.

Adding new validators and handlers is easy. See the `grotesque.model`-namespace for the default examples and more documentation.

### Rule selection
The value of `:picker-fn` in the grammar map is used for selecting a rule body from the list of valid rule bodies. The function takes two arguments, the current grammar and a vector of valid rule bodies. It returns a new grammar with the selected body `assoc`:ed under the `:picked-rule` key.

There are some sample pickers included in the `grotesque.picker-fns` namespace that provide more information. 

Note that you can use the rule body’s id for identifying the rules according to some collected metadata. The metadata is intended for any custom pickers you might need, e.g. it could be used to store how many times a rule body was picked or considered for picking for testing purposes.

The default picker is `grotesque.picker-fns/random-picker-fn` which just picks one rule randomly.

## Notes on future features
The foremost feature missing from Grotesque at the moment is pattern matching. A second important missing feature is Tracery-style modifiers. 

### Pattern matching
This will allow the user to specify *wildcard* model variables. These would be parts of conditions and effects that would be matched with an existing variable when the rule was selected.

A partial example of pattern matching:
```clojure
{:rules {:event ["#X# punches #Y#!" 
                 :when.X.relationship.Y.hostile 
                 :set.Y.relationship.X.hostile]}
 :model {:jack {:relationship {:john :hostile}}
         :john {:relationship {:jack :neutral}}}}
```
In this example the `X` and `Y` are the wildcards. When checking for valid rules, this rule will check if there is any combination of valid, different variables that will fit these requirements and inject them into the given positions.

In this example, `X` would match with `:jack` and `Y` would match with `:john`. Therefore, before the conditions were checked the rule body would be morphed into the following form:
```clojure
["#jack# punches #john#!"
 :when.jack.relationship.john.hostile
 :set.john.relationship.jack.hostile]
```
The rule itself would of course remain unchanged and ready to match different variables next time.

### Modifiers
These should match the standards set by Tracery. Basically these are middleware functions that take the output of the generated text and modify it somehow.

An example would be e.g. capitalization of the first letter:
```clojure
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
This would allow the user much more control over the generation.

### Possible other future features
Other future features after 1.0 and the ones above could include:
- Optimizations
- Sample project (NaNoGenMo 2020?)
- Visualization
- Use from Java/JavaScript

## About
Heavily inspired by the work of 
[Kate Compton](https://github.com/galaxykate), 
[Elan Ruskin](https://www.gdcvault.com/play/1015317/AI-driven-Dynamic-Dialog-through) and 
[Emily Short](https://emshort.blog/).

The [name](https://www.merriam-webster.com/dictionary/grotesque) itself is 
a riff on [Tracery](https://www.merriam-webster.com/dictionary/tracery).

Distributed under the MIT Expat License.
