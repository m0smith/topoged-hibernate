(ns basic
  (:use [clojure.test :only [deftest is]])
  (:use [topoged.hibernate :only [with-hibernate-tx init]]))

(init #(.configure (org.hibernate.cfg.Configuration.)))

(defn wrap
  "Convert keywords to strings and make the map a HashMap"
  [m]
  (let [newmap (reduce conj m (map (fn [[k v]] [(name k) v]) m))]
    (java.util.HashMap. newmap)))


(deftest list-in-tx []
	 (with-hibernate-tx [session _]
	   (let [events (.. session (createQuery "from Event") list)]
	     (is (= 2 (count events))))))

(deftest list-as-seq []
	 (let [events (with-hibernate-tx [session _] (.. session (createQuery "from Event") list))]
	   (is (= 2 (count events)))))


(deftest basic-hibernate []
	 (with-hibernate-tx [session tx]
	     (.save session "Event" (wrap {:title "Our very first event!"
					   :date (java.util.Date.)}))
	     (.save session "Event" (wrap {:title "A follow-up event"
					   :date (java.util.Date.)})))
	 (list-in-tx)
	 (list-as-seq))


(defn test-ns-hook []
  (basic-hibernate))



