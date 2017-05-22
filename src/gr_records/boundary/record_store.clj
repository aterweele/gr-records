(ns gr-records.boundary.record-store)

(defprotocol RecordStore
  (add-record [this m] "Persist record m to the given store.")
  (by-gender [this] "Retrieve all records ordered by gender.")
  (by-birth-date [this] "Retrieve all records ordered by birth date.")
  (by-last-name [this] "Retrieve all records ordered by last name."))
