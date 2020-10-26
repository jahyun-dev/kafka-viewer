(ns kafka-viewer.router
  (:require [reitit.ring :as ring]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.dev :as dev]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.coercion.spec :as coercion-spec]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.spec :as spec]
            [reitit.dev.pretty :as pretty]
            [spec-tools.spell :as spell]
            [muuntaja.core :as m]
            [kafka-viewer.kafka.routes :as kafka]
            [kafka-viewer.index :refer [reagent-index-handler]]
            ))

(def router-config
  {:validate                    spec/validate               ; enable spec validation for route data
   ;:reitit.middleware/transform dev/print-request-diffs     ; pretty diffs
   :reitit.spec/wrap            spell/closed                ; strict top-level validation
   :exception                   pretty/exception            ; pretty exceptions
   :data                        {:coercion   coercion-spec/coercion ; spec
                                 :muuntaja   m/instance     ; default content negotiation
                                 :middleware [parameters/parameters-middleware ; query-params & form-params
                                              muuntaja/format-negotiate-middleware ; content-negotiation
                                              muuntaja/format-response-middleware ; encoding response body
                                              exception/exception-middleware ; handle exceptions
                                              muuntaja/format-request-middleware ; decoding request body
                                              coercion/coerce-response-middleware ; coercing request parameters
                                              coercion/coerce-request-middleware]}}) ; coercing request parameters

(defn routes
  [env]
  (ring/ring-handler
    (ring/router
      [
       ["/" {:get {:handler reagent-index-handler}}]
       ["/api" (kafka/routes env)]
       ]
      router-config)
    (ring/routes
      (ring/redirect-trailing-slash-handler)
      (ring/create-resource-handler {:path "/css" :root "css"})
      (ring/create-resource-handler {:path "/js" :root "js"})
      (ring/create-default-handler
        {:not-found reagent-index-handler}))))

