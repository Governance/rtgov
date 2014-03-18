Order Management - Application
==============================

This quickstart provides an example SwitchYard application, based on the Orders demo from the SwitchYard
project. The service accepts either an order or a payment. Orders are checked with the inventory service
and if available, the order is accepted and finally sent to the logistics service to be delivered
to the customer.

To deploy the quickstart, after the server has been started, run:

	mvn jboss-as:deploy

To test the service, there are a set of example request SOAP messages defined in the
test/main/resources/xml folder. These can be sent to the service using a SOAP client (e.g. SOAP UI),
initialized using the wsdl found in the src/main/resources/wsdl folder, or using the following command:

	mvn exec:java -Dreq=<name> [ -Dcount=<number> ]

NOTE: You will need to build the order management app, before being able to run this test client,
either by deploying the service or running "mvn clean install".

The 'req' property specifies the name of the request SOAP XML file. Available values are:

	order1 - Valid order for BUTTER, from customer Fred
	order2 - Invalid order for LAPTOP, from customer Fred, due to item not being available
	order3 - Valid order for JAM, from customer Fred, which results in a delay causing SLA violations
	order4 - Valid order for BUTTER, from customer Joe
	fredpay - Fred makes a payment

The 'count' property represents the number of requests that will be sent - the default value is 1.

Further information about using this Order Management application with other quickstarts (e.g. sync and
async policy enforcement) can be found in the QuickStart Guide.

To undeploy the quickstart, run:

	mvn jboss-as:undeploy


NOTE: This quickstart can be deployed with the profile(s): client or server (needs to be co-located with
switchyard execution environment)

