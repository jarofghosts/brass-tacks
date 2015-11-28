(ns brass-tacks.views
  (:require [reagent.session :as session]))

(defn home-page []
  [:div [:h2 "Welcome to brass-tacks"]
   [:div [:a {:href "/edit"} "edit something"]]])

(defn edit-page
  []
  [:div.ui.container
   [:div.sixteen.wide.column
    [:div.ui.labeled.input [:div.ui.label "Title:"]
     [:input {:type "text"}]]]
   [:div.sixteen.wide.column
    [:div.ui.labeled.input [:div.ui.label "Content"]
     [:input {:field :text-area}]]]])


(defn current-page []
  [:div.ui.container
   [:div.ui.grid
    [:div.sixteen.wide.column
     [:h1.ui.header "Brass Tacks"]]
    [:div.sixteen.wide.column
     [(session/get :current-page)]]]])
