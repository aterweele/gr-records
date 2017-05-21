(ns gr-records.main
    (:gen-class)
    (:require [clojure.java.io :as io]
              [clojure.pprint :refer [print-table]]
              [clojure.string :as string]
              [clojure.tools.cli :as cli]
              [com.stuartsierra.component :as component]
              [duct.util.runtime :refer [add-shutdown-hook]]
              [duct.util.system :refer [load-system]]
              [environ.core :refer [env]]
              [gr-records.data.sort :as sort]
              [gr-records.io.files :as files]
              [gr-records.io.printing :as printing]))

(def cli-options
  [["-d" "--daemon" "run the web server"]
   ["-h" "--help" "print usage message and exit"]
   ["-o" "--output OUTPUT-TYPE"
    (str "output format. 1 for gender falling back to last name, 2 for birth"
         " date, 3 for inverse last name")
    :default 1
    :default-desc "1"
    :parse-fn #(Integer/parseInt %)
    :validate [#{1 2 3} "Only 1, 2, and 3 are valid output formats."]]])

(defn parse-and-sort-files
  [output-type files]
  (let [sort-fn (case output-type
                  1 sort/by-gender-last-name
                  2 sort/by-birth-date
                  3 sort/by-last-name)]
    (->> files (map io/file) (mapcat files/parse-record-file) sort-fn)))

(defn -main [& args]
  (let [{:keys [options arguments summary errors]}
        (cli/parse-opts args cli-options)]
    (cond
      (not-empty errors) (println (string/join \newline errors))
      (:help options)    (println summary)

      (:daemon options)
      (let [bindings {'http-port (Integer/parseInt (:port env "3000"))}
            system   (->> (load-system [(io/resource "gr_records/system.edn")] bindings)
                          (component/start))]
        (add-shutdown-hook ::stop-system #(component/stop system))
        (println "Started HTTP server on port" (-> system :http :port)))

      ;; FIXME: specifying a nonexistant file will produce a
      ;; stacktrace
      :else (->> arguments
                 (parse-and-sort-files (:output options))
                 printing/reformat-dates
                 print-table))))
