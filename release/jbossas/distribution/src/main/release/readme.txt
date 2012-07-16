OVERLORD Business Activity Monitoring README
============================================

Prerequisites:

Maven (http://maven.apache.org/download.html) version 3.0.4 or higher.


To install the Overlord Business Activity Monitoring system within a JBossAS environment:

1) Set the JBOSS_HOME environment variable to the root folder of your JBossAS environment

2) While the JBossAS server is not running, run the following command from the root folder of the BAM distribution:

mvn install


This will install all the necessary modules and deployments, include the Gadget Server.

For more information on using BAM, see https://docs.jboss.org/author/display/BAM/User+Guide, or for developing with it, see https://docs.jboss.org/author/display/BAM/Developer+Guide.

Next step - try out some of the quickstarts in the samples folder.



To uninstall the Overlord BAM system, run the following from the BAM distribution root folder:

mvn clean


