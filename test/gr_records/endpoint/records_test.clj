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
      (doseq [{:keys [delimiter person]}
              ;; TODO: more of these
              [{:delimiter ", "
                :person    {:first-name     "Rich"
                            :last-name      "Hickey"
                            :favorite-color "blue"
                            :birth-date     "1975-01-02"}}
               {:delimiter " | "
                :person    {:first-name     "Alex"
                            :last-name      "Miller"
                            :favorite-color "green"
                            :birth-date     "1977-04-29"}}
               {:delimiter " "
                :person    {:first-name     "Stuart"
                            :last-name      "Sierra"
                            :favorite-color "khaki"
                            :birth-date     "1980-11-14"}}]]
        (let [response (-> (http-mock/request :post "/records")
                           (http-mock/content-type "text/csv")
                           (http-mock/body (delimit person delimiter))
                           app)]
          (is (http-pred/ok? response))))
      ;; TODO: do each kind of get request, parse the result, assert
      ;; it is sorted.
      (doseq [{:keys [endpoint] sorted? :sort}
              [{:endpoint "gender"
                :sort     sort-test/gender-last-name-sorted?}
               {:endpoint "birthdate"
                :sort     sort-test/ascending-dates?}
               {:endpoint "name"
                :sort     sort-test/descending-names?}]]
        (let [response (-> (http-mock/request :get
                                              (format "/records/%s" endpoint))
                           app)
              result   (-> response :body (json/read-str :key-fn keyword))]
          (is (http-pred/ok? response))
          (is (sorted? result)))))))
