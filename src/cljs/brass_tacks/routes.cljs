(ns brass-tacks.routes
  (:require [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [brass-tacks.views :as views]))

(defn init!
  []
  (secretary/defroute "/" []
                      (session/put! :current-page views/home-page))

  (secretary/defroute "/edit" []
                      (session/put! :current-page views/edit-page)))

