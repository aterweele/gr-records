(ns gr-records.io.files
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [gr-records.data.person :as person]))

(defn record-type
  "Determine how data in the given filename is delimited according to
  its extension."
  [filename]
  (case (-> filename (string/split #"\.") last)
    "csv" ::comma-separated
    "psv" ::pipe-separated
    "ssv" ::space-separated))

(defmulti read-record-file
  "Parse the data file into a list of lists."
  record-type)
(defmethod read-record-file ::comma-separated [filename]
  (-> filename io/reader csv/read-csv))
(defmethod read-record-file ::pipe-separated [filename]
  (as-> filename x
    (io/reader x)
    (csv/read-csv x {:separator \|})
    ;; TODO: this double map is a good opportunity to break out
    ;; specter

    ;; TODO: document assertion that we do not want to count the space
    ;; around entries.

    ;; TODO: transduce?

    ;; Do not count the space around entries.
    (map #(map string/trim %) x)))
(defmethod read-record-file ::space-separated [filename]
  ;; TODO: document assumption that multiple spaces do not happen
  (-> filename io/reader (csv/read-csv {:separator \space})))

(defn parse-record-file
  "Read the file given by filename into a list of maps"
  [filename]
  (-> filename read-record-file (map person/->Person)))
