(ns kafka-viewer.components.search
  (:require (reagent.core :as r)))

(defn search-input
  [keyword on-change]
  [:div {:class "mt-1 relative"}
   [:div {:class "absolute inset-y-0 left-0 pl-3 flex items-center"}
    [:svg {:class "mr-3 h-4 w-4 text-gray-400" :viewBox "0 0 20 20" :fill "currentColor"}
     [:path {:fill-rule "evenodd" :d "M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" :clip-rule "evenodd"}]]]
   [:input {:id        "search" :class "form-input block pl-9 sm:text-sm sm:leading-5" :placeholder "Search"
            :value     keyword
            :on-change on-change}]
   ]
  )
