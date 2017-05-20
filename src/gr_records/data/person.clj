(ns gr-records.data.person
  (:import [java.time Instant]))

(defrecord Person [last-name first-name gender favorite-color birth-date])

(defn seq->person
  "Create a person from the supplied array."
  [[last-name first-name gender favorite-color birth-date]]
  (->Person last-name first-name gender favorite-color
            (Instant/parse (str birth-date "T00:00:00.00Z"))))
