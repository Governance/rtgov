# The Runtime Governance UI

## Running UI

The Runtime Governance UI project builds as a WAR which can be deployed to a Java application server such as JBoss EAP.
In fact, a specific EAP 6.x version of the WAR is created during the build process.

Another (even easier) way to run the rtgov-ui project is to simply do this:

    $ mvn -Prun clean install

The "run" profile will be activated, which will launch the application using an embedded Jetty server.


