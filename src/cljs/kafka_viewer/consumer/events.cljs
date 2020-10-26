(ns kafka-viewer.consumer.events
  (:require
    [reagent.core :as r]
    [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub]]
    [ajax.core :refer [GET POST]]
    [day8.re-frame.http-fx]
    [re-frame.core :as rf]))

(reg-event-fx
  :start-consume-topic
  (fn [{:keys [db]} [_ topic]]
    (dispatch [:init-topic-events])
    (GET (str "/api/topics/subscribe?topic=" topic)
         {:handler (fn [v]
                     (dispatch [:set-consumer-id v])
                     (dispatch [:set-polling? true]))})
    db))

(def api-executing? (r/atom false))
(defn poll-events
  []
  (let [polling? @(rf/subscribe [:polling?])
        consumer-id @(rf/subscribe [:consumer-id])]
    (if (and polling? consumer-id (not @api-executing?))
      (do
        (reset! api-executing? true)
        (GET (str "/api/topics/poll?consumer-id=" consumer-id)
             {:handler (fn [events]
                         (reset! api-executing? false)
                         (if (not-empty events)
                           (dispatch [:append-topic-events events])
                           ))})
        ))))

(defonce polling-timer (js/setInterval poll-events 2000))

(reg-event-db
  :consume-topic
  (fn [db [_ topic]]
    (assoc db :consume-topic topic)))

(reg-sub
  :consume-topic
  (fn [db _]
    (:consume-topic db)))

(reg-event-db
  :set-polling?
  (fn [db [_ polling?]]
    (assoc db :polling? polling?)))

(reg-sub
  :polling?
  (fn [db _]
    (:polling? db)))

(reg-event-db
  :init-topic-events
  (fn [db _]
    (assoc db :topic-events [])))

(reg-event-db
  :append-topic-events
  (fn [db [_ events]]
    (let [max-topic (:max-topic db)]
      (assoc db :topic-events (take max-topic (concat events (:topic-events db)))))))

(reg-sub
  :topic-events
  (fn [db _]
    (:topic-events db)))

(reg-event-db
  :set-consumer-id
  (fn [db [_ consumer-id]]
    (assoc db :consumer-id consumer-id)))

(reg-sub
  :consumer-id
  (fn [db _]
    (:consumer-id db)))
