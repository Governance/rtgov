OVERLORD Runtime Governance README
==================================

Prerequisites:

Maven (http://maven.apache.org/download.html) version 3.0.4 or higher.

JBoss EAP 6.1.0.GA or higher.

SwitchYard 2.0.0.Final or higher. This will need to be installed into JBoss EAP.

Download Elasticsearch 1.1.1 or higher. Unzip the distribution and start the server from the bin folder using the 'elasticsearch' command. This will need to be running before starting EAP with RTGov server installed.


To install the Overlord Runtime Governance system within a JBoss EAP environment:

1) Set the JBOSS_HOME environment variable to the root folder of your JBossEAP environment

2) While the JBoss EAP server is not running, run the following command from the root folder of the distribution:

mvn install


This will install all the necessary modules and deployments for the full server. If a 'client only' installation is required, which will report activity events to a remote server, then specify the appropriate configuration using the "-Dtype=client" option.

For more information on using Runtime Governance, see http://www.jboss.org/overlord/documentation/rtgov

Next step - try out some of the quickstarts in the samples folder.



To uninstall the Overlord Runtime Governance system, run the following from the distribution root folder:

mvn clean


