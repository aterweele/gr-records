(ns gr-records.component.in-memory-store
  (:require [com.stuartsierra.component :as component]
            [gr-records.boundary.record-store :as rs]
            [gr-records.data.sort :as sort]))

(defrecord AtomStore [records]
  component/Lifecycle
  (start [this] (assoc this :records (atom nil)))
  (stop [this] (assoc this :records nil))
  rs/RecordStore
  (add-record [_ m] (swap! records conj m))
  (by-gender [_] (sort/by-gender-last-name @records))
  (by-birth-date [_] (sort/by-birth-date @records))
  (by-last-name [_] (sort/by-last-name @records)))
