Runtime Governance Samples
==========================

Each of the sub-folders contains a quickstart example. A description of the quickstarts, including how to
build and run them, can be found in the individual Readme.txt files with the quickstarts.

To build and deploy all of the deloyable quickstarts, simply use the following command from this folder, after the
server has been started:

	mvn jboss-as:deploy

and to undeploy:

	mvn jboss-as:undeploy



NOTE: The samples in this folder have been implemented to work with the 'server' configuration, where runtime
governance is co-located with the executing SwitchYard applications. If using a distributed configuration,
with the 'client' installation of runtime governance, then the Infinispan caches used by the policy quickstarts
will need to be configured to be accessilble by both client and server machines.


