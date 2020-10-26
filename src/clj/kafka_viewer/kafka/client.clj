(ns kafka-viewer.kafka.client
  (:require [environ.core :refer [env]]
            [integrant.core :as ig]
            [jsonista.core :as j]
            [kafka-viewer.utils :refer [periodically]]
            [kafka-viewer.pool :as p])
  (:import (java.util HashMap)
           (org.apache.kafka.clients.admin AdminClient)
           (org.apache.kafka.clients.consumer KafkaConsumer)
           (org.apache.kafka.common.serialization StringDeserializer)
           (java.time Duration)))

(defn mk-admin
  [bootstrap-servers]
  (let [kafka-configs (doto
                        (HashMap.)
                        (.put "bootstrap.servers" bootstrap-servers))]
    (AdminClient/create kafka-configs)))

(defn mk-consumer
  [bootstrap-servers viewer-id]
  (new KafkaConsumer {"bootstrap.servers"  bootstrap-servers
                      "group.id"           viewer-id
                      "key.deserializer"   StringDeserializer
                      "value.deserializer" StringDeserializer}))

(defn topic-description-to-map
  [x]
  {:name            (.name x)
   :internal        (.isInternal x)
   :partition-count (.size (.partitions x))})

(defn get-topic-names
  [client]
  (->> (.listTopics client)
       (.names)
       (.get)
       seq))

(defn get-topics
  [admin-client]
  (map topic-description-to-map
       (-> (.describeTopics admin-client (get-topic-names admin-client))
           (.all)
           (.get)
           (.values)
           seq
           )))

(defn ConsumerRecord->data
  [x]
  {:topic     (.topic x)
   :key       (.key x)
   :partition (.partition x)
   :offset    (.offset x)
   :timestamp (.timestamp x)
   :value     (j/read-value (.value x))
   })

(defn position-all
  [consumer]
  (->> (.assignment consumer)
       seq
       (map #(.position consumer % (Duration/ofMillis 5000)))))

(defn start-subscribe-to-end
  [consumer topic]
  (do
    (.subscribe consumer [topic])
    (.poll consumer (Duration/ofMillis 1000))
    (.seekToEnd consumer (.assignment consumer))
    (.commitSync consumer)
    (position-all consumer)))

(defn poll
  [consumer timeout]
  (->> (.poll consumer (Duration/ofMillis timeout))
       seq
       (map ConsumerRecord->data)))

(defn subscriptions
  [consumer]
  (seq (.subscription consumer)))

(defn close-consumer
  [consumer]
  (if consumer (.close consumer)))

