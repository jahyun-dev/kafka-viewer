(ns kafka-viewer.server
  (:gen-class)
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [kafka-viewer.router :as router]
            [environ.core :refer [env]]
            [kafka-viewer.kafka.client :refer [mk-admin mk-consumer close-consumer]]
            [kafka-viewer.pool :as p]
            [kafka-viewer.utils :as utils]))

(defn app-config
  [config]
  (let [bootstrap-servers (:bootstrap-servers config)
        port (:port config)]
    {:server/jetty     {:handler (ig/ref :kafka-viewer/app)
                        :port    port}
     :kafka-viewer/app {:kafka (ig/ref :kafka/client)}
     :kafka/client     {:bootstrap-servers bootstrap-servers}}))

(defn app
  [env]
  (router/routes env))

(defmethod ig/prep-key :server/jetty [_ config]
  (let [port (:port config)]
    (println "Prep server port " port)
    (merge config {:port (:port config)})))

(defmethod ig/prep-key :kafka/client [_ config]
  (let [bootstrap-servers (:bootstrap-servers config)]
    (println "Prep kafka client " bootstrap-servers)
    (merge config {:bootstrap-servers bootstrap-servers})))

(defmethod ig/init-key :server/jetty [_ {:keys [handler port]}]
  (println (str "Server running on port " port "\n"))
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key :kafka/client [_ config]
  (println "Configured kafka client")
  (let [bootstrap-servers (:bootstrap-servers config)
        release-job (utils/periodically #(p/release-object 60 close-consumer) 1000)]
    {:admin             (mk-admin bootstrap-servers)
     :bootstrap-servers bootstrap-servers
     :release-job       release-job}))

(defmethod ig/init-key :kafka-viewer/app [_ config]
  (println "Started app")
  (app config))

(defmethod ig/halt-key! :server/jetty [_ jetty]
  (.stop jetty))

(defmethod ig/halt-key! :kafka/client [_ {:keys [admin release-job]}]
  (do
    (.close admin)
    (p/release-object 0 close-consumer)
    (.stop release-job)))

(defn -main
  [& args]
  (let [bootstrap-servers (:bootstrap-servers env)
        port-raw (:port env)
        port (if (int? port-raw)
               port-raw
               (Integer/parseInt port-raw))]
    (-> (app-config {:bootstrap-servers bootstrap-servers
                     :port              port}) ig/prep ig/init)))
