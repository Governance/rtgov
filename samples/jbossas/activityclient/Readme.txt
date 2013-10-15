Activity Client
===============

This quickstart provides a test client for generating activity information.
The example business transactions are defined in the src/main/resources/txns folder.

Before being able to run the quickstart, you will need to edit the pom.xml to
set the 'overlord.username' and 'overlord.password' properties at the top of
the file. These should be set to the username (which generally defaults to 'admin')
and password entered during installation.

To run the activity client, simply enter the following command from this folder:

mvn clean install -Dcount=1000

The 'count' property represents the number of transactions that will be created and can be
set to any value. If set to -1, then the client will run indefinitely.


