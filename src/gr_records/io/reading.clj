(ns gr-records.io.reading
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [gr-records.data.person :as person]))

(def extension
  {"csv" ::comma-separated
   "psv" ::pipe-separated
   "ssv" ::space-separated})

(defn file-record-type [file]
  (-> file
      .getName
      (string/split #"\.")
      last
      extension))

(defn header-record-type [header]
  (-> header
      (string/split #"/")
      last
      extension))

(def separator
  {::comma-separated \,
   ::pipe-separated  \|
   ::space-separated \space})

(def trim-operator
  (let [trim-cells (partial map (partial map string/trim))]
    {::comma-separated trim-cells
     ::pipe-separated  trim-cells
     ::space-separated identity}))

(defn- parse-from-source
  "Read from any Read-CSV-From."
  [delimit-type source]
  (let [trim-fn (trim-operator delimit-type)]
    (as-> source %
      (csv/read-csv % :separator (separator delimit-type))
      ((trim-operator delimit-type) %)
      (map person/seq->person %))))

(defn parse-rows
  ([file] (parse-from-source (file-record-type file) (io/reader file)))
  ([header body]
   (parse-from-source (header-record-type header) (io/reader body))))
