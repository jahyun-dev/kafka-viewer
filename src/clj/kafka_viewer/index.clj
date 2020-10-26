(ns kafka-viewer.index
  (:require
    [hiccup.page :refer [include-js include-css html5]]
    [environ.core :refer [env]]))

(def mount-target
  [:div#app
   [:h2 "Kafka Viewer"]
   [:p "please wait ..."]
   [:p "(Check the js console if nothing exciting happens.)"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name    "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css "css/tailwind.min.css")
   (include-css "css/tailwind-ui.min.css")
   ])

(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "js/main.js")]))

(defn reagent-index-handler
  [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (loading-page)})
