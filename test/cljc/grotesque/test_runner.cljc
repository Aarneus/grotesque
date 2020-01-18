(ns grotesque.test-runner
  (:require [grotesque.core-test]
            [grotesque.model-test]
            [grotesque.schema-test]
            [grotesque.util-test]
            #?(:cljs [doo.runner :refer-macros [doo-all-tests]]
               :clj  [clojure.test :refer [run-all-tests]])))

#?(:cljs (doo-all-tests)
   :clj  (run-all-tests))

