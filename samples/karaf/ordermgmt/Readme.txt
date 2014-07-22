Order Management - Example OSGi Application
===========================================

The sub-folders provide the components for an example Order Management application, with additional information processor and event processor network.

The application is comprised of a REST API (orderservice-rests) which uses the Order Service (orderservice), which in turn invokes the Inventory Service (inventoryservice) and Logistics Service (logisticsservice).

The Information Processor (ip) extracts relevant information from the sent and received messages. The Event Processor Network (epn) has been configured to convert any exceptions from the OSGi services into Situations.

To deploy the quickstart, after the console has been started, run:

	features:install rtgov-samples-ordermgmt         (if deployed with the rtgov-client feature)
	features:install rtgov-samples-ordermgmt-epn     (if deployed with the rtgov-all feature)

To undeploy the quickstart, run:

	features:uninstall rtgov-samples-ordermgmt       (if deployed with the rtgov-client feature)
	features:uninstall rtgov-samples-ordermgmt-epn   (if deployed with the rtgov-all feature)


NOTE: This quickstart can be deployed with the feature(s): rtgov-all or rtgov-client



