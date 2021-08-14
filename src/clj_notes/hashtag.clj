(ns clj-notes.hashtag)

;; # 的规则和用途===start
;; # is Dispatch character that tells the Clojure reader how to interpret the next character
;; using a read table
;; set
#{1 2 3}

;; discard two following input
;; 忽略:b 2
{:a 1, #_#_:b 2, :c 3}
;; user=> {:a 1  #_ :b :c 2 :d 3}
;; {:a 1, :c 2, :d 3}

;; regular expression
(re-matches #"^test$" "test")
;; => "test"

;; anonymous function
#(println %)
;; var quote
(read-string "#'foo")
;; symbolic values
(/ 1.0 0.0)
;; #Inf

;; tagged literals
(type #inst "2014-05-19T19:12:37.925-00:00")                ;; java.util.Date
;; meta
(defn fn-name
  []
  "hello")
(meta #'fn-name)

;; reader conditionals
;; #?(:clj     (Clojure true)
;;    :cljs    (ClojureScript false)
;;    :default (fallthrough false))

;; #?@ splicing reader conditional
;; (defn build-list []
;;   (list #?@(:clj  [5 6 7 8]
;;             :cljs [1 2 3 4])))                              ;; return [5 6 7 8] when run on clojure

;; #= allows the reader to evaluate an arbitrary form during read time
(read-string "#=(+ 3 4)")                                   ;; 7
;; # 的规则和用途===end

