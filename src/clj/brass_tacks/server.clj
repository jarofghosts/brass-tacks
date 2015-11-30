(ns brass-tacks.server
  (:use org.httpkit.server)
  (:require [brass-tacks.handler :refer [app]]
            [environ.core :refer [env]]
            (:gen-class)))

 (defn -main [& args]
   (let [port (Integer/parseInt (or (env :port) "3000"))]
     (run-server app {:port port})))
