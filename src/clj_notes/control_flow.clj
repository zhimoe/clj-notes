(ns clj-notes.control-flow)

;; In Clojure, everything except false and nil are true.
;; if do 是保留字，core form
;; do是group多个立即执行的语句
;; map filter是函数
;; case if-let if-not when when-not when-let doseq doall let cond condp while loop for cond-> cond->>全部是macro
;; (if test consequent alternative)
;; (cond & clauses)
(def x 10)
(cond
  (> x 0) "greater!"
  (= x 0) "zero!"
  :default "lesser!")

;; when is a macro combine if with do
(when (> 5 2)
  (println "five")
  (println "is")
  (println "greater")
  "done")

(when-not (< 5 2)
  (println "two")
  (println "is")
  (println "smaller")
  "done")
(and)                                                       ;;=> true
(and :a :b :c)                                              ;;=> :c
(< 2 4 6 8)
(< '(1 2 3))
;; = 比较
;; == only compare integer (including ratio), big decimal, and floating point.
(== 2M 1.9999999999999)
;;=> false
(== 2M 1.9999999999999999999999999999999999999999999999)
;;=> true

;; while
;; (while (request-on-queue?)
;;   (handle-request (pop-request-queue)))

;; loop and recur
;; (loop bindings & body) (recur bindings)
(defn fact-loop [n]
  (loop [current n fact 1]
    (if (= current 1)
      fact
      (recur (dec current) (* fact current)))))

;; doseq and dotimes
(defn run-report [user]
  (println "Running report for" user))
(defn dispatch-reporting-jobs [all-users]
  (doseq [user all-users]
    (run-report user)))
(dotimes [x 5]
  (println "X is" x))



(map + [0 1 2 3] [0 1 2])                                   ;; => (0 2 4)
;; filter and remove
(remove zero? expenses)
;; reduce
(defn factorial [n]
  (let [numbers (range 1 (+ n 1))]
    (reduce * numbers)))

;; (for seq-exprs body-expr)
;; list comprehension is a way to create a list from existing lists
(for [x '(1 2 3)]
  (+ 10 x))                                                 ;;(11 12 13)
(for [x [0 1 2 3 4 5]
      :let [y (* x 3)]
      :when (even? y)]
  y)

;; cond-> and cond->>


;; if
(if true
  (println "executed when true")
  (println "executed when false"))

;; use do to execute multi expressions
(if true
  (do
    (println "one")
    (println "two")))

;; if-let:
(defn positive-number [numbers]
  (if-let [pos-nums (not-empty (filter pos? numbers))]
    pos-nums
    "no positive numbers"))

;; when when-let case cond condp
(defn cond-test
  [n]
  (cond
    (= n 1) "n is 1"
    (and (> n 3) (< n 10)) "n is over 3 and under 10"
    :else "n is other"))

(cond-test 1000)

;; str
(let [first "Hirokuni"
      last "Kim"]
  (str "My name is " first " " last))

;; format
(format "My name is %s %s" "Hirokuni" "Kim")

;; power function
(defn power
  [x n]
  (reduce * (repeat n x)))


;; for compression
(for [x '(1 2 3)]
  (+ 10 x))

;; (doc for)
;; 双重for 循环
(for [x (range 10)
      y (range 20)
      :while (< y x)]
  [x y])

;; <==> {x | x >0}
(for [x '(-1 1 2)
      :when (> x 0)]
  x)

(for [x [0 1 2 3 4 5]
      :let [y (* x 3)]
      :when (even? y)]
  y)
