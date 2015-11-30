(ns brass-tacks.handler
  (:require [brass-tacks.db :as db]
            [clojure.core.async :as async :refer [<! <!! >! >!! go go-loop]]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [sente-web-server-adapter]]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :refer [wrap-reload]]
            [environ.core :refer [env]]))

(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! sente-web-server-adapter {})]
  (def ring-ajax-post ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk ch-recv)
  (def chsk-send! send-fn)
  (def connected-uids connected-uids))

(defmulti event-handler* :id)

(defmethod event-handler* :default
  [ev-msg]
  (println (:event ev-msg)))

(defmethod event-handler* :doc/get
  [{:keys [?reply-fn ?data]}]
  (?reply-fn (db/get-document ?data)))

(defmethod event-handler* :doc/save
  [{:keys [?reply-fn ?data]}]
  (?reply-fn (db/add-document! ?data)))

(sente/start-chsk-router! ch-chsk event-handler*)
(def mount-target
  [:div#app])

(def loading-page
  (html
    [:html
     [:head
      [:meta {:charset "utf-8"}]
      [:meta {:name "viewport"
              :content "width=device-width, initial-scale=1"}]
      (include-css (if (env :dev) "/css/semantic.css" "/css/semantic.min.css"))]
     [:body
      mount-target
      (include-js "/js/app.js")]]))


(defroutes routes
           (GET "/" [] loading-page)
           (GET "/edit" [] loading-page)
           (GET "/edit/:id" [] loading-page)
           (GET "/chsk" req (ring-ajax-get-or-ws-handshake req))
           (POST "/chsk" req (ring-ajax-post req))

           (resources "/")
           (not-found "Not Found"))

(def app
  (let [handler (-> (wrap-defaults routes site-defaults)
                    wrap-keyword-params
                    wrap-params)]
    (if (env :dev) (-> handler wrap-exceptions wrap-reload) handler)))
