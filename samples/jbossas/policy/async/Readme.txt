Asynchronous Policy Enforcement
===============================

This quickstart provides an example of a business policy that has been defined to run asynchronously
within the activity server. The decisions taken by this policy are only actionable after the business
transaction has completed, hence the asynchronous nature of the enforcement. In this specific example,
if the customer exceeds their credit limit, then any subsequent order requests received after the
decision has been made, will be rejected due to the customer being suspended. To unsuspend the customer,
a payment must be made.

For this quickstart to function correctly, you must also deploy the Order Management Application and
Information Processor.

To deploy the quickstart, after the server has been started, run:

	mvn jboss-as:deploy

To undeploy the quickstart, run:

	mvn jboss-as:undeploy


NOTE: This quickstart can be deployed with the profile(s): server

