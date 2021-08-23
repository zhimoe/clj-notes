(ns app.redis
  (:require [taoensso.carmine :as car :refer (wcar)]))

(def server1-conn {:pool {} :spec {:uri "redis://localhost:6879/"}})
(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))