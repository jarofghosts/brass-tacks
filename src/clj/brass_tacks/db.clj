(ns brass-tacks.db
  (:use [datomic.api :only [q db] :as d])
  (:import (java.util UUID)))

(def schema
  [{:db/id #db/id[:db.part/db]
    :db/ident :document/id
    :db/unique :db.unique/identity
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "App-generated UUID for a document"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :document/title
    :db/fulltext true
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "User-provided title for a document"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :document/body
    :db/fulltext true
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "User-provided body for a document"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :document/active
    :db/valueType :db.type/boolean
    :db/cardinality :db.cardinality/one
    :db/doc "Configuration for availability of a document"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :document/author
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "The ID of the document's author"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :document/tags
    :db/fulltext true
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/many
    :db/doc "User-provided tags for document searching"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :author/id
    :db/unique :db.unique/identity
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "App-generated UUID for an author"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :author/email
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "E-mail address associated with author"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :document
    :db/doc "Partition for documents"
    :db.install/_partition :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :author
    :db/doc "Paritition for authors"
    :db.install/_partition :db.part/db}])

(def uri "datomic:mem://brass-tacks")

(defn- create-uuid
  []
  (str (UUID/randomUUID)))

(d/create-database uri)
(def conn (d/connect uri))
@(d/transact conn schema)

(defn add-document!
  [doc]
  (let [doc-id (or (:document/id doc) (create-uuid))
        db-id (or (:db/id doc) (d/tempid :document))
        complete-doc (merge doc {:document/id doc-id :db/id db-id})]
    @(d/transact conn [complete-doc])
    doc-id))

(defn get-document
  [id]
  (let [document (q '[:find (pull ?e [:db/id :document/id :document/title :document/body]) :in $ ?doc-id :where [?e :document/id ?doc-id]] (db conn) id)]
    (first (first document))))
