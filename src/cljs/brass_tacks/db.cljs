(ns brass-tacks.db
  (require [datascript.core :as d]
           [cljs-uuid.core :as uuid]))

(def conn (d/create-conn))

(defn add-document!
  [doc]
  (d/transact!
    [(assoc doc :id (uuid/make-random))]))
