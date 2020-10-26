(ns kafka-viewer.topics.events
  (:require
    [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub]]
    [clojure.string :refer [includes? blank? starts-with?]]
    [ajax.core :refer [GET POST]]
    [day8.re-frame.http-fx]))

(reg-event-fx
  :get-topics
  (fn [{:keys [db]} _]
    (GET "/api/topics" {:handler #(dispatch [:topics %1])})
    (assoc db :loading-topics? true)))

(reg-event-db
  :topics
  (fn [db [_ topics]]
    (assoc db :topics topics)))

(reg-event-db
  :set-topic-search-keyword
  (fn [db [_ keyword]]
    (assoc db :topic-search-keyword keyword)))

(reg-event-db
  :set-topic-filter
  (fn [db [_ topic-filter]]
    (assoc db :topic-filter topic-filter)))

(reg-sub
  :topic-filter
  (fn [db _]
    (:topic-filter db)))

(defn system-topic?
  [{:keys [name internal]}]
  (or (starts-with? name "_") internal))

(defn filter-topics
  [{:keys [keyword system?]} topics]
  (let [data (->>
               topics
               (filter #(includes? (:name %) keyword))
               (filter (if system? system-topic? (comp not system-topic?)))
               )]
    data))

(reg-sub
  :topics
  (fn [db _]
    (let [topics (sort #(compare (:name %1) (:name %2)) (:topics db))
          topic-filter (:topic-filter db)
          filtered-topics (filter-topics topic-filter topics)]
      filtered-topics)))
