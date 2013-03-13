OVERLORD Runtime Governance README
==================================

Prerequisites:

Maven (http://maven.apache.org/download.html) version 3.0.4 or higher.


To install the Overlord Runtime Governance system within a JBossAS environment:

1) Set the JBOSS_HOME environment variable to the root folder of your JBossAS environment

2) While the JBossAS server is not running, run the following command from the root folder of the distribution:

mvn install


This will install all the necessary modules and deployments, include the Gadget Server, for the full server. If a 'client only' installation is required, which will report activity events to a remote server, then specify the appropriate configuration using the "-Dtype=<type>" option. The only client type currently supported is 'restc', to use a client installation that reports activity events to the server using REST.

For more information on using Runtime Governance, see http://www.jboss.org/overlord/documentation/rtgov

Next step - try out some of the quickstarts in the samples folder.



To uninstall the Overlord Runtime Governance system, run the following from the distribution root folder:

mvn clean


