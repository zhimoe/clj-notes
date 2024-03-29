(defproject clj-notes "0.1.0-SNAPSHOT"
  :description "a project for learning clojure"
  :url "http://zhi.moe/project/clojure"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :mirrors {"central"  {:name "central"
                        :url  "https://maven.aliyun.com/repository/public"}
            #"clojars" {:name         "Internal nexus"
                        :url          "https://mirrors.tuna.tsinghua.edu.cn/clojars"
                        :repo-manager true}}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [selmer "1.12.5"]
                 [ring "1.7.1"]
                 [com.github.seancorfield/next.jdbc "1.2.689"]
                 [cc.qbits/spandex "0.7.8"]
                 ;;[clojurewerkz/elastisch "3.0.1"]
                 [org.clojure/data.json "2.4.0"]
                 [com.taoensso/carmine "3.1.0"]
                 ]
  :main ^:skip-aot clj-notes.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  )




