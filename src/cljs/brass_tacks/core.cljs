(ns brass-tacks.core
  (:require [brass-tacks.views :as views]
            [brass-tacks.routes :as routes]
            [reagent.core :as reagent]
            [accountant.core :as accountant]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [views/current-page] (.getElementById js/document "app")))

(defn init! []
  (routes/init!)
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
