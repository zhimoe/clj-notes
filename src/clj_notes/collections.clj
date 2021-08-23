(ns clj-notes.collections)

;; 数据结构是编程中最重要的部分，clojure提供了丰富的集合类型，同时做到了抽象统一
;; “It is better to have 100 functions operate on one data structure than
;; 10 functions on 10 data structures.” —Alan Perlis


;; 序列(sequence) 是clojure中重要的概念，表示一个逻辑list，抽象为ISeq接口。
;; 序列clojure.lang.ISeq包含重要方法：
;; (first coll),(next coll),(cons item seq), (rest coll),(more coll), equiv,count, empty, seq方法
(def coll [])                                               ;; vector
(let [s (seq coll)]
  (if s
    (comment "work on (first s) and recur on (rest s)")
    (comment "all done - terminate")))

;; 可以生成序列的结构称为 Seqable
;; ======start
;; 几乎一切数据结构在clojure中都是序列，这些数据结构包括：
;; All iterable types (types that implement java.util.Iterable)
;; Java collections (java.util.Set, java.util.List, etc)
;; Java arrays
;; Java maps
;; All types that implement java.lang.CharSequence interface, including Java strings
;; nil
;; clojure.lang.ISeq - the sequence abstraction interface,更常用的是clojure.lang.ASeq,clojure.lang.LazySeq
;; clojure.lang.Seqable - seqable marker,只有一个方法:ISeq seq()
;; clojure.lang.Sequential - 遍历顺序和初始化顺序一致,Lists, vectors, and effectively all seqs are sequential.
;; ======end

;; Sequence functions (map, filter, etc) 会隐式call seq on the incoming (seqable) collection and
;; return a sequence (possibly empty, not nil)

;; 大部分情况下，开发者使用的都是map和seq两种数据结构，map有时也是当作seq来处理

;; clojure中4大数据结构,list是直接实现ISeq接口,而set,vector,map实现的是Seqable接口

;; ====== seq和sequence方法区别，sequence always return () when false
(seq nil)                                                   ;; => nil
(sequence nil)                                              ;; => ()
(seq ())                                                    ;; => nil
(sequence ())                                               ;; => ()


;; 创建seq的函数
;; range,repeat,iterate,cycle,interleave(交错取值),interpose(给序列插入一个间隔值)

;; 关于seq的全部方法，参考官方ref：https://clojure.org/reference/sequences


;; vector map list set四大数据结构
;; vector是arraylist，可以通过索引访问
;; literal
([1 2 3 5])
(vector 1 2 3)
(get ["abc" false 99] 0)
(conj [1 2 3] 4 5 6)

;; list是linked list，只能通过遍历或者first/rest/nth
(def cards '(10 :ace :jack 9))
;; literal list, 这里需要使用quote是因为clojure的语法就是list，不quote的话，会被认为是一个表达式，执行后发现第一个参数不是方法，抛异常
'(1 2 3)
;; same as follow:
(list 1 2 3)


;; set
(def players #{"Alice", "Bob", "Kelly"})
;; map
(def scores {"Fred"   1400
             "Bob"    1240
             "Angela" 1024})
(assoc scores "Sally" 0)                                    ;;associate
(dissoc scores "Bob")                                       ;;dissociate
(get scores "Angela")
(contains? scores "Fred")
(keys scores)

(def players #{"Alice" "Bob" "Kelly"})
(zipmap players (repeat 0))

;; with map and into
(into {} (map (fn [p layer] [players 0]) players))

;; with reduce
(reduce (fn [m player]
          (assoc m player 0))
        {}                                                  ; initial value
        players)

;; when key is keyword, the keyword can be function
(def stu {:name "bob" :age 10})
(:name stu)                                                 ;;=> "bob"

;; map destructuring
(def my-hashmap {:a "A" :b "B" :c "C" :d "D"})
(def my-nested-hashmap {:a "A" :b "B" :c "C" :d "D" :q {:x "X" :y "Y" :z "Z"}})

(let [{a :a d :d} my-hashmap]
  (println a d))
;; ;;  => A D

(let [{a :a, b :b, {x :x, y :y} :q} my-nested-hashmap]
  (println a b x y))
;; ;;  => A B X Y
(let [{a :a, b :b, not-found :not-found, :or {not-found ":)"}, :as all} my-hashmap]
  (println a b not-found all))
;; ;;  => A B :) {:a A :b B :c C :d D}

;;
(def sm (sorted-map
          "Bravo" 204
          "Alfa" 35
          "Sigma" 99
          "Charlie" 100))
;;{"Alfa" 35, "Bravo" 204, "Charlie" 100, "Sigma" 99}
