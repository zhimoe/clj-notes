(ns clj-notes.files
  (:import (java.io IOException)))

;; Clojure will return the result of the last form evaluated in a function
;; pr/prn is to reader, so 不会执行控制符
;; print/println is to human
(prn "hello \n world")
;; => "hello \n world"
(println "hello \n world")
;; => hello
;; =>  world

;; the default print destination is STDOUT
(do
  (println "hello world")
  (println "this is STDOUT"))

;; with the default STDOUT/STDERR, you can rebind the *out*/*err* to other destination
;; you can bind the *out* to any stream-like object, such as files, sockets, or pipes
(def file-writer (clojure.java.io/writer "out.txt"))
(binding [*out* file-writer]
  (println "hello write to out.txt file"))
;; close the file
(.close file-writer)

(require '[clojure.java.io :as io]
         '[clojure.edn :as edn])

;; the people.edn file content:
(->> "people.edn"
     io/resource
     slurp
     edn/read-string
     (map :language))
;; -> ("Lisp" "Python" "Clojure")

(clojure.java.io/copy
  (clojure.java.io/file "./file-to-copy.txt")
  (clojure.java.io/file "./my-new-copy.txt"))
;; -> java.io.FileNotFoundException
;; copy function accept :encoding "UTF-8" opts

;; safe copy avoid over-write
(defn safe-copy [source-path destination-path & opts]
  (let [source (clojure.java.io/file source-path)
        destination (clojure.java.io/file destination-path)
        options (merge {:overwrite false} (apply hash-map opts))]
    (if (and (.exists source)
             (or (:overwrite options)
                 (= false (.exists destination))))
      (try
        (= nil (clojure.java.io/copy source destination))
        (catch Exception e (str "exception: " (.getMessage e))))
      false)))

;; with-open
(with-open [reader (clojure.java.io/reader "file-to-copy.txt")
            writer (clojure.java.io/writer "my-new-copy.txt")]
  (clojure.java.io/copy reader writer))

;; todo: 理解make-writer函数

;; silently=true
(clojure.java.io/delete-file "./safe-copy1.txt" true)

;; catch exception
(try
  (clojure.java.io/delete-file "./file-that-does-not-exist.txt")
  (catch IOException e (str "exception: " (.getMessage e))))

;; file-seq return all File objects in directory
(def files-in-target (file-seq (clojure.java.io/file "./target")))

(defn only-files
  "Filter a sequence of files/directories by the .isFile property of java.io.File"
  [files]
  (filter #(.isFile %) files))

(defn names
  "Return the .getName property of a sequence of files"
  [file-s]
  (map #(.getName %) file-s))

