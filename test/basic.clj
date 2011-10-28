(ns basic
  (:import [java.util Date])
  (:use [clojure.test :only [deftest is use-fixtures]])
  (:use [topoged.hibernate :only [with-hibernate-tx init]]))


(defn to-entity
  "Convert keywords to strings and make the map a HashMap"
  [m]
  (let [newmap (reduce conj m (map (fn [[k v]] [(name k) v]) m))]
    (java.util.HashMap. newmap)))

(defn populate-tables [f]
  (init)
  (with-hibernate-tx [session tx]
    (.save session "Event" (to-entity {:title "Our very first event!"
				       :date (Date.)}))
    (.save session "Event" (to-entity {:title "A follow-up event"
				       :date (Date.)})))
  (f))

(deftest list-in-tx []
	 (with-hibernate-tx [session _]
	   (let [events (.. session (createQuery "from Event") list)]
	     (is (= 2 (count events))))))

(deftest list-as-seq []
	 (let [events (with-hibernate-tx [session _] (.. session (createQuery "from Event") list))]
	   (is (= 2 (count events)))))


(deftest basic-hibernate []
	 (populate-tables #())
	 (list-in-tx)
	 (list-as-seq))

(use-fixtures :once populate-tables)

(defn test-ns-hook []
  (basic-hibernate))



