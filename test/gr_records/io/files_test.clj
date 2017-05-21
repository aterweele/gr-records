(ns gr-records.io.files-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [gr-records.io.files :as files]))

(deftest file-parsing
  (testing "Parse a CSV"
    (let [people (files/parse-record-file
                  (-> "gr_records/knvb.csv" io/resource io/file))]
      (is (= 3 (count people)))
      (is (some #(-> % :last-name (= "de Guzmán")) people))))
  (testing "Parse a PSV (pipe-separated values file)"
    (let [people (files/parse-record-file
                  (-> "gr_records/utf8.psv" io/resource io/file))]
      (is (= 3 (count people)))
      (is (= #{"安倍" "Е́льцин" "Sigurðardóttir"}
             (->> people (map :last-name) set)))))
  (testing "Parse a SSV (space-separated values file)"
    (let [people (files/parse-record-file
                  (-> "gr_records/authors.ssv" io/resource io/file))]
      (is (= 3 (count people)))
      (is (= #{"green" "blue" "magic"}
             (->> people (map :favorite-color) set))))))
