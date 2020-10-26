(ns kafka-viewer.utils
  (:require [clojure.java.io :refer [reader]])
  (:import (java.util UUID Properties)
           (java.io Reader)))

(defn periodically
  [f interval]
  (doto (Thread.
          #(try
             (while (not (.isInterrupted (Thread/currentThread)))
               (Thread/sleep interval)
               (f))
             (catch InterruptedException e
               (println "execution error " e))))
    (.start)))

(defn uuid [] (str (UUID/randomUUID)))

(defn load-props
  [file-name]
  (with-open [^Reader reader (clojure.java.io/reader file-name)]
    (let [props (Properties.)]
      (.load props reader)
      (into {} (for [[k v] props] [(keyword k) (read-string v)])))))
