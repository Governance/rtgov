# The Runtime Governance UI

## Running UI

The Runtime Governance UI module builds as a WAR which can be deployed to a variety of Java containers, such as JBoss EAP, Fuse, etc.

Another (even easier) way to run (and test) the rtgov/ui project, against mock backend services, is to simply do this:

    $ mvn -Prun clean install

The "run" profile will be activated, which will launch the application using an embedded Jetty server.


