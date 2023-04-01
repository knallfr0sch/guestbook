(ns guestbook.routes.home
  (:require [guestbook.db.core :as db]
            [guestbook.layout :as layout]
            [guestbook.middleware :as middleware]
            [ring.util.http-response :as response]
            [ring.util.response]))
  
(defn home-page [request]
  (layout/render 
   request "home.html" {:messages (db/get-messages)}))

(defn about-page [request]
  (layout/render request "about.html"))

(defn save-message! [{:keys [params]}]
  (db/save-message! params)
  (response/found "/"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/about" {:get about-page}]
   ["/message" {:post save-message!}]])

