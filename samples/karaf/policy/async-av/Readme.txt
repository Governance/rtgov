Asynchronous Policy Enforcement
===============================

This quickstart provides an example of a business policy that has been defined to run asynchronously
within the activity server. The decisions taken by this policy are only actionable after the business
transaction has completed, hence the asynchronous nature of the enforcement. In this specific example,
if the customer exceeds their credit limit, then any subsequent order requests received after the
decision has been made, will be rejected due to the customer being suspended. To unsuspend the customer,
a payment must be made.

This "Activity Validator (AV)" is used to examine the suspend status of the customer associated with
an inbound request, to determine if the request should be blocked.

For this quickstart to function correctly, you must also deploy the Order Management Application and
Information Processor.

To deploy the quickstart, after the console has been started, run:

	features:install rtgov-samples-policy-async

To undeploy the quickstart, run:

	features:uninstall rtgov-samples-policy-async


NOTE: This quickstart can be deployed with the feature(s): rtgov-all



