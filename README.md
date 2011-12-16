# topoged-hibernate

The topoged-hibernate library is part of the larger topoged project.
Its purpose is to create a Clojure interface to Hibernate.  The goals
of this project are:

*  To remove all the boilerplate code.
*  Allow Hibernate configuration to be used as is.  There is no need
   to create a new configuration mechanism in Clojure; XML is just fine.
*  Allow for programatic configurarion.
*  Integrate with existing Clojure paradigms. 


## Usage

The following simple example shows how to save a record in to a table.

    (hibernate)
	(with-session [session tx]
	    (.save session "Event" (entity-map 
            {:title "A follow-up event"  :date (Date.)}))

The `(hibernate)` call initializes Hibernate using the standard
hibernate initialization.

`with-session` is a macro that gets a session and a transaction and execeutes the body of within the transaction, closing the transaction at the end.

## Caveats

This has only been tested with the dynamic-map entities in hibernate.

There is a mismatch between hibernate maps and clojure maps which
means that there is some translating between them.  Hopefully, this
will be smoothed over in the future


## License

Copyright (C) 2011 

Distributed under the Eclipse Public License, the same as Clojure.
