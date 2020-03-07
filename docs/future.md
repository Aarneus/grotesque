[Back to overview](overview.md)

## Notes on future features
The foremost feature missing from Grotesque at the moment is pattern matching. 

### Pattern matching
This will allow the user to specify *wildcard* variables.  
These would be parts of conditions and effects that would be matched with an existing variable when the rule was selected.  

A partial example of pattern matching:
```
{:rules {:event ["#X# punches #Y#!" 
                 :when.X.relationship.Y.hostile 
                 :set.Y.relationship.X.hostile]}
 :data  {:jack {:relationship {:john :hostile}}
         :john {:relationship {:jack :neutral}}}}
```
In this example the `X` and `Y` are the wildcards.  
When checking for valid rules, this rule will check if there is any combination of valid,
 different variables that will fit these requirements and inject them into the given positions.

In this example, `X` would match with `:jack` and `Y` would match with `:john`.  
Therefore, before the conditions were checked the rule body would be morphed into the following form:
```
["#jack# punches #john#!"
 :when.jack.relationship.john.hostile
 :set.john.relationship.jack.hostile]
```
The rule itself would of course remain unchanged and ready to match different variables next time.

### Possible other future features
Other future features after 1.0 and the ones above could include:
- Optimizations
- Sample project (NaNoGenMo 2020?)
- Visualization
- Cookbook of possible effects, conditions and selectors
- Support for interop from Java/JavaScript