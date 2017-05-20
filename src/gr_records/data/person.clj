(ns gr-records.data.person)

(defrecord Person [last-name first-name gender favorite-color birth-date])
;; TODO: make ->Person private?

(defn person
  "Create a person from the supplied array."
  [[last-name first-name gender favorite-color birth-date]]
  ;; TODO: implement. Parse birth-date into an Instant.
  )
