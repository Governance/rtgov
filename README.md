# Overlord Runtime Governance (RTGov)

Any feature requests or issues with the RTGov component should be reported here: https://issues.jboss.org/browse/RTGOV


## Setting up the environment

When building the repository, the 'release' folder is platform specific and therefore need a
target platform to be specified. All other folders are platform agnostic.

The following environment needs to be setup for the selected target platform:


JBossAS7/Switchyard:

The 'jbossas' target platform requires JBossEAP with Switchyard. This can be obtained from: http://www.jboss.org/switchyard/downloads

(Note: currently requires switchyard-1.0 or later).

Once installed, set the JBOSS_HOME environment variable to point to the root of the JBossEAP/SwitchYard installation.



## Building the distribution

From the root folder, build the distribution using:

**mvn clean install**

Once the build has completed, the distribution can be found in the release/jbossas/distribution/target folder.

NOTE: When building from source for the first time, you should include the _-Pdocs_ option, as the documentation will be required to build the distribution.

It is recommended that you have MAVEN_OPTS set to the following: -Xms512m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m

### Integration tests for EAP

If the following environment variables have been defined, then the integration tests will automatically
be invoked using the specified JBossEAP/SwitchYard environment.

EAP_DIST: The path to the EAP distribution zip.

SWITCHYARD_DIST: The path to the Switchyard Installer zip.

JBOSS_HOME: This is the target location of the EAP environment.


### Generating Javadocs and REST APIs

To generate the javadocs and REST API documentation, include the **docs** profile when building:

**mvn clean install -Pdocs**

The javadocs will be generated to the docs/javadoc/target folder, and the REST API docs in the docs/restapi/target folder.


### Building RTGov UI for deploying to FSW 6.0

To generate the RTGov UI war for use in FSW 6.0, you will need to include the profile **fsw60**:

**mvn clean install -Pfsw60**

The overlord-rtgov-ui.war is located in the ui/overlord-rtgov-ui-fsw60/target.


## Contributing

Instructions on setting up an Eclipse development environment can be found here: https://github.com/Governance/rtgov/wiki/Setting-Up-Eclipse-Development-Environment

Please also read the guidelines for contributors: https://github.com/Governance/overlord/wiki/Contributor-Guidelines

