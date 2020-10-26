(ns kafka-viewer.events
  (:require [re-frame.core :as re-frame]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend.controllers :as rfc]))
(re-frame/reg-event-fx
  :navigate
  (fn [_ [_ & route]]
    {::navigate! route}))

(re-frame/reg-event-db
  :navigated
  (fn [db [_ new-match]]
    (let [old-match (:current-route db)
          controllers (rfc/apply-controllers (:controllers old-match) new-match)]
      (assoc db :current-route (assoc new-match :controllers controllers)))))

;;; Subscriptions ;;;
(re-frame/reg-sub
  :current-route
  (fn [db]
    (:current-route db)))

;;; Effects ;;;
;; Triggering navigation from events.
(re-frame/reg-fx
  :navigate!
  (fn [route]
    (apply rfe/push-state route)))
