(ns kafka-viewer.db
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-event-db
  :initialize-db
  (fn [_ _]
    {:current-route nil
     :topics        []
     :consume-topic nil
     :consumer-id   nil
     :polling?      false
     :topic-events  []
     :topic-filter  {:keyword ""
                     :system? false}
     :topic-message-filter {:keyword ""}
     :max-topic     1000
     }))


