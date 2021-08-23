(ns clj-notes.multimethod)

;;多态（polymorphism）是编程中很重要的一个概念。指为不同数据类型的实体提供统一的接口，或使用一个单一的符号来表示多个不同的类型。
;;多态的最常见主要类别有：
;;
;;- 特设多态(Ad-hoc polymorphism) ：为个体的特定类型的任意集合定义一个共同接口。Java的函数重载。Clojure的multimethod。
;;- 参数多态：指定一个或多个类型不靠名字而是靠可以标识任何类型的抽象符号。Java中的泛型。
;;- 子类型（也叫做子类型多态或包含多态）：一个名字指称很多不同的类的实例，这些类有某个共同的超类。Java的继承与面向接口编程。
;;
;;一个multimethod包含一个*dispatching function* 和一或多个实现函数。 通过defmulti定义dispatching function。
;;dispatching function用于实参来得到一个`dispatching value`, multimethod 通过这个value来匹配调用哪个具体函数。
;;可以提供一个`:default` 的dispatching value对应的实现函数，如果没有匹配上，该函数会被调用。如果完全没有匹配上，调用报错。

(defmulti describe-thing class)                             ;class is dispatching function, return dispatching value is class type

(defmethod describe-thing java.lang.Long
  [thing] (println "a Long" (str thing)))                   ;when the thing is Long, call this

(defmethod describe-thing java.lang.String
  [thing] (println "a String" (str thing)))                 ;when the thing is String, call this


;; keyword as function,
(defmulti promotion-due :position)
;; 上面的dispatching function等价于下面这个
(defmulti promotion-due
          (fn [emp]
            (:position emp)))

;; argument that contains :engineer field/or key, call this
(defmethod promotion-due :engineer
  [emp] (> (:lines-of-code emp) 100000))

(defmethod promotion-due :manager
  [emp] (> (:num-reports emp) 10))

; Works with records
(defrecord Employee [name position num-reports lines-of-code])

;; 多个参数的例子
(deftype Shape [])
(deftype Rectangle [])
(deftype Ellipse [])
(deftype Triangle [])

(defmulti intersect
          (fn [a b]
            [(class a) (class b)]))

(defmethod intersect [Rectangle Ellipse]
  [r e] (printf "Rectangle x Ellipse [names r=%s, e=%s]\n"
                (class r) (class e)))

(defmethod intersect [Rectangle Rectangle]
  [r1 r2] (printf "Rectangle x Rectangle [names r1=%s, r2=%s]\n"
                  (class r1) (class r2)))

(defmethod intersect [Rectangle Shape]
  [r s] (printf "Rectangle x Shape [names r=%s, s=%s]\n"
                (class r) (class s)))


;; ======
;; Typically, in most languages, inheritance relations are defined between classes at the point where these classes are defined.
;; In Clojure, the concept of an inheritance hierarchy is completely detached from any particular class mechanism—it lives on its own.
;; We define inheritance relationships between abstract "tags," which are customarily either (namespace-qualified) symbols or keywords.

;; :: called namespace-qualified keywords.

(derive ::dog ::mammal)
(derive ::cat ::mammal)
(derive ::husky ::dog)
(isa? [::husky ::husky] [::dog ::cat])
(isa? [::husky ::husky] [::dog ::mammal])

(derive ::rectangle ::shape)
(derive ::ellipse ::shape)
(derive ::triangle ::shape)

(defmulti intersect
          (fn [a b]
            [(:kind a) (:kind b)]))                         ;keyword as function, return [a,b] as dispatching value

(defmethod intersect [::rectangle ::ellipse]
  [r e] (printf "Rectangle x Ellipse"))

(defmethod intersect [::rectangle ::rectangle]
  [r1 r2] (printf "Rectangle x Rectangle"))

(defmethod intersect [::rectangle ::shape]
  [r s] (printf "Rectangle x Shape"))

(intersect {:kind ::rectangle} {:kind ::shape})             ;Rectangle x Shape
(intersect {:kind ::rectangle} {:kind ::triangle})
;;Rectangle x Shape
;;since ::triangle derived from ::shape