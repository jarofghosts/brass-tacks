(ns brass-tacks.core
  (:require [brass-tacks.views :as views]
            [reagent.core :as reagent]
            [accountant.core :as accountant]))

(defn mount-root []
  (reagent/render [views/current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
