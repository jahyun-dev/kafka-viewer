(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.repl.state :as state]
            [integrant.core :as ig]
            [clojure.pprint]
            [kafka-viewer.server :as sv]
            [kafka-viewer.utils :as utils]
            ))

(ig-repl/set-prep!
  (fn []
    (let [config (utils/load-props "./.local/config")
          bootstrap-servers (:BOOTSTRAP_SERVERS config)
          port (:PORT config)]
      (-> (sv/app-config {:bootstrap-servers bootstrap-servers
                          :port              port})))))

;; dev/resources/dev.edn
(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(defn app [] (-> state/system :kafka-viewer/app))
(defn admin-client [] (-> state/system :kafka/client))
(defn bootstrap-servers [] (-> state/config :kafka/client :bootstrap-servers))

(comment
  (go)
  (halt)
  (reset)
  (reset-all)
  (set! *print-namespace-maps* true)
  )


