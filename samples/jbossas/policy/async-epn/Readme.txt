Asynchronous Policy Enforcement (EPN)
=====================================

This quickstart provides an example of a business policy that has been defined to run asynchronously
within the activity server. The decisions taken by this policy are only actionable after the business
transaction has completed, hence the asynchronous nature of the enforcement. In this specific example,
if the customer exceeds their credit limit, then any subsequent order requests received after the
decision has been made, will be rejected due to the customer being suspended. To unsuspend the customer,
a payment must be made.

The enforcement of the policy, to block suspended customers, is performed by the policy-async-av
quickstart.

For this quickstart to function correctly, you must also deploy the Order Management Application and
Information Processor.

To deploy the quickstart, after the server has been started, run:

	mvn jboss-as:deploy (for EAP)
	mvn wildfly:deploy (for Wildfly)

To undeploy the quickstart, run:

	mvn jboss-as:undeploy (for EAP)
	mvn wildfly:undeploy (for Wildfly)


NOTE: This quickstart can be deployed with the profile(s): server

