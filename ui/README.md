# The Runtime Governance UI

## Debugging the UI

The UI has been developed with GWT and Errai. To debug the UI, you will need to:

* Deploy the UI war to an application server and start up the server

* Run "mvn gwt:run-codeserver" from the _ui/overlord-rtgov-ui-base_ folder of the RTGov source

* Open your browser to localhost:9876 and copy the "Dev Mode On" bookmark somewhere

* Open another browser window on the RTGov UI: http://localhost:8080/rtgov-ui

* Click the "Dev Mode On" book mark. It should prompt you with a white box on the page. Click the "Compile" button

Using Google Chrome, you can then turn on Source Maps somewhere in the settings, and you will then be able to see the Java source code and set breakpoints using the developer tools.



