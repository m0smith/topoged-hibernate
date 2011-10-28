(ns basic
  (:use [clojure.test :only [deftest is]])
  (:use [topoged.hibernate :only [with-hibernate-tx init]]))

(init #(.configure (org.hibernate.cfg.Configuration.)))

(defn wrap
  "Convert keywords to strings and make the map a HashMap"
  [m]
  (let [newmap (reduce conj m (map (fn [[k v]] [(name k) v]) m))]
    (java.util.HashMap. newmap)))

(deftest basic-hibernate []
	 (with-hibernate-tx [session   tx  ]
	     (.save session "Event" (wrap {:title "Our very first event!"
					   :date (java.util.Date.)}))
	     (.save session "Event" (wrap {:title "A follow-up event"
					   :date (java.util.Date.)})))
	 (with-hibernate-tx [session _]
	   (let [rows (.. session (createQuery "from Event") list)]
	     (is (= 2 (count rows)))
	     (println rows))))

(deftest lazy-results []
	 (println 
	  (with-hibernate-tx [session _]
	    (let [rows (.. session (createQuery "from Event") list)]
	      rows))))







