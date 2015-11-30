(ns brass-tacks.views
  (:require [reagent.session :as session]
            [reagent.core :refer [atom]]
            [taoensso.sente :as sente :refer [cb-success?]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk"
                                  {:type :auto})]
  (def chsk chsk)
  (def ch-chsk ch-recv)
  (def chsk-send! send-fn)
  (def chsk-state state))

(defn- get-value
  [ev]
  (-> ev
      .-target
      .-value))

(defn- save-document
  [doc]
  (chsk-send! [:doc/save doc] 5000
              (fn [doc-id]
                (accountant/navigate! (str "/edit/" doc-id)))))

(defn- load-document
  [doc-id doc-atom]
  (when (= false (session/get :loaded))
    (chsk-send! [:doc/get doc-id] 5000
                (fn [doc]
                  (if (cb-success? doc) (do (session/put! :loaded true) (reset! doc-atom doc)))
                  (js/setTimeout #(load-document doc-id doc-atom) 20)))))

(defn edit-page
  "edit a document"
  []
  (let [document-id (session/get :document-id)
        document (atom {:document/title "" :document/body ""})]
    (do
      (when document-id (load-document document-id document))
      (fn []
        [:div.ui.container
         [:div.ui.grid
          [:div.sixteen.wide.column
           [:h2.ui.header (:document/title @document)]]
          [:div.sixteen.wide.column
           [:div.ui.labeled.input [:div.ui.label "Title:"]
            [:input {:type "text"
                     :on-input #(swap! document assoc :document/title (get-value %))
                     :value (:document/title @document)}]]]
          [:div.sixteen.wide.column
           [:textarea {:on-input #(swap! document assoc :document/body (get-value %))
                       :value (:document/body @document)}]]
          [:div.sixteen.wide.column
           [:button.ui.primary.button {:on-click #(save-document @document)}
            "Save"]]]]))))

(defn home-page []
  [:div [:h2.ui.header "Welcome to brass-tacks"]
   [:div [:a {:href "/edit"} "edit something"]]])

(defn current-page []
  [:div.ui.container
   [:div.ui.grid
    [:div.sixteen.wide.column
     [:h1.ui.header "Brass Tacks"]]
    [:div.sixteen.wide.column
     [(session/get :current-page)]]]])

(secretary/defroute "/" []
                    (session/put! :current-page #'home-page))

(secretary/defroute "/edit/:id" [id]
                    (session/put! :document-id id)
                    (session/put! :loaded false)
                    (session/put! :current-page #'edit-page))

(secretary/defroute "/edit" []
                    (session/put! :document-id nil)
                    (session/put! :loaded true)
                    (session/put! :current-page #'edit-page))

