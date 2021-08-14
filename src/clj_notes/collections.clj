(ns clj-notes.collections)

;; ;; ;; 序列(sequence) 是clojure中重要的概念，序列包含三个重要特性方法：
;; (first coll),(next coll),(cons item seq),
;; 一般还包括(rest coll),(more coll)两个方法(这些方法定义在clojure.lang.ISeq接口中)
;; 可以生成序列的结构称为 seqable
(def coll [])
(let [s (seq coll)]
  (if s
    (comment "work on (first s) and recur on (rest s)")
    (comment "all done - terminate")))
;; Sequence functions (map, filter, etc)implicitly call seq on the incoming (seqable) collection and
;; return a sequence (possibly empty, not nil)


;; 几乎一切数据结构在clojure中都是序列，这些数据结构包括：
;; All iterable types (types that implement java.util.Iterable)
;; Java collections (java.util.Set, java.util.List, etc)
;; Java arrays
;; Java maps
;; All types that implement java.lang.CharSequence interface, including Java strings
;; nil
;; clojure.lang.ISeq - the sequence abstraction interface,更常用的是clojure.lang.ASeq,clojure.lang.LazySeq
;; clojure.lang.Seqable - seqable marker,只有一个方法:ISeq seq();;
;; clojure.lang.Sequential - 遍历顺序和初始化顺序一致,Lists, vectors, and effectively all seqs are sequential.

;; clojure中4大数据结构,list是直接实现ISeq接口,而set,vector,map实现的是Seqable接口
(seq nil)
;; ;; => nil
(seq ())
;; ;; => nil
(sequence ())
;; ;; => ()
(sequence nil)                                              ;; (sequence nil) yields ()
;; ;; => ()


;; ;; 创建seq的函数
;; range,repeat,iterate,cycle,interleave(交错取值),interpose(给序列插入一个间隔值)

;; 过滤序列
(filter even? coll)
(take-while even? coll)
(drop-while even? coll)
(split-with even? coll)
;; assoc-in associate使加入
;; split-at,take-,drop-
;; every?,some,not-every?,not-any?,

;; 序列转换
;; map,reduce,sort,sort-by,
;; (file-seq)
;; (for)

;; vector fn: conj nth count .indexOf
;; user=> (.indexOf [1 2 3] 4)
;; -1

;; user=> (count [1 2])
;; 2

;; set fn: conj nth count disj sort contains? subset? superset?
#{1 2 3}

;; map fn: assoc merge keys vals
(let [os {:Apple "Mac" :Microsoft "Windows"}]
  (get os :Apple))

(assoc {:Apple "Mac" :Microsoft "Windows"} :Commodore "Amiga")


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

;; !!! There is no & rest func for maps.