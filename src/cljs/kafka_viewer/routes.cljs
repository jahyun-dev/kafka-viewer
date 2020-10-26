(ns kafka-viewer.routes
  (:require
    [re-frame.core :as rf]
    [reagent.dom :refer [render]]
    [reitit.coercion.spec :as rt-spec]
    [reitit.frontend :as rt-fe]
    [reitit.frontend.easy :as rt-easy]

    [kafka-viewer.topics.view :refer [topics]]

    [kafka-viewer.consumer.view :refer [consumer]]
    ))

(def routes
  ["/"
   [""
    {:name       :topics
     :view       #'topics
     :main-title "Topics"
     :controllers
                 [{:start (fn [& params] (do
                                           (js/console.log "Entering topics")
                                           (rf/dispatch [:get-topics])))
                   :stop  (fn [& params] (do
                                           (js/console.log "Leaving topics")
                                           ))}]}]
   ["consume-topic/:topic"
    {:name       :consume-topic
     :view       #'consumer
     :main-title "Consume Topic"
     :controllers
                 [{:parameters {:path [:topic]}
                   :start      (fn [params] (let [topic (-> params :path :topic)]
                                              (do
                                                (js/console.log (str "Entering " topic " topic consumer"))
                                                (rf/dispatch [:consume-topic topic])
                                                (rf/dispatch [:start-consume-topic topic])
                                                )))
                   :stop       (fn [params] (let [topic (-> params :path :topic)]
                                              (rf/dispatch [:set-polling? false])
                                              (js/console.log (str "Leaving " topic " topic consumer"))))}]}]
   ]
  )

(defn on-navigate [new-match]
  (when new-match
    (rf/dispatch [:navigated new-match])))

(def router
  (rt-fe/router
    routes
    {:data {:coercion rt-spec/coercion}}))

(defn init-routes! []
  (rt-easy/start!
    router
    on-navigate
    {:use-fragment true}))
