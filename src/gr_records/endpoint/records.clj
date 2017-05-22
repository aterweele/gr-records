(ns gr-records.endpoint.records
  (:require [compojure.core :refer :all]
            [gr-records.boundary.record-store :as store]
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
     (map (partial store/add-record store)
          (reading/parse-rows content-type body))
     (ok))
    (GET "/gender" _ (-> store store/by-gender ok))
    (GET "/birthdate" _ (-> store store/by-birth-date ok))
    (GET "/name" _ (-> store store/by-last-name ok)))
   middle-json/wrap-json-response))
