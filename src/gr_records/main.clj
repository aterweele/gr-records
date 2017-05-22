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
              [gr-records.io.printing :as printing]
              [gr-records.io.reading :as reading]))

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

(def sort-order
  {1 sort/by-gender-last-name
   2 sort/by-birth-date
   3 sort/by-last-name})

(defn -main [& args]
  (let [{:keys [options arguments summary errors]}
        (cli/parse-opts args cli-options)]
    (cond
      (not-empty errors) (do (println (string/join \newline errors))
                             (System/exit -1))
      (:help options)    (println summary)

      ;; Start the webserver if the daemon option is specified.
      (:daemon options)
      (let [system   (-> [(io/resource "gr_records/system.edn")]
                         load-system
                         component/start)]
        (add-shutdown-hook ::stop-system #(component/stop system))
        (println "Started HTTP server on port" (-> system :http :port)))

      ;; Otherwise, handle the specified files.
      :else (let [files (map io/file arguments)]
              (if (every? #(.exists %) files)
                (->> files
                     (mapcat reading/parse-rows)
                     ((sort-order (options :output)))
                     printing/reformat-dates
                     print-table)
                (do (println (format "Specified files do not exist: %s"
                                    (string/join
                                     \space
                                     (filter (complement #(.exists %)) files))))
                    (System/exit -1)))))))
