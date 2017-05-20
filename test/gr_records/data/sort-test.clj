(ns gr-records.data.sort-test
  (:require [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [gr-records.data.person :as person]
            [gr-records.data.sort :as sort])
  (:import [java.time Instant Duration]))

(def gender-gen (gen/elements [:male :female]))

(def date-gen (gen/fmap #(.plus Instant/EPOCH (Duration/ofDays %)) gen/nat))

(def person-gen
  (gen/fmap (partial apply person/->Person)
            (gen/tuple
             ;; last name
             gen/string
             ;; first name
             gen/string
             ;; gender
             gender-gen
             ;; favorite color
             gen/string
             ;; birth date
             date-gen)))

(def person-seq-gen (gen/vector person-gen))

(defn elements-preserved?
  "Are all elements in coll preserved under function f?"
  [coll f]
  (= (set coll) (-> coll f set)))

(defn gender-last-name-sorted?
  "Are the elements of coll sorted first by gender, falling back to
  last name?"
  [coll]
  (every?
   (fn [[{left-gender :gender left-name :last-name}
         {right-gender :gender right-name :last-name}]]
     (and
      ;; [f f], [m m], and [f m] are all valid orderings, so
      ;; disallow [m f].
      (not= [left-gender right-gender] ["male" "female"])
      ;; when the genders are the same, the records must be sorted by
      ;; ascending name.
      (if (= left-gender right-gender)
        (not (pos? (compare left-name right-name)))
        true)))
   (partition 2 1 coll)))

(defspec by-gender-last-name
  (prop/for-all
   [person-seq person-seq-gen]
   (and (elements-preserved? person-seq sort/by-gender-last-name)
        (-> person-seq sort/by-gender-last-name gender-last-name-sorted?))))

(defn ascending-dates?
  [dates]
  (every? (fn [[l r]] (not (.isBefore l r)))
          (partition 2 1 dates)))

(defspec by-birth-date
  (prop/for-all
   [person-seq person-seq-gen]
   (and (elements-preserved? person-seq sort/by-birth-date)
        (->> person-seq
             sort/by-birth-date
             (map :birth-date)
             ascending-dates?))))

(defn descending-names? [names]
  (every? (fn [[l r]] (not (neg? (compare l r)))) (partition 2 1 names)))

(defspec by-last-name
  (prop/for-all
   [person-seq person-seq-gen]
   (and (elements-preserved person-seq sort/by-last-name)
        (->> person-seq sort/by-last-name (map :last-name) descending-names?))))
