Elasticsearch - Event Processor Network
=================================================

This quickstart provides an example of an Event Processor Network that stores ActivityUnits, ResponseTime and Situations to ElasticSearch

The EPN defines 3 simple nodes. Which store each Serializable type
 - ElasticSearchActivityUnitStore  : Subscribes to ActivityUnits
 - ElasticSearchResponseTimeStore  : Subscribes to ServiceResponseTimes
 - ElasticSearchSituationStore     : Subscribes to Situations

BasicElasticSearchProcessor
 Responsible for accepting the type and persist and sends it directly a ElasticSearchRepo
 Uses MVEL to extract a id to store the object under. If "NONE" defined then a random id is assigned


BasicElasticAPIRepo
 - Responsible for connecting and storing documents in elastic search.
 - Configurable with elasticsearch configuration properties. index, type, host, port


Requirements
  - Elasticsearch installation
  - Kibana is recommend to visualize the results of the EPN in operation
  - To best visualise it is recommend to run the SLA quick start in combination with this epn. The SLA quickstart will result in all 3 types been store in elasticsearch

https://issues.jboss.org/browse/RTGOV-342
The epn is provided as a proof of concept. Production ready implementation would extend both the Eventprocessor and ELasticRepo to only store what the project or business deems relevent

For this quickstart to function correctly, you must also deploy the Order Management Application and
Information Processor.

To deploy the quickstart, after the server has been started, run:

	mvn jboss-as:deploy

To undeploy the quickstart, run:

	mvn jboss-as:undeploy

