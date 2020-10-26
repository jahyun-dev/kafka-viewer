(ns kafka-viewer.kafka.routes
  (:require [kafka-viewer.kafka.client :as client]
            [kafka-viewer.pool :as p]
            [kafka-viewer.utils :as utils]))

(defn get-consumer
  [req]
  (let [consumer-id (get-in req [:query-params "consumer-id"])
        consumer (p/take-object consumer-id)]
    consumer))

(defn routes
  [env]
  (let [admin (-> env :kafka :admin)
        bootstrap-server (-> env :kafka :bootstrap-servers)]
    [["/topics"
      [""
       {:get
        {:handler (fn [_]
                    {:status 200
                     :body   (client/get-topics admin)})}}]
      ["/subscribe"
       {:get
        {:handler (fn [{:keys [query-params]}]
                    (let [consumer-id (str "kafka-viewer-" (utils/uuid))
                          consumer (p/add-object consumer-id (client/mk-consumer bootstrap-server consumer-id))
                          topic (get query-params "topic")]
                      {:status 200
                       :body   (do
                                 (client/start-subscribe-to-end consumer topic)
                                 consumer-id)}))
         }}]
      ["/poll"
       {:get
        {:handler (fn [req]
                    {:status 200
                     :body   (let [consumer (get-consumer req)]
                               (client/poll consumer 1000))})
         }}]
      ["/subscriptions"
       {:get
        {:handler (fn [_]
                    {:status 200
                     :body   (let [subs (client/subscriptions nil)]
                               subs)})
         }}]
      ]
     ]))
