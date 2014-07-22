Service Level Agreement - Event Processor Network
=================================================

This quickstart provides an example of an Event Processor Network that is used to detect Service
Level Agreement violations.

Two different rules have been provided, SLAViolationParameterized.drl that uses parameterized information within
the epn.json configuration to determine what the response time levels to severity should be, and the
SLAViolationDerived.drl which uses a more transitional rule structure, where the decision is defined in the left
hand side of the rule.

NOTE: Currently on the SLAViolationParameterized.drl rule actually generates 'Situation' objects, to indicate
that a SLA violation has occurred. The SLAViolationDerived.drl simple outputs a message. However the body
of these rules can be updated to also create 'Situation' objects. 

For this quickstart to function correctly, you must also deploy the Order Management Application and
Information Processor.

To deploy the quickstart, after the server has been started, run:

	mvn jboss-as:deploy

To undeploy the quickstart, run:

	mvn jboss-as:undeploy


NOTE: This quickstart can be deployed with the profile(s): server

