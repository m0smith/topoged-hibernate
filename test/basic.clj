(ns basic
  (:import [java.util Date])
  (:require clojure.test)
  (:use [clojure.test :only [deftest is use-fixtures]])
  (:use [topoged.hibernate :only [with-session hibernate entity-map add-entity-factory
								  create-session-factory
								  hibernate-properties-config *hibernate-session-factory*]]))



(defn populate-tables [f]
  (hibernate)
  
  (with-session [session tx]
	; Use a HashMap
	(.save session "Event" (doto (java.util.HashMap.)
							 (.put  "title" "Our very first event!")
							 (.put "date" (Date.))))
	; Use a clojure map
	(.save session "Event" (entity-map {:title "A follow-up event"
										:date (Date.)}))
	; Use an entity factory
	(let [add-event (add-entity-factory "Event" title date)
		  add-person (add-entity-factory "Person" firstname lastname)]

	  (add-event "Added with entity factory" (Date.))
	  (add-person "Foo" "Bar")
	  
	  )
	)
  
  (f))
(deftest add-event-to-person-set []
		 (with-session [session _]
		   (let [
				 person (.load session "Person" 1)
				 event (.load session "Event" 3)]
			 (doto (. person (get "events")) (.add event))))
		 (with-session [session _]
		   (is (= 1 (count (.. session (load "Person" 1) (get "events" )))))))

(deftest list-in-tx []
	 (with-session [session _]
	   (let [events (.. session (createQuery "from Event") list)]
	     (is (= 3 (count events))))))

(deftest list-as-seq []
	 (let [events (with-session [session _] (.. session (createQuery "from Event") list))]
	   (is (= 3 (count events)))))

(deftest binding-test[]
	 (let [hsf (create-session-factory #(hibernate-properties-config
					     "test/hibernate-test.properties"
					     "test/Type.hbm.xml"))]
	   (binding [*hibernate-session-factory* hsf]
	     (with-session [session _]
	       (.save session "Type" (entity-map {:id  1
						 :name "Type1"
						 :desc "The first type"})))
	     (with-session [session _]
	       (let [type (merge {} (.get session "Type"  1))]
		 (println type)
		 (is (= "Type1"(type "name"))))))))
	     

(deftest basic-hibernate []
		 (populate-tables #())
		 (list-in-tx)
		 (list-as-seq)
		 (add-event-to-person-set)
		 )

(use-fixtures :once populate-tables)

(defn test-ns-hook []
  (basic-hibernate)
  (binding-test)
  )



