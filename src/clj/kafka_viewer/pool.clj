(ns kafka-viewer.pool
  (:require [kafka-viewer.utils :refer [periodically]]
            [clj-time.core :as t]))

(def pool (ref {}))
(def release-time 60)

(defn add-object [id object]
  (dosync
    (alter pool assoc id {:object object :time (t/now) :create-time (t/now)})
    object))

(defn take-object [id]
  (dosync
    (let [obj (get @pool id)
          object (:object obj)]
      (alter pool assoc id (assoc obj :object object :time (t/now)))
      object)))

(defn release-object
  [min-second release-fn]
  (doseq [[id {:keys [object time]}] @pool]
    (if (t/before? (t/plus time (t/seconds min-second)) (t/now))
      (dosync
        (release-fn object)
        (alter pool dissoc id)))))


@pool