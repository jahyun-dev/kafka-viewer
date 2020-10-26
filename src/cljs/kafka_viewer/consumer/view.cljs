(ns kafka-viewer.consumer.view
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            ["react-json-view" :default ReactJson]
            [cljs-time.core :refer [to-default-time-zone]]
            [cljs-time.coerce :as t-coerce]
            [cljs-time.format :as t-format]))

(def date-formatter (t-format/formatter "HH:mm:ss"))

(defn tr-row
  [{:keys [key timestamp value]}]
  (let [open-json-view (r/atom false)]
    (fn []
      [:tr {:class "bg-white"}
       ;[:td {:class "px-2 py-3 whitespace-no-wrap text-sm leading-5 font-medium text-gray-900"} key]
       [:td {:class "px-2 py-3 whitespace-no-wrap text-sm font-medium text-gray-900"} (t-format/unparse date-formatter (to-default-time-zone (t-coerce/from-long timestamp)))]
       [:td {:class "px-2 py-3 whitespace-no-wrap text-sm text-gray-500"}
        [:div {:class "cursor-pointer" :on-click (fn []
                                                   (reset! open-json-view (not @open-json-view)))}
         (if @open-json-view
           [:> ReactJson {:src                        value
                          :name                       false
                          :displayDataTypes           false
                          :collapsed                  (not @open-json-view)
                          :collapseStringsAfterLength 10}]
           (str (apply str (take 200 (str value))) "...")
           )]
        ]])))

(defn tables []
  (let [topic-events @(rf/subscribe [:topic-events])
        indexed-topic-events (map-indexed vector topic-events)]
    [:div {:class "flex flex-col"}
     [:div {:class "-my-2 overflow-x-auto sm:-mx-6 lg:-mx-8"}
      [:div {:class "py-2 align-middle inline-block min-w-full sm:px-6 lg:px-8"}
       [:div {:class "shadow overflow-hidden border-b border-gray-200 sm:rounded-lg"}
        [:table {:class "min-w-full divide-y divide-gray-200"}
         [:thead
          [:tr
           [:th {:class "px-2 py-3 bg-gray-50 text-left text-xs leading-2 font-medium text-gray-500 uppercase tracking-wider"} "timestamp"]
           [:th {:class "px-2 py-3 bg-gray-50 text-left text-xs leading-2 font-medium text-gray-500 uppercase tracking-wider"} "value"]
           ]]
         [:tbody
          (for [[_ event] indexed-topic-events]
            ^{:key (str (:topic event) (:partition event) (:offset event))}
            [tr-row event])
          ]]]]]]))

(defn consumer []
  (let [consume-topic @(rf/subscribe [:consume-topic])]
    [:div
     [:h1 {:class "text-2xl font-semibold text-gray-900 pb-4"} consume-topic]
     [tables]
     ]))
