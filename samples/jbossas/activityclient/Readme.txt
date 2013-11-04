Activity Client
===============

This quickstart provides a test client for generating activity information.
The example business transactions are defined in the src/main/resources/txns folder.

To run the activity client, simply enter the following command from this folder:

mvn clean install -Dcount=1000

The 'count' property represents the number of transactions that will be created and can be
set to any value. If set to -1, then the client will run indefinitely.

You will be requested to enter the 'username' and 'password'. For example, you can use
the details configured when installing the governance capabilities on the server.


