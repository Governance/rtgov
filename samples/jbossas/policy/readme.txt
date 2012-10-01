Policy Enforcement Sample
=========================

This example demonstrates how a Switchyard application can be instrumented, to intercept service
communications and report them to the BAM infrastructure. These events can then be analyzed to
apply business policies that can then be enforced within the runtime.

An example Event Processor Network (EPN) has been defined to check the credit worthiness of
customers purchasing goods, and if they exceed certain criteria, they are then suspended.
Once the customer is suspended, this should then prevent any further payment operations being
handled, unless they have settled some or all of their balance.


Deploying the example
---------------------

1) Make sure that the JBOSS_HOME variable is set to the location of the target JBossAS environment.

2) Build the Policy sample, using the following command from the ${bam}/samples/policy folder:

mvn clean install

This will automatically deploy the built war files into the JBossAS deployments folder. If the JBOSS_HOME
variable has not been set, then these war files will need to manually be copied from
${bam}/samples/policy/${component}/target/policy-${component}.war into the
${as7}/standalone/deployments folder.


NOTE: The components are as follows:

- epn: this component represents the Event Processor Network (EPN) used to evaluate the business policies
and record any suspended customers.



Running the example
-------------------

Startup a SOAP client (e.g. SOAPUI) and send an example message to the URL:

http://127.0.0.1:8080/demo-orders/OrderService

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

which should result in an 'order accepted'.


