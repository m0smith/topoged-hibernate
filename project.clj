(defproject topoged-hibernate "1.0.0-SNAPSHOT"
  :description "topoged hibernate sub-project"
  :dependencies [[org.clojure/clojure "1.3.0"]
		 [org.hibernate/hibernate-annotations "3.4.0.GA"]
		 [org.slf4j/slf4j-api "1.6.2"]
		 [clj-glob "1.0.0"]
		 [org.clojure/tools.logging "0.2.3"]]
  :dev-dependencies [[swank-clojure "1.3.3"]
		     [com.h2database/h2 "1.2.139"]
		     [org.slf4j/slf4j-log4j12 "1.6.2"]
		     [org.slf4j/log4j-over-slf4j "1.6.2"]])