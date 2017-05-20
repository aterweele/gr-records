(ns gr-records.main
    (:gen-class)
    (:require [com.stuartsierra.component :as component]
              [duct.util.runtime :refer [add-shutdown-hook]]
              [duct.util.system :refer [load-system]]
              [environ.core :refer [env]]
              [clojure.java.io :as io]))

(defn -main [& args]
  (if ((set args) "-d")
    (let [bindings {'http-port (Integer/parseInt (:port env "3000"))}
          system   (->> (load-system [(io/resource "gr_records/system.edn")] bindings)
                        (component/start))]
      (add-shutdown-hook ::stop-system #(component/stop system))
      (println "Started HTTP server on port" (-> system :http :port)))
    ;; TODO: cmd-line mode here.
    ))
