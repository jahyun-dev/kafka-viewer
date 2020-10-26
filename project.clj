(defproject kafka-viewer "0.1.0-SNAPSHOT"
  :description "Kafka Viewer"
  :url "https://github.com/jahyun-dev/kafka-viewer"
  :license {:name "The MIT License"
            :url  "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.fasterxml.jackson.core/jackson-databind "2.10.5"]
                 [ring "1.8.1"]
                 [integrant "0.8.0"]
                 [metosin/reitit "0.5.5"]
                 [metosin/ring-http-response "0.9.1"]
                 [metosin/jsonista "0.2.7"]
                 [environ "1.2.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/java.data "1.0.86"]

                 ; logging
                 [ch.qos.logback/logback-classic "1.2.3"]

                 ;kafka
                 [org.apache.kafka/kafka_2.13 "2.5.0"]
                 [org.apache.kafka/kafka-clients "2.5.0"]

                 ; cljs
                 [org.clojure/clojurescript "1.10.773"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library
                               org.clojure/google-closure-library-third-party]]
                 [thheller/shadow-cljs "2.11.5"]
                 [reagent "1.0.0-alpha2"]
                 [re-frame "1.1.1"]
                 [cljs-ajax "0.8.1"]
                 [day8.re-frame/http-fx "0.2.1"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 ]
  :plugins [[lein-shadow "0.3.1"]
            [lein-shell "0.5.0"]]
  :min-lein-version "2.9.0"

  :source-paths ["src/clj" "src/cljs"]
  :resource-paths ["resources" "public"]

  :clean-targets ^{:protect false} ["public" "target"]

  :shadow-cljs {:lein   true
                :nrepl  {:port 3333}
                :builds {:app {:target     :browser
                               :output-dir "public/js"
                               :asset-path "/js"
                               :modules    {:main {:init-fn  kafka-viewer.core/init
                                                   :preloads [devtools.preload
                                                              re-frisk.preload]}}}}}

  :aliases {"watch"   ["with-profile" "dev" "do"
                       ["shadow" "watch" "app"]]

            "release" ["with-profile" "prod" "do"
                       ["shadow" "release" "app"]]
            }
  :profiles
  {:uberjar {:main         kafka-viewer.server
             :aot          [kafka-viewer.server]
             :omit-source  true
             :uberjar-name "kafka-viewer.jar"
             :prep-tasks    ["compile" ["release"]]
             :env          {:production true}
             }
   :dev     {:source-paths   ["dev/src"]
             :resource-paths ["dev/resources" "target"]
             :clean-targets  ^{:protect false} ["target"]
             :dependencies   [[integrant/repl "0.2.0"]
                              [re-frisk "1.3.4"]
                              [binaryage/devtools "1.0.2"]
                              ]
             :env            {:dev true}}
   :prod    {}
   })
