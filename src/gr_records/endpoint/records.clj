(ns gr-records.endpoint.records
  (:require [compojure.core :refer :all]
            [gr-records.boundary.record-store :as store]
            [gr-records.io.printing :as output]
            [gr-records.io.reading :as reading]
            [ring.middleware.json :as middle-json]
            [ring.util.http-response :refer :all]))

(defn endpoint [{:keys [store]}]
  (->
   (context
    "/records" _
    (POST
     "/"
     {:keys [body] {content-type "content-type"} :headers}
     ;; TODO: reject requests with multiple rows?
     (doseq [record (reading/parse-rows content-type body)]
       (store/add-record store record))
     (ok))
    (GET "/gender" _ (-> store store/by-gender output/reformat-dates ok))
    (GET "/birthdate" _ (-> store store/by-birth-date output/reformat-dates ok))
    (GET "/name" _ (-> store store/by-last-name output/reformat-dates ok)))
   middle-json/wrap-json-response))
