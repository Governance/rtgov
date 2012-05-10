SLA Monitor Sample
==================

This example demonstrates how a Switchyard application can be instrumented, to intercept service
communications and report them to the BAM infrastructure.

An example Event Processor Network (EPN) has been defined to check the response time of the service
and report issues to an active collection.

TODO: UPDATE WHEN FININSHED EXAMPLE.


Deploying the example
---------------------

1) Download and unpack a Switchyard AS7 distribution (http://www.jboss.org/switchyard/downloads)

2) Startup the server, using the following command from the 'bin' folder:

./standalone.sh --server-config standalone-full.xml

This starts the full configuration, as this includes JMS.

3) Deploy the savara-bam.war from the ${bam}/modules folder into the ${as7}/standalone/deployments folder

4) Build the SLA Monitor sample, using the following command from the $bam/samples/slamonitor folder:

mvn clean install

5) Copy the ${bam}/samples/slamonitor/epn/target/samples-jbossas-slamonitor-epn-<version>.war file
into the ${as7}/standalone/deployments folder. This deployable artifact represents the samples
Event Processor Network (EPN), used to detect SLA violations.

6) Copy the ${bam}/samples/slamonitor/epn/target/samples-jbossas-slamonitor-orders-<version>.war file
into the ${as7}/standalone/deployments folder. This deployable artifact represents the Switchyard
application with additional 'Exchange Handler' used to intercept service communications and report
them to the BAM infrastructure.


Running the example
-------------------

Startup a SOAP client (e.g. SOAPUI) and send an example message to the URL:

http://127.0.0.1:18001/demo-orders/OrderService

The initial message could be:

<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <orders:submitOrder xmlns:orders="urn:switchyard-quickstart-demo:orders:1.0">
            <order>
                <orderId>PO-19838-XYZ</orderId>
                <itemId>BUTTER</itemId>
                <quantity>400</quantity>
            </order>
        </orders:submitOrder>
    </soap:Body>
</soap:Envelope>

which should result in an 'order accepted'. Then send the following message:

<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <orders:submitOrder xmlns:orders="urn:switchyard-quickstart-demo:orders:1.0">
            <order>
                <orderId>PO-19838-XYZ</orderId>
                <itemId>BUTTER</itemId>
                <quantity>1400</quantity>
            </order>
        </orders:submitOrder>
    </soap:Body>
</soap:Envelope>

which should result in an 'insufficient quantity' response.


TODO: Need to describe how to view the SLA monitor output - and also how to vary the message
to trigger SLA violations.


