(ns clj-notes.concurrency)
;; future
(let [future-val (future (inc 1))]
  (println (deref future-val)))
;; deref == @
(let [future-val (future (inc 1))]
  (println @future-val))

(def my-future (future (Thread/sleep 5000)))
(repeatedly 6
            (fn []
              (println (realized? my-future))
              (Thread/sleep 1000)))

;; (doc future)

;; promise
(def my-promise (promise))
;; you define a promise
(def listen-and-callback (fn []
                           (println "Start listening...")
                           (future (println "Callback fired: " @my-promise))))

(defn do-time-consuming-job []
  (Thread/sleep 5000)
  (deliver my-promise "delivered value"))

(listen-and-callback)
(do-time-consuming-job)

;; atom is like mutable var in other languages but atom is thread safe

;; ref dosync ref-set alter
(def my-ref (ref 0))
(dosync
  (alter my-ref
         (fn [current_ref]
           (inc current_ref))))

(print @my-ref)

(def user (ref {}))
(dosync
  (alter user merge {:name "Kim"})
  (throw (Exception. "something wrong happens!"))
  (alter user merge {:age 32}))

(def user-record (atom {}))

(do (swap! user-record merge {:name "Kim"})
    (throw (Exception. "something wrong happens!"))
    (swap! user-record merge {:age 32}))

