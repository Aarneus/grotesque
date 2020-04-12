# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/) and uses [semantic versioning](https://semver.org/).

## [2.2.0] - 2020-04-12
### Added
- Wildcard variables are now implemented, see [the docs](docs/variables.md) for more information

## [2.1.0] - 2020-03-07
### Added
- Text output modifiers are now implemented, see [the docs](docs/modifiers.md) for more information
### Changed
- The generation method is now recursion based instead of looping
- The invocation tree can now be generated separately
- The grotesque/invocation namespace is now grotesque/generation
- Selection functions are now in grotesque/selection
### Fixed 
- A unit test result depended on the running environment too much

## [2.0.0] - 2020-02-16
### Changed
- Conditions are now validated in order of declaration
- Effects are now executed in order of declaration
- Condition validator and effect handler functions now get the grammar, 
rule id and the whole tag as parameters (instead of omitting the first keyword)
- Error messages now include the tag as well as rule that caused the error

## [1.0.2] - 2020-01-18
### Fixed
- ClojureScript string parsing when requiring the library

## [1.0.1] - 2020-01-18
### Fixed
- The internal util macro try-catch-cljc (didn't work in CLJS when requiring the library)

## [1.0.0] - 2020-01-18
### Added
- Core functionality
- Tests
- Documentation

[Unreleased]: https://github.com/Aarneus/grotesque/compare/2.2.0...HEAD
[2.2.0]: https://github.com/Aarneus/grotesque/compare/2.1.0...2.2.0
[2.1.0]: https://github.com/Aarneus/grotesque/compare/2.0.0...2.1.0
[2.0.0]: https://github.com/Aarneus/grotesque/compare/1.0.2...2.0.0
[1.0.2]: https://github.com/Aarneus/grotesque/compare/1.0.1...1.0.2
[1.0.1]: https://github.com/Aarneus/grotesque/compare/1.0.0...1.0.1
[1.0.0]: https://github.com/Aarneus/grotesque/compare/0.0.0...1.0.0
