(defproject gr-records "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 ;; Duct framework and general web application
                 ;; dependencies
                 [com.stuartsierra/component "0.3.2"]
                 [compojure "1.6.0"]
                 [duct "0.8.2"]
                 [environ "1.1.0"]
                 [ring "1.6.1"]
                 [ring/ring-defaults "0.3.0"]
                 [ring/ring-jetty-adapter "1.6.1"]
                 [ring/ring-json "0.4.0"]
                 [ring-jetty-component "0.3.1"]
                 [metosin/ring-http-response "0.9.0"]
                 ;; file parsing
                 [org.clojure/data.csv "0.1.4"]
                 ;; command-line argument parsing
                 [org.clojure/tools.cli "0.3.5"]
                 ;; serializing to/from JSON
                 [org.clojure/data.json "0.2.6"]]
  :plugins [[lein-environ "1.0.3"]
            [lein-cloverage "1.0.9"]]
  :main ^:skip-aot gr-records.main
  :target-path "target/%s/"
  :aliases {"setup"  ["run" "-m" "duct.util.repl/setup"]}
  :profiles
  {:dev  [:project/dev  :profiles/dev]
   :test [:project/test :profiles/test]
   :uberjar {:aot :all}
   :profiles/dev  {}
   :profiles/test {}
   :project/dev   {:dependencies [[duct/generate "0.8.2"]
                                  [reloaded.repl "0.2.3"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [org.clojure/tools.nrepl "0.2.13"]
                                  [eftest "0.3.1"]
                                  [com.gearswithingears/shrubbery "0.4.1"]
                                  [kerodon "0.8.0"]
                                  [org.clojure/test.check "0.9.0"]]
                   :source-paths   ["dev/src"]
                   :resource-paths ["dev/resources" "test-resources"]
                   :repl-options {:init-ns user}
                   :env {:port "3000"}}
   :project/test  {}})
