(ns kafka-viewer.topics.view
  (:require [re-frame.core :as rf]
            [reitit.frontend.easy :as rfe]))

(defn search-bar
  []
  (let [{:keys [keyword system?] :as topic-filter} @(rf/subscribe [:topic-filter])]
    [:div {:class "flex justify-between"}
     [:div {:class "mt-1 relative"}
      [:div {:class "absolute inset-y-0 left-0 pl-3 flex items-center"}
       [:svg {:class "mr-3 h-4 w-4 text-gray-400" :viewBox "0 0 20 20" :fill "currentColor"}
        [:path {:fill-rule "evenodd" :d "M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" :clip-rule "evenodd"}]]]
      [:input {:id        "search" :class "form-input block pl-9 sm:text-sm sm:leading-5" :placeholder "Search"
               :value     keyword
               :on-change #(rf/dispatch [:set-topic-filter (assoc topic-filter :keyword (-> % .-target .-value))])}]
      ]
     [:div {:class "flex"}
      [:div {:class "flex items-center"}
       [:p {:class "ml-2 mr-2"} "System Topics"]
       [:input {:type "checkbox" :checked system? :on-change #(rf/dispatch [:set-topic-filter (assoc topic-filter :system? (-> % .-target .-checked))])}]]
      ]
     ]
    ))

(defn topic
  [{:keys [name]}]
  (if name
    [:li {:class "border-t border-gray-200" :key name}
     [:a {:href (rfe/href :consume-topic {:topic name}) :class "block hover:bg-gray-50 focus:outline-none focus:bg-gray-50 transition duration-150 ease-in-out"}
      [:div {:class "px-4 py-4 flex items-center sm:px-6"}
       [:div {:class "min-w-0 flex-1 sm:flex sm:items-center sm:justify-between"}
        [:div
         [:div {:class "text-sm leading-5 font-medium text-indigo-600 truncate"}
          name]]]
       [:div {:class "ml-5 flex-shrink-0"}
        [:svg {:class "h-5 w-5 text-gray-400" :xmlns "http://www.w3.org/2000/svg" :viewBox "0 0 20 20" :fill "currentColor"}
         [:path {:fill-rule "evenodd" :d "M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" :clip-rule "evenodd"}]]]]]])
  )

(defn topic-list
  [topics]
  [:div {:class "bg-white shadow overflow-hidden sm:rounded-md"}
   [:ul
    (map topic topics)
    ]])

(defn topics []
  (let [broker-topics @(rf/subscribe [:topics])
        topic-count (count broker-topics)]
    [:div
     [:h1 {:class "text-2xl font-semibold text-gray-900 pb-2"} "Kafka Topics"]
     [:div {:class "pb-2"} (search-bar)]
     (topic-list broker-topics)]))
