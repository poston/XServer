XServer
=======

XServer is a simple http server that base on netty and spring.

The server framework as follow:

<pre>
+---+   +---+   +---+   +---+                  +---+
| 1 |   | 2 |   | 3 |   | 4 |   ... Client...  | n |
+---+   +---+   +---+   +---+                  +---+
                ||
                ||-------------------JSON,JSONP,XML
               \||/
+---------------------------------------------------+
|                XServer Http Interface             |			   
+---------------------------------------------------+
|      Jdbc(Mail,HBase,ElasticSearch...)Template    |
|---------------------------------------------------+
|                       Spring                      |
|---------------------------------------------------+
|                       Netty                       |
+---------------------------------------------------+
</pre>

The author E-mail: poston1@163.com

<ul>Plan:
<li>2013/01 Integrate Netty And Spring, implment basic HTTP Service</li>
<li>2013/02 Add some component, like Mail, Jdbc, Quartz, HBase...</li>
<li>2013/04 Bug fix and product apply</li>
<li>2014/04 Add some component, like Gradle, SVN, WebSocket</li>
<li>2015/04 Add some interface context and interface resolver(the basic version, just WebInterface), this version allow deployer to make decision which interface should be load by XServer</li>
<li>2015/06 Add Cookie and Session(user shiro)</li>
<li>2015/07 Add filter mechanism, like IP white(black) list, permission check, cache mechanism, parameter mapping</li>
<li>2015/09 Product apply</li>
<li>2016/03 Import ZooKeeper as configuration manager</li>
<li>2016/07 Use ZooKeeper for distribution managing(register service, fault manage...)</li>
<li>2016/09 Import Kafka for message system</li>
<li>2016/11 Import RPC service, change services to micro-service</li>
</ul>
