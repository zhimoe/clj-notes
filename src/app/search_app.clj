(ns app.search-app
  (:require [clojure.pprint :as pp]
            [clojure.data.json :as json]
            [qbits.spandex :as s]
            [clj-http.client :as http]))


(def doc {
          "user"      "kimchy",
          "post_date" "2009-11-15T14:12:12",
          "message"   "trying out Elasticsearch"
          })

(http/put "http://localhost:9200/myapp2_development1/_doc/2"
          {:headers    {"Content-Type" "application/json"}
           :debug      true
           :debug-body true
           :body       (json/write-str doc)})


(def c (s/client {:hosts ["http://127.0.0.1:9200"]}))
(s/request c {:url    "myapp2_development2/_search"
              :method :get
              :body   {:query {:match_all {}}}})