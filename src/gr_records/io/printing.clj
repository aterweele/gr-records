(ns gr-records.io.printing
  (:import [java.time.format DateTimeFormatter FormatStyle]
           [java.util Locale]))

(defn reformat-dates
  [ms]
  (let [formatter (-> "M/d/yyyy"
                      DateTimeFormatter/ofPattern
                      (.withZone java.time.ZoneOffset/UTC))]
    (map #(update % :birth-date (fn [d] (.format formatter d))) ms)))
