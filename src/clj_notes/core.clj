(ns clj-notes.core
  (:gen-class)
  (:import (clojure.java.api Clojure)
           (java.util Date Random)))
;; :gen-class generate java class file
;; 数据类型
;; clojure.lang.BigInt(like java BigInteger),N is literal for BigInt,
;; M is literal for BigDecimal
(+ 9223372036854775807 10N)                                 ;; 9223372036854775817N
(+ 98765431123456789.1 10M)                                 ;; 9.87654311234568E16

;; Param 和 Arg的区别
;; Parameter is variable in the declaration of function.
;; Argument is the actual value of this variable that gets passed to function.

;; !!! clojure中逗号等于空白符, 所以不要在参数，列表等地方使用逗号！
;; !!! everything but false and nil evaluates to true in Clojure

;; install leiningen:
;; put lein.bat in your PATH
;; open cmder,run: lein repl
;; start repl,use exit,(exit),(quit) or ctrl+d to quit repl
(println "hello clojure")

;; Symbols are used to bind names to values
;; ' will prevent a form from being evaluated
;; '() same as (quote ())

;; def define global variable
;; let local variable binding

;; defn 定义函数
;; defn- 定义ns内私有函数
(defn f
  "the second line is doc-string"
  {:added  "1.2"                                            ;; this is attr-map
   :static true}
  [param]
  (print "hello " param))

(meta (var f))
;; #' is the reader macro for var and works the exactly same
(meta #'f)

;; fn create a function
(def f (fn [] (println "this is from fn function")))
;; #() is the shortcut for (fn [] ...)
(def plus-one #(+ 1 %))
;; % will be replaced with arguments passed to the function
;; %1 is for the first argument, %2 is for the second and so on

;; namespace
;; create-ns create a namespace
;; in-ns move to a namespace
;; require loads a namespace and
;; refer refers the namespace.
;; To do these at once, you can use use

;; you can rename namespace
;; (require '[clj-notes.core :as temp-ns])

;; ns macro creates a new namespace and
;;  gives you an opportunity to load other namespaces at the creation time

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

;; ;; ;; Recursion
;; simple recursion, don't call it!
(defn fibo
  "this is recursion function"
  [n]
  (if (or (= n 0) (= n 1))
    n
    (+ (fibo (- n 1)) (fibo (- n 2)))))                     ;; this is else branch
;; do not do this!!! take a long time to finish
;; (fibo 100)

;; use recur
(defn fibo-recur [iteration]
  (let [fibo (fn [one two n]
               (if (= iteration n)
                 one
                 (recur two (+ one two) (inc n))))]
    ;; recur re-binds it's arguments to new values and call the function with the new values
    ;; fibo is an inner function
    (fibo 0N 1N 0)))

(println (fibo-recur 100))
;; it is really fast
;; notes
;; with simple recursion, each recursive call creates a stack frame which is 
;; a data to store the information of the called function on memory.
;; Doing deep recursion requires large memory for stack frames, but since it cannot, 
;; we get StackOverflowError

;; 尾递归
;; A function is tail recursive when the recursion is happening at the end of it's definition
;; In other words, a tail recursive function must return itself as it's returned value.
;; When you use recur, it makes sure you are doing tail recursion

;; (doc loop)
;; loop/recur is merely a friendly way to write recursion code.
;; All imperative loops can be converted to recursions and all recursions can be converted to loops,
;; so Clojure chose recursions.
;; Although you can write code that looks like an imperative loop with loop/recur,
;; Clojure is doing recursion under the hood.

(comment these will be ignored, no matter with/without quote)

