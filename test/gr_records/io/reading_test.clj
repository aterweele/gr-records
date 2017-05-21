(ns gr-records.io.reading-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [gr-records.io.reading :as reading]))

(deftest file-parsing
  (testing "Parse a CSV"
    (let [people (-> "gr_records/knvb.csv"
                     io/resource
                     io/file
                     reading/parse-rows)]
      (is (= 3 (count people)))
      (is (some #(-> % :last-name (= "de Guzmán")) people))))
  (testing "Parse a PSV (pipe-separated values file)"
    (let [people (-> "gr_records/utf8.psv"
                     io/resource
                     io/file
                     reading/parse-rows)]
      (is (= 3 (count people)))
      (is (= #{"安倍" "Е́льцин" "Sigurðardóttir"}
             (->> people (map :last-name) set)))))
  (testing "Parse a SSV (space-separated values file)"
    (let [people (-> "gr_records/authors.ssv"
                     io/resource
                     io/file
                     reading/parse-rows)]
      (is (= 3 (count people)))
      (is (= #{"green" "blue" "magic"}
             (->> people (map :favorite-color) set))))))
