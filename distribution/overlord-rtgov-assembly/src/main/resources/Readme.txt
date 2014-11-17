OVERLORD Runtime Governance README
==================================

Prerequisites:

Ant (http://ant.apache.org/bindownload.cgi) version 1.9.2 or higher.

Maven (http://maven.apache.org/download.html) version 3.0.4 or higher. [Required for deploying quickstarts]

JBoss EAP 6.1.0.GA or higher.

SwitchYard 2.0.0.Final or higher. This will need to be installed into JBoss EAP.

Download Elasticsearch 1.1.1 or higher. Unzip the distribution and start the server from the bin folder using the 'elasticsearch' command. This will need to be running before starting EAP with RTGov server installed.


To install the Overlord Runtime Governance system within an EAP6 environment, run the following command from the top level folder:

    ant [-Dplatform=eap6] [-Dtype={all|client}] [-Dpath=<eap6dir>]

If no parameters are defined, then the defaults are:

    plaform: eap6
    type: all
    path: no default - will request path from user


For more information on using Runtime Governance, see http://www.projectoverlord.io/rtgov.

Next step - try out some of the quickstarts in the samples folder.



To uninstall the Overlord Runtime Governance system, run the following from the distribution root folder:

    ant uninstall [-Dplatform=eap6] [-Dtype={all|client}] [-Dpath=<eap6dir>]

with the same parameters used during installation.
