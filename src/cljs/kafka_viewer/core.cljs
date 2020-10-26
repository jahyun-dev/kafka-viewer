(ns kafka-viewer.core
  (:require
    [kafka-viewer.events]
    [kafka-viewer.routes :as routes]
    [kafka-viewer.views :as views]
    [kafka-viewer.nav.view :refer [nav]]
    [re-frame.core :as re-frame]
    [reagent.dom :as rdom]
    [day8.re-frame.http-fx]

    ; events
    [kafka-viewer.events]
    [kafka-viewer.db]
    [kafka-viewer.topics.events]
    [kafka-viewer.consumer.events]
    ))

(defn render []
  (rdom/render [views/root] (.getElementById js/document "app")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (#'routes/init-routes!)
  (render)
  )

(defn ^:dev/after-load after-reload []
  (render))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root)
  )

