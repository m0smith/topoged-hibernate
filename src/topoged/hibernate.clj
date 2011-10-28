(ns topoged.hibernate
  (:import
   	   [java.io FileReader]
	   [java.util Properties]
	   [org.hibernate EntityMode Session Transaction]
	   [org.hibernate.cfg AnnotationConfiguration Configuration ]
	   [org.hibernate.impl SessionFactoryImpl]
	   )
  (:use [org.satta.glob :only (glob)])
  (:use [clojure.tools.logging :only (info error )]))


(defn hibernate-properties-config
  ([prop-file & hbm-globs]
      (let [props (doto (Properties.)
		    (.load (FileReader. prop-file))
		    )
	    cfg (doto (AnnotationConfiguration.)
		  (.addProperties props)
		  )]
	(doseq [hbm-glob hbm-globs]
	  (doseq [file (glob hbm-glob)]
	    (let [p (.getAbsolutePath file)]
	      ( .addFile cfg p))))
	cfg)))

(defn init
  "Initialize the hibernate session factory"
  ([] (init #(.configure (Configuration.))))
  ([cfg-fn]
     (let [hsf (memoize #(.buildSessionFactory ( cfg-fn)))]
       (letfn [( begin-tx-local []
		 (try
		   (let [^SessionFactoryImpl sf (hsf)
			 session (.. sf openSession (getSession EntityMode/MAP))
			 tx (. session beginTransaction)]
		     [session tx])
		   (catch Exception ex
		     (error ex "Failed to start transaction"))))]
	 (intern 'topoged.hibernate 'begin-tx begin-tx-local)))))

(defmacro with-hibernate-tx
  "Execute body in the context of a hibernate trasnascton.
 The session and tx parameters are set with the hibernate session and
a transaction.  The transaction is commited unless an Exception is
thrown in body.  If there is an unhandled exception thrown in body,
the transaction will be rolled back.  The session is also closed
regardless of any exceptions"
  [[session tx] & body]
  (let [src (gensym "src") rtnval (gensym "rtnval") ex (gensym "ex")]
    `(let [~src (begin-tx)
	   ~(with-meta session {:tag 'org.hibernate.Session}) (first ~src) 
	   ~(with-meta tx {:tag 'org.hibernate.Transaction}) (second ~src)]
       (try
	 (let [~rtnval  (do ~@body)]
	   (. ~session flush)
	   (. ~tx commit)
	   ~rtnval)
	 (catch Exception ~ex
	   (try
	     (if (and ~tx (.isActive ~tx))
	       (.rollback ~tx))
	     (finally
	      	(error ~ex "Rollback transaction")
		(throw ~ex))))
	 (finally
	  (if (and ~session (. ~session isOpen))
	    (.close ~session)))))))
