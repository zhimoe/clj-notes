(ns clj-notes.java
  (:import (java.util Set Date Calendar Random)
           (java.text SimpleDateFormat)))

;; Java
;; import
(import 'Date)
(import '(org.apache.hadoop.hbase.client HTable Scan)
        '(java.util.function BiFunction Consumer))


;; Instantiation
;; (new ClassName args*)
;; (ClassName. args*) 如果第一个symbol以dot(.)结束，说明是一个ClassName， 则表示调用constructor
(new Date "2016/2/19")
(SimpleDateFormat. "yyyy-MM-dd")

;; access java members(method,field)
;; (. ClassSymbol methodSymbol args*)
;; (. ClassSymbol (methodSymbol args*)) ;;useful when is macro
;; (. instanceExpr methodSymbol args*)
;; (. instanceExpr (methodSymbol args*)) ;;useful when is macro

;; the dot special form can be read as "in the scope of."
;; e.g. in the scope of System ,get the env of PATH
(. System getenv "PATH")
(. System (getenv "PATH"))
(def rnd (Random.))
(. rnd nextInt 10)
(. rnd (nextInt 10))

;; syntax sugar for static member
(System/getenv "PATH")
(Math/pow 2 3)
(Calendar/JANUARY)
;; syntax sugar for instance member
(.nextInt rnd 10)                                           ;;(. rnd nextInt 10)

;; dot-dot macro
;; 在java中会有连击的方法调用
;; Calendar.getInstance().getTimeZone().getDisplayName()
;; 翻译成clojure代码是
(. (. (Calendar/getInstance) getTimeZone) getDisplayName)
;; 或者使用语法糖
(.getDisplayName (.getTimeZone (Calendar/getInstance)))
;; clojure还提供了dot-dot macro  顺序上面看上去和Java方法调用非常像了
(.. (Calendar/getInstance) getTimeZone getDisplayName)
;; 如果方法接受参数, 则对应方法和参数使用()包裹
(.. (Calendar/getInstance)
    getTimeZone
    (getDisplayName true TimeZone/SHORT))

;; doto macro 实现Java的构建者模式
;; 在java中构建一个复杂对象时一般会采用builder模式，即在一个对象上面连续调用不同的方法
;; builder.setName().setAge().setWeight().build()
;; clojure提供了 doto
;; 不使用doto
(import '(java.util Calendar))
(defn the-past-midnight-1 []
  (let [calendar-obj (Calendar/getInstance)]
    (.set calendar-obj Calendar/AM_PM Calendar/AM)
    (.set calendar-obj Calendar/HOUR 0)
    (.set calendar-obj Calendar/MINUTE 0)
    (.set calendar-obj Calendar/SECOND 0)
    (.set calendar-obj Calendar/MILLISECOND 0)
    (.getTime calendar-obj)))
;; 使用doto
(defn the-past-midnight-2 []
  (let [calendar-obj (Calendar/getInstance)]
    (doto calendar-obj
      (.set Calendar/AM_PM Calendar/AM)
      (.set Calendar/HOUR 0)
      (.set Calendar/MINUTE 0)
      (.set Calendar/SECOND 0)
      (.set Calendar/MILLISECOND 0))
    (.getTime calendar-obj)))

;; memfn make java method as function
(map #(.getBytes %) ["amit" "rob" "kyle"])
;; 这里必须使用一个匿名函数，将string对象传入，否则clojure无法知道.getBytes方法
;; 使用memfn和type hint，将getBytes变成一个高阶函数
(map (memfn ^String getBytes) ["amit" "rob" "kyle"])
(memfn subSequence start end)                               ;; 这是一个普通的clojure函数

;; bean 可以将java对象转为map
(bean (Calendar/getInstance))

;; tokens is java array
(def tokens (.split "clojure.in.action" "\\."))             ;;type=> [Ljava.lang.String;
(alength tokens)
(aget tokens 2)
(aset tokens 2 "actionable")
;; clojure seq to java array
;; (to-array, to-array-2d, and into-array)
;; (make-array) to create java array
;; also amap and areduce for array

;; 异常处理


;; 高级话题
;; 实现java interface

;; 处理Java方法重载
;; clojure 是动态语言，如果一个java方法使用参数类型重载，那么clojure无法识别
;; 例如，JDK11在java.util.Collection.toArray(IntFunction<T[]> generator),
;; 没有type hints，clojure会抛出 illegalArgumentException


;; 调用Java
;; import java class
(import Date)
(println (str (new Date)))
;; Wed Jul 24 22:55:24 CST 2019

(new Date "2016/2/19")
(Date.)
(Date. "2016/2/19")
(Math/pow 2 3)                                              ;; static method
(def rnd (new Random))
(. rnd nextInt 10)

(let [date1 (new Date)
      date2 (new Date)]
  (.equals date1 date2))



;; ;; ;; Java方法调用
;; import
;; (import & import-symbols-or-lists)
(import 'java.util.Date 'java.text.SimpleDateFormat)
(import '[java.util Date Set])
(ns clj-notes.core
  (:import (java.util Set Date Calendar TimeZone)
           (java.util.concurrent ConcurrentHashMap)))

;; new a instance
(def sdf (new SimpleDateFormat "yyyy-MM-dd"))
;; or
(def sdf-dot (SimpleDateFormat. "yyyy-MM-dd"))

;; call instance method
(.parse sdf date-string)
;; or use dot-form, see more on below
(. sdf parse "2019-10-03")

;; access static method/field
;; (ClassName/staticMethod args*)
;; (ClassName/staticField)

;; nested class
(.getEnclosingClass java.util.Map$Entry)
;; -> java.util.Map

;; dot special form
;; all above java call style are expanded into calls to the dot operator (described below) at macroexpansion time.
;;  The expansions are as follows
(.instanceMember instance args*)
;; ==> (. instance instanceMember args*)
(.instanceMember ClassName args*)
;; ==>(. (identity ClassName) instanceMember args*)
(.-instanceField instance)
;; ==> (. instance -instanceField)
(ClassName/staticMethod args*)
;; ==> (. ClassName staticMethod args*)
(ClassName/staticField)
;; ==> (. ClassName staticField)

;;  general forms, dot read as 'in the scope of'
;; (. ClassName member)
;; (. instanceExpr member)
;;  if the member is start with "-", it will resolve only as field access,不会被识别为方法


;; dot-dot
(import 'java.util.Calendar)
(. (. (Calendar/getInstance) getTimeZone) getDisplayName)
;; use .. macro chain the calls
(.. (Calendar/getInstance)
    getTimeZone
    (getDisplayName true TimeZone/SHORT))

;;  doto macro
(doto calendar-obj
  (.set Calendar/AM_PM Calendar/AM)
  (.set Calendar/HOUR 0)
  (.set Calendar/MINUTE 0)
  (.set Calendar/SECOND 0)
  (.set Calendar/MILLISECOND 0))


;;  (memfn methodNm) 用于提示参数有这个methodNm
(map (memfn getBytes) ["amit" "rob" "kyle"])
;; ==
(map #(.getBytes %) ["amit" "rob" "kyle"])
;;  memfn 使用了反射,所以如果能够添加一个类型提示可以提高性能
(time (dotimes [n 100000] (mapv (memfn toLowerCase) ["A" "B" "C"])))
;; "Elapsed time: 1188.276915 msecs"
(time (dotimes [n 100000] (mapv (memfn ^String toLowerCase) ["A" "B" "C"])))
;; "Elapsed time: 74.903093 msecs"
;;  see the source of memfn

;; bean convert java bean to map
(bean (Calendar/getInstance))


;; ;; ;;  java array
(def tokens (.split "clojure.in.action" "\\."))
;; #'user/tokens
;; user=> (type tokens)
;; [Ljava.lang.String;;
;;  alength,aget,aset function for java array, caution! java array is mutable



