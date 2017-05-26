(ns gr-records.endpoint.records-test
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [duct.util.system]
            [gr-records.data.sort-test :as sort-test]
            [ring.mock.request :as http-mock]
            [ring.util.http-predicates :as http-pred]))

(def system (->> ["system.edn" "test.edn"]
                 (map (partial format "gr_records/%s"))
                 (keep io/resource)
                 duct.util.system/load-system
                 atom))

(defn with-system
  [test-fn]
  (swap! system component/start)
  (test-fn)
  (swap! system component/stop))

(defn delimit
  [{:keys [last-name first-name gender favorite-color birth-date]} s]
  (format "%s%s%s%s%s%s%s%s%s"
          last-name s first-name s gender s favorite-color s birth-date s))

(use-fixtures :each with-system)

(deftest endpoint
  (let [app (-> @system :records :routes)]
    (testing "Multiple post scenario"
      (doseq [{:keys [delimiter header person]}
              [{:delimiter ", "
                :header    "text/csv"
                :person    {:first-name     "Rich"
                            :last-name      "Hickey"
                            :gender         "male"
                            :favorite-color "blue"
                            :birth-date     "1975-01-02"}}
               {:delimiter " | "
                :header    "text/psv"
                :person    {:first-name     "Alex"
                            :last-name      "Miller"
                            :gender         "male"
                            :favorite-color "green"
                            :birth-date     "1977-04-29"}}
               {:delimiter " "
                :header    "text/ssv"
                :person    {:first-name     "Stuart"
                            :last-name      "Sierra"
                            :gender         "male"
                            :favorite-color "khaki"
                            :birth-date     "1980-11-14"}}]]
        (let [request  (-> (http-mock/request :post "/records")
                           (http-mock/content-type header)
                           (http-mock/body (delimit person delimiter)))
              response (app request)]
          (is (http-pred/ok? response))))
      (doseq [{:keys [endpoint] sorted? :sort}
              [{:endpoint "gender"
                :sort     sort-test/gender-last-name-sorted?}
               {:endpoint "birthdate"
                :sort     (comp sort-test/ascending-dates? :birth-date)}
               {:endpoint "name"
                :sort     (comp sort-test/descending-names? :last-name)}]]
        (let [response (-> (http-mock/request :get
                                              (format "/records/%s" endpoint))
                           app)
              result   (-> response :body (json/read-str :key-fn keyword))]
          (is (http-pred/ok? response))
          (is (sorted? result)))))))
