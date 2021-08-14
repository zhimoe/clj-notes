(ns clj-notes.macros)

;; 看这个之前先对do等控制语句熟悉
;; macro
;; ' is quote, 避免定义macro时被evaluated. '()和(quote ())相同, 减少括号的语法糖
(defmacro unless [test then]
  "Evaluates then when test evaluates to be falsely"
  (list 'if (list 'not test)
        then))
;; quote还用于在import和require
(require 'clojure.string)

;; macroexpand 可以展开宏
(macroexpand '(unless false (println "hi")))
;; (if (not false) (println "hi"))

;; `表示语法quote, 区别是当表达式包含symbols, 语法quote在展开时会包含包含fully qualified namespace.
'(dec (inc 1))                                              ;;(dec (inc 1))
`(dec (inc 1))                                              ;;(clojure.core/dec (clojure.core/inc 1))
;; `和'第二个区别是, 语法quote可以在宏内unquote
`(+ 1 ~(inc 1))                                             ;;=> (clojure.core/+ 1 2)
'(+ 1 ~(inc 1))                                             ;;=> (+ 1 (clojure.core/unquote (inc 1)))


;; unquote-splice 要求unquote的form返回值必须是list, ~@作用就是把list展开, 类似scala foldMap
`(+ ~@(list 1 2 3))                                         ;;(clojure.core/+ 1 2 3)

;; thread-first macro: 将上一个form的结果作为下一个form的第一个实参
;; 这里的thread是管道的意思, 不是Java高并发的线程
(-> []
    (conj 1)
    (conj 2)
    (conj 3))
;; thread-last macro: 将上一个form的结果作为下一个form的最后一个实参
(->> ["Japan" "China" "Korea"]
     (map clojure.string/upper-case)
     (map #(str "Hello " %)))                               ;;("Hello JAPAN!" "Hello CHINA!" "Hello KOREA!")

;; delay and force

;; future and deref
;; 使用future 包裹的代码将在另一个线程执行
(do
  (future
    (Thread/sleep 3000)
    (println "after sleep"))                                ;; 在另一个线程执行, 所以立即执行下一句
  (println "hello"))
;;hello
;;nil
;;after sleep
;; future可以返回值, 通过 deref获取future的具体值
;; deref和@作用相同
(let [future-val (future (inc 1))]
  (println future-val))                                     ;;#<core$future_call$reify__6320@142cbba: 2>

(let [future-val (future (inc 1))]
  (println (deref future-val)))                             ;;2

;; sleep-and-wait 是LazySeq, 里面包含两个future, future耗时1s和2s
(let [sleep-and-wait
      (map (fn [time]
             (future
               (Thread/sleep time)
               (println (str "slept " time " sec"))))
           [1000 2000])]
  (println (type sleep-and-wait))
  (doall (map deref sleep-and-wait))
  (println "done"))
;;slept 1000 sec
;;slept 2000 sec
;;done


(def my-future (future (Thread/sleep 5000)))
;; realized? 检查future是否done
(repeatedly 6
            (fn []
              (println (realized? my-future))
              (Thread/sleep 1000)))

;; promise
;; a future is a promise from other thread,
;; a promise is your defined object, you can delievered to other thread
(def my-promise (promise))
(def listen-and-callback #((println "Start listening...")
                           (future (println "Callback fired: " @my-promise))))
(defn do-time-consuming-job []
  (Thread/sleep 5000)
  (deliver my-promise "delivered value"))
(listen-and-callback)
(do-time-consuming-job)


;; atom reset! swap!
;; ref dosync ref-set  alter



;; ;; ;; 宏
;; clojure 的一半威力来自于macro,编写程序使用的是语言自带能力,编写macro则是扩展编程语言自身,例如语法defn
;; 什么时候使用macro? 规则1:你不需要macro;; 规则2:当你脑子里反复出现要是clojure要有X特性就好了,那说明你需要用macro实现这个X特性.

;; 第一个macro: unless函数只有在test==false时,才执行then表达式(例如then是print语句),
;; 如果unless是一个普通函数,那么then表达式在test执行的同时也会执行
;; 使用macro可以让then在指定的地方执行
(defmacro unless [test then]
  "Evaluates then expr when test evaluates to be false"
  (list 'if test nil then))

;; 一个macro有2步:先macro展开,然后编译.
;; 仔细思考其实macro和web编程中的模板技术非常相似,例如freemarker.
;; freemarker中包含html所有的标签,同时增加了占位符概念,通过渲染时(macro展开)将占位符替换为实参.
;; clojure提供了2种方式构建模板
;; 一种是通过list/concat等函数构建, 此时需要使用`quoting告诉list哪些是clojure原语(if,nil等)
;; 第二种是完全freemarker的模板语法,稍后介绍,先看一下macro展开工具
(macroexpand '(unless (blank? "s") (println "test arg is false")))
(macroexpand-1 '(unless false (println "test arg is false")))
;; 可以使用macroexpand-1和macroexpand查看macro展开形式,注意!!!macro展开形式是无法发现编译错误的.

;; macro是一个野兽,到处都是你想象不到的陷阱,假设如下一个macro
(defmacro bad-unless [test then]
  (list 'if 'test nil then))

(macroexpand '(bad-unless (blank? "s") (println "should print this when false")))
;; #>(if test nil (println "should print this when false"))
;; 碰巧 test是clojure的原语函数, (if test nil 1 2 3)始终 返回nil,
;; 如果你将参数test换成其名字,那么编译期间会报错,如下
;; user=> (defmacro bad-unless [expr then]
;;   #_=>   (list 'if 'expr nil then))
;; #'user/bad-unless

;; user=> (macroexpand '(bad-unless (blank? "s") (println "should print this when false")))
;; (if expr nil (println "should print this when false"))

;; user=> (bad-unless (blank? "s") (println "should print this when false"))
;; Syntax error compiling at (REPL:1:1).
;; Unable to resolve symbol: expr in this context

;; 所以不要随意使用函数名作为参数名!!!

;;  注意list函数和'()构建list的区别: (list & items)中items会被evaluated,而'(& items) 不会.
(let [x 1 y 2]
  (list x y))
;; ;;  => (1 2)
;; and when using quote ' they are not:
(let [x 1 y 2]
  '(x y))
;; ;;  => (x y)
;; 所以上面的list不能使用'替换
(defmacro unless [test then]
  '(if test nil then))
;; user=> (macroexpand '(unless nil "ops"))
;; ((quote if) test nil then) ;; test和then没有被展开为nil "ops", 'if中的quote也被保留

;; 使用list很快就会使得你无法看清macro最后生成的代码长啥样,仔细想想macro其实就是模板技术,例如freemarker
;; 定义好html标签和变量占位符,然后用实际变量替换占位符,clojure提供了相应的模板技术: `和~/~@
;; ` 表示一个模板的开始, ~/~@表示在模板内的占位符, ~@表示变量是一个list,转为不定长参数

;; ' quoting vs ` syntax-quoting
;; 1. ` returns the fully qualified namespace, and this is important
;; 2. ` allow unquote/splicing unquote in it

`(+ ~(list 1 2 3))
;; (clojure.core/+ (1 2 3))

`(+ ~@(list 1 2 3))
;; (clojure.core/+ 1 2 3)
;; ~@ The splicing unquote works just like ~ unquote,
;; except it expands a sequence and
;;  splice the sequence contents into the enclosing syntax-quoted data structure
;; 是不是和java/scala里面的flatMap函数相似?

;; remove namespace in `,use ~'expr,即在模板中使用~'普通引述
;; user=> `[:a ~(+ 1 1) ~'c]
;; [:a 2 c]
;; user=> `[:a ~(+ 1 1) ~`c]
;; [:a 2 user/c]

;; 到此为止,macro已经不可控了,不信你试试:
;; user=> `[:a ~(+ 1 1) ~'c]
;; user=> `[:a ~(+ 1 1) ~`c]
;; user=> `{:a 1 :b '~(+ 1 2)}
;; user=> `[:a ~(+ 1 1) '~'c]
;; user=> `{:a 1 :b '~@(list 1 2)}
;; user=> `(1 `(2 3) 4)
;; user=> `(list 1 `(2 ~(- 9 6)) 4)


;; 看一下clojure自带的一些优雅的macro
(defmacro and
  "Evaluates exprs one at a time, from left to right. If a form
  returns logical false (nil or false), and returns that value and
  doesn't evaluate any of the other expressions, otherwise it returns
  the value of the last expr. (and) returns true."
  {:added "1.0"}
  ([] true)
  ([x] x)
  ([x & next]
   `(let [and# ~x]
      (if and# (and ~@next) and#))))

(defmacro ..
  "form => fieldName-symbol or (instanceMethodName-symbol args*)

  Expands into a member access (.) of the first member on the first
  argument, followed by the next member on the result, etc. For
  instance:

  (.. System (getProperties) (get \"os.name\"))

  expands to:

  (. (. System (getProperties)) (get \"os.name\"))

  but is easier to write, read, and understand."
  {:added "1.0"}
  ([x form] `(. ~x ~form))
  ([x form & more] `(.. (. ~x ~form) ~@more)))


;; thread first macro, here the thread means pipe
(-> []
    (conj 1)
    (conj 2)
    (conj 3))
;; [1 2 3]

(first (.split (.replace (.toUpperCase "a b c d") "A" "X") " "))
;; "X"

;; ;; Perhaps easier to read:
;; -> 后面是初始参数,第2行开始每一行是一个函数调用,
;; 且上一行的返回值会作为这一行第一个参数(这就是thread first)的first含义
;; 这里的thread是管道的意思,而不是并发编程的线程
;; 如果省略(),那么野生符号(bare symbol)和keyword都会当作一个函数调用,
;; 例如,这里的.toUpperCase是bare symbol,等效于(.toUpperCase args)
(-> "a b c d"
    .toUpperCase
    (.replace "A" "X")
    (.split " ")
    first)
;; the previous call same as follow, comma is equals whitespace
(-> "a b c d"
    (.toUpperCase)
    (.replace "A" "X")
    (.split " ")
    first)

;; suppose a function
(defn calculate []
  (reduce + (map #(* % %) (filter odd? (range 10)))))

;; same as
;; 上一行的结果作为最后一个参数插入,这叫thread last
(defn calculate* []
  (->> (range 10)
       (filter odd?)
       (map #(* % %))
       (reduce +)))

;; 如果想要指定每次插入的位置那么需要用 as->
;; v是每一行的返回值的名称,这样你可以在下一行任意参数位置指定
(as-> [:foo :bar] v
      (map name v)
      (first v)
      (.substring v 1))

