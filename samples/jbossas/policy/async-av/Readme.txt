Asynchronous Policy Enforcement (AV)
====================================

This quickstart provides the enforcement aspect of the policy-async-epn quickstart, to block transactions
being performed by suspended customers.

For this quickstart to function correctly, you must also deploy the Order Management Application and
Information Processor, as well as the Policy Async EPN quickstart.

To deploy the quickstart, after the server has been started, run:

	mvn jboss-as:deploy (for EAP)
	mvn wildfly:deploy (for Wildfly)

To undeploy the quickstart, run:

	mvn jboss-as:undeploy (for EAP)
	mvn wildfly:undeploy (for Wildfly)


NOTE: This quickstart can be deployed with the profile(s): client or server

