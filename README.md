# grotesque

A Clojure(script) library for text generation.

Uses a context-free grammar similar to [Tracery](https://tracery.io/) but with added 
[state conditions and effects on rules](https://emshort.blog/2019/11/29/storylets-you-want-them/).

Honestly if I didn't want to to use this library with Clojure this would most likely be a Tracery extension.

![Tests](https://github.com/Aarneus/grotesque/workflows/Clojure%20CI/badge.svg)
[![Clojars Project](https://img.shields.io/clojars/v/grotesque.svg)](https://clojars.org/grotesque)

## A text-generation library
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
### The example output for "#position#"
``` 
A red car is on the road.
A green boat is in the water.
A blue plane is in the sky.
A red boat is in the water.
```
The basic functionality of the grammar is that a word in the text that is surrounded by hashtags # is replaced with the phrase in a corresponding rule.  
Here `"The #color# ball."` is replaced with e.g. `"The blue ball."`

Note that rules also accept either strings or keywords as their keys.  
They also accept brackets ([ and ]) instead of hashtags.

This simple example also demonstrates how the state handling works. The `:vehicle` rules set the type of vehicle and the `:terrain` checks it to make sure that e.g. `A red boat is in the sky.` is never generated.

Note that the state handling functions are omitted here.  
How they work and how to implement your own is detailed in the [documentation for state](docs/model.md).

## Documentation
Read the docs [here](docs/overview.md).

## Quickstart
The simplest way to start playing with Grotesque is to generate a simple grammar with an equally simple example model:
```clojure
(ns example.core
  (:require [grotesque.core :as grotesque]))

(defn handler-fn [grammar [attribute value]]
  (assoc-in grammar [:data :model attribute] value))

(defn validator-fn [grammar [attribute value]]
  (= value (get-in grammar [:data :model attribute])))

(-> {"color"    ["red" "blue" "green"]
     "vehicle"  [["car"   :set.vehicle.car] 
                 ["boat"  :set.vehicle.boat] 
                 ["plane" :set.vehicle.plane]]
     "terrain"  [["on the road"  :when.vehicle.car]
                 ["in the water" :when.vehicle.boat]
                 ["in the sky"   :when.vehicle.plane]]
     "position" ["A #color# #vehicle# is #terrain#."]}
    (grotesque/create-grammar)
    (grotesque/set-handler :set handler-fn)
    (grotesque/set-validator :when validator-fn)
    (grotesque/generate "#position#")
    :generated)
```

## About
Heavily inspired by the work of  
[Kate Compton](https://github.com/galaxykate),  
[Elan Ruskin](https://www.gdcvault.com/play/1015317/AI-driven-Dynamic-Dialog-through) and  
[Emily Short](https://emshort.blog/).

The [name](https://www.merriam-webster.com/dictionary/grotesque) itself is 
a riff on [Tracery](https://www.merriam-webster.com/dictionary/tracery).

Distributed under the MIT Expat License.
