(ns kafka-viewer.views
  (:require
    [re-frame.core :as re-frame]
    [kafka-viewer.nav.view :refer [nav]]
    )
  )

(defn main
  []
  (let [current-route @(re-frame/subscribe [:current-route])
        current-view (-> current-route :data :view)]
    [:div {:class "flex flex-col w-0 flex-1 overflow-hidden"}
     [:main {:class "flex-1 relative z-0 overflow-y-auto focus:outline-none", :tabIndex "0"}
      [:div {:class "pt-2 pb-6 md:py-6"}
       [:div {:class "max-w-7xl mx-auto px-4 sm:px-6 md:px-8"}
        [:div {:class "py-4"}
         (when current-route
           [current-view])
         ]]
       ]
      ]
     ]
    ))

(defn root
  []
  [:div {:class "h-screen flex overflow-hidden bg-gray-100"}
   [nav]
   [main]
   ])

