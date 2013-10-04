Synchronous Policy Enforcement
==============================

This quickstart provides an example of a business policy that has been defined to run synchronously
within the execution environment. The decisions taken by this policy are immediately actionable, allowing
them to be used to block the business transaction. In this specific example, if the customer issues multiple
requests too quickly, the requests will be rejected, so providing a crude form of throttling.

For this quickstart to function correctly, you must also deploy the Order Management Application and
Information Processor.

To deploy the quickstart, after the server has been started, run:

	mvn jboss-as:deploy

To undeploy the quickstart, run:

	mvn jboss-as:undeploy


NOTE: This quickstart can be deployed with the profile(s): client or server (needs to be co-located with
switchyard execution environment)

