XServer
=======

XServer is a simple http server that base on netty and spring.

The server framework as follow:


+---+	+---+	+---+	+---+				           +---+
| 1 |	| 2 |	|	3 |	| 4 |   ... Client...  | n |
+---+	+---+	+---+	+---+				           +---+
				    ||
				    ||-------------------JSON,JSONP,XML
			     \||/
+---------------------------------------------------+
|			    	    XServer Http Interface			      	|			   
+---------------------------------------------------+
|     Jdbc(Mail,HBase,ElasticSearch...)Template     |
|---------------------------------------------------+
|						             Spring        					    |
|---------------------------------------------------+
|						              Netty         						|
+---------------------------------------------------+
