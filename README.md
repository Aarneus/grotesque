# grotesque

A Clojure(script) library for text generation.
Uses a context-free grammar similar to [Tracery](https://tracery.io/) but with added state conditions and effects on rules.

Essentially a cleaner, simpler version of the system I used for [my NaNoGenMo 2016 entry](https://github.com/Aarneus/blackhearts).

*Still a work-in-progress.*

## Principles
- Full support for both Clojure and ClojureScript
- Compatibility with Tracery (at least to some extent)
- Purely functional and data-driven

## Features to be done for 1.0
- [x] Rule parsing
- [x] Text generation
- [x] Unit testing
- [ ] Effects and conditions
- [ ] Configurable error logging function
- [ ] Configurable activation function
- [ ] Configurable rule picker function
- [ ] User documentation
- [ ] Schema validation

## Possible future features
- Optimizations
- Pattern matching
- Sample project (NaNoGenMo 2019?)
- Support for Tracery style modifiers
- Parameters for non-terminal tokens
- ClojureScript unit testing config
- Grammar generation utilities

## Usage

This is still a WIP.

More documentation to come.

## License

Copyright © 2019 Aarne Uotila

Distributed under the MIT Expat License.
