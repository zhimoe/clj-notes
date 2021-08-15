(ns clj-notes.try-catch)

;; 控制语句
;; (try expr* catch-clause* finally-clause?)
;; (catch ExpClassType e exception-handle-exper*)
(try
  (print "Attempting division... ")
  (/ 1 0)
  (catch Exception e
    (let [exdata (ex-data e)]
      (println exdata)))
  (catch ArithmeticException e "DIVIDE BY ZERO!")
  (catch Throwable e "Unknown exception encountered!")
  (finally
    (println "done.")))

(throw (Exception. "error message"))

;; ex-data
;; ex-info