(ns clj-notes.functions)
;; todo: trampolining( trampoline蹦床),尾递归优化 http://jakemccrary.com/blog/2010/12/06/trampolining-through-mutual-recursion/
;; todo: STM
;; todo: 多重方法defmulti/defmethod
;; todo: modifiers

;; function template
;;(defn function-name
;;  doc-string?
;;  metadata-map?
;;  [parameter-list*]
;;  conditions-map?
;;  body-expressions*)

;;(def ^:dynamic *db-host* "localhost")
(def ^:dynamic *eval-me* 10)
(defn print-the-var [label]
  (println label *eval-me*))
(print-the-var "A:")
(binding [*eval-me* 20]                                     ;; the first binding
  (print-the-var "B:")
  (binding [*eval-me* 30]                                   ;; the second binding
    (print-the-var "C:"))
  (print-the-var "D:"))
(print-the-var "E:")

;; defmulti
;; (defmulti name docstring? attr-map? dispatch-fn & options)

(defmulti
  ^{:doc      "Internal helper for copy"
    :private  true
    :arglists '([input output opts])}
  do-copy
  (fn [input output opts] [(type input) (type output)]))

;; function and destructuring example
(defn des
  "args means get :k1 value from argument (map) and binding it to k1(parameter)"
  [{k1 :k1}]
  (println "destructing in map" k1))

;; also you can destructuring from str/symbol key
(defn currency-of
  [{currency "currency"}]
  currency)
(defn currency-of
  [{currency 'currency}]
  currency)
;; 可以用:keys,:strs,:syms一次性解构所有参数,形参和实参同名
(defn currency-of
  [{:keys [currency amount]}]
  (println currency amount))

(currency-of {:currency "RMB" :amount 100000})              ;; ok
(currency-of {"currency" "RMB" "amount" 100000})            ;; currency will be nil,you will need use :strs or :syms

;; :strs
(defn currency-strs
  [{:strs [currency amount]}]
  currency)
(currency-strs {"currency" "RMB" "amount" 100000})          ;; ok

;; :syms
(defn currency-syms
  [{:syms [currency amount]}]
  currency)
(currency-syms {'currency "CNY" 'amount 100000})            ;; ok

;; 默认值参数 use :or to give a default value for parameter
(defn currency-or
  [{:keys [currency amount] :or {currency "USD"}}]
  currency)
(currency-or {:amount 100000})                              ;; => "USD"

;; 不定长参数 use & for Variadic Functions parameters
(defn log
  "first arg is msg and other arguments are stored in args,args is a seq"
  [msg & args]
  (println "msg=" msg ",and rest args=" (map #(str "*" %1 "*") args)))
(log "hi" "jim" "bella")
;; msg= hi ,and rest args= (*jim* *bella*)

;; 命名参数 named params , achieved by Variadic Functions parameters destructing
(defn job-info
  [& {:keys [name job income] :or {job "unemployed" income "$0.00"}}]
  (if name
    [name job income]
    (println "No name specified")))

;; cation! 这里实参不是map
(job-info :name "Robert" :job "Engineer")
;; => ["Robert" "Engineer" "$0.00"]

;; Without the use of a variadic argument list,
(defn job-info-map
  [{:keys [name job income] :or {job "unemployed" income "$0.00"}}]
  (if name
    [name job income]
    (println "No name specified")))
;; 如果没有&， 则这个方法实参必须是map，变成了普通的map destructing
;; you would have to call the function with a single map argument such as
(job-info-map {:name "Robert" :job "Engineer"})
;; => ["Robert" "Engineer" "$0.00"]

;;  more example on destructing: https://gist.github.com/john2x/e1dca953548bfdfb9844
(def my-vec [1 2 3])

(let [[a b c d] my-vec]
  (println a b c d))
;; 1 2 3 nil

(let [[a b & the-rest] my-vec]
  (println "a=" a "b=" b "the-rest=" the-rest))
;; a= 1 b= 2 the-rest= (3)
(let [[:as all] my-vec]
  (println all))
;; [1 2 3]
(let [[a :as all] my-vec]
  (println a all))
;; 1 [1 2 3]
(let [[a b & the-rest :as all] my-vec]
  (println a b the-rest all))
;; 1 2 (3) [1 2 3]
;; !!! note: & the-rest convert vector to list,
;; but :as preserves them (as a list, or as a vector)

(def my-vec ["first" "second"])
(let [{a 0 b 1} my-vec]
  (println a b))                                            ;; => "first second"

;; optional arguments to functions
(defn foo [a b & more-args]
  (println a b more-args))
(foo :a :b)                                                 ;; ;;  => :a :b nil
(foo :a :b :x)                                              ;; ;;  => :a :b (:x)
(foo :a :b :x :y :z)                                        ;; ;;  => :a :b (:x :y :z)


;; count file lines
(defn- num-lines
  [file]
  (with-open [rdr (clojure.java.io/reader file)]
    (count (line-seq rdr))))

;; :as bind entire map to param that followed :as
;; See https://github.com/ring-clojure/ring/wiki/File-Uploads for explanation
(defn file-handler
  "argument is a map,:as request binding the arg to request var,而
  {{{tempfile :tempfile filename :filename} \"file\"} :params  是嵌套解构,即从实参取出:params
  然后又从:params内部取出:filename和:tempfile,其中:tempfile是一个java.io.File"
  ;; [{{{tempfile :tempfile filename :filename} "file"} :params :as request}]
  [{{{tempfile :tempfile filename :filename} "file"} :params :as request}]
  (println request)
  (let [n (num-lines tempfile)]
    (println (str "File " filename " has " n " lines "))))

;; 请求示例:
;; {...
;;  :params
;;   {"file" {:filename     "words.txt"
;;            :content-type "text/plain"
;;            :tempfile     #object[java.io.File ...]
;;            :size         51}}
;;  ...}

;; 又一个嵌套解构的例子
(defn first-first
  "最外面的[]是表示参数,[[i _] _]表示实参必须一个二维以上的vector,只取第二维的第一个
  如果是1维,返回nil"
  [[[i _] _]]
  i)
;; user=> (first-first [[1 2] [3 4]])
;; 1
;; user=> (first-first [[[1 2] [3 4]] [5 6]])
;; [1 2]

;; (defn name doc-string? attr-map? [params*] prepost-map? body)
;; (defn name doc-string? attr-map? ([params*] prepost-map? body) + attr-map?)
;; function can have params type hint
(defn round
  "^double here is type hint
  everything start with ^ means metadata"
  [^double d ^long precision]
  (let [factor (Math/pow 10 precision)]
    (/ (Math/floor (* d factor)) factor)))


;; 重载函数
(defn bar
  "参数个数不同的重载,clojure还有更强大的defmulti/defmethod"
  ([a b] (bar a b 100))
  ([a b c] (* a b c)))

;; 复习上面学过的命名参数
(defn named-arg-fn [& {:keys [function sequence]}]
  (map function sequence))
;; the arg is not a map!!!
;; (named-arg-fn :sequence [1 2 3] :function #(+ % 2))




;; todo: 补充oop中的多态知识点
;; ;; clojure的多态和多重方法defmulti/defmethod
;; java中的多态是通过方法重载来实现的,重载要求方法的参数个数或类型不同来实现. -- 静态分配
;; clojure中实现参数个数重载非常简单,上面已经见过.
;; clojure中的多态通过multi-method可以实现更为灵活的代码,clojure中一般不会用到多态,一旦用到,都是核心逻辑代码.
;; 假设一个场景:一个print函数,希望这个函数可以支持nil,string,vector,map等多种类型的打印,在Java中可以通过参数类型重载实现多个print方法.

(comment
  ;; define a multi
  ;; dispatch-fn can be keyword since when arg is map
  (defmulti multi-name docstring? attr-map? dispatch-fn & options)

  ;; define a multi-method
  (defmethod multi-name dispatch-value & fn-tail)

  ;; get the multimethod dispatch map
  (methods multi-name)
  ;; or use get-method on arg to check which method is dispatched
  (get-method multi-name "mint.com")

  ;; remove multimehtod impl by remove-method and removeall-methods.
  )

;; todo: trampolining( trampoline蹦床),尾递归优化 http://jakemccrary.com/blog/2010/12/06/trampolining-through-mutual-recursion/
;; 蹦床运动解决的一个问题就是调用栈过深导致栈溢出.
(declare my-odd?)

(defn my-even? [n]
  (if (zero? n)
    true
    (my-odd? (dec (Math/abs n)))))

(defn my-odd? [n]
  (if (zero? n)
    false
    (my-even? (dec (Math/abs n)))))

;; > (my-even? 10000)
;; Execution error (StackOverflowError) at user/my-even? (REPL:4).
;; 这里就出现栈溢出,scala中同样有这个问题:
(comment
  "def foldR[A,B](as: List[A], b: B, f: (A,B) => B): B = as match {
    case Nil => b
    case h :: t => f(h,foldR(t,b,f))
  }
  ")

;; clojure 通过 trampoline 函数解决这个问题

(defn my-even? [n]
  (if (zero? n)
    true
    #(my-odd? (dec (Math/abs n)))))

(defn my-odd? [n]
  (if (zero? n)
    false
    #(my-even? (dec (Math/abs n)))))

;; > (trampoline my-even? 10000)

