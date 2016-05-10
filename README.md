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

<ul>Develop History And Plan:
<li>2013/01 <strong>Integrate</strong> Netty And Spring, implment <strong>basic</strong> HTTP Service</li>
<li>2013/02 Add some <strong>component</strong>, like Mail, Jdbc, Quartz, HBase...</li>
<li>2013/04 <strong>Bug fix</strong> and product apply</li>
<li>2014/04 Add some <strong>component</strong>, like Gradle, SVN, WebSocket</li>
<li>2015/04 Add some interface context and interface resolver(the basic version, just WebInterface), this version allow deployer to make decision(<strong>customization</strong>) which interface should be load by XServer</li>
<li>2015/06 Add <strong>Cookie and Session</strong>(user shiro)</li>
<li>2015/07 Add <strong>filter mechanism</strong>, like IP white(black) list, permission check, cache mechanism, parameter mapping</li>
<li>2015/09 Product <strong>apply</strong></li>
<li>2016/03 Import <strong>ZooKeeper</strong> as configuration manager</li>
<li>2016/07 Use ZooKeeper for <strong>distribution managing</strong>(register service, fault manage...)</li>
<li>2016/09 Import <strong>Kafka</strong> for message system</li>
<li>2016/11 Import <strong>RPC</strong> service, change services to <strong>micro-service</strong></li>
</ul>

<p>
<strong>Note the source on the github is <U>2013/04</U> version<strong>

