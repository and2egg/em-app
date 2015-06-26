# Energy market management application


This application is part of my diploma thesis called "Cost aware resource management in distributed cloud data centers". 
It features a standalone application server which continually retrieves data from energy markets and provides forecasts based on this data. 
These price forecasts can be used in a cloud simulator (or possibly in a real cloud as well) where possible cost reductions due to changing energy prices in distributed data centers are investigated. 
It incorporates an R server which makes use of the powerful statistical tool R (see <a href="http://www.r-project.org/">here</a>) to build models and forecast data accordingly. 



## Project Setup


### Java Key Store

In order to get access to certain web services (e.g., ISO NE) via a secure HttpsUrlConnection some steps are initially necessary. 

	1) Go to a valid URL from the web service to be set up, access the certificate and save it Base64 encoded as a file on the local file system (path/to/cert). 

	2) Open a cmdline prompt as an administrator and execute the following line to list all certificates in the keystore:

		keytool -list -keystore "%JAVA_HOME%/jre/lib/security/cacerts"

	3) Insert the certificate file into the keystore (default password "changeit"): 

		keytool -import -noprompt -trustcacerts -alias <certalias> -file path/to/cert -keystore "%JAVA_HOME%/jre/lib/security/cacerts" -storepass changeit

	4) Check if the certificate has been added successfully by re-executing: 

		keytool -list -keystore "%JAVA_HOME%/jre/lib/security/cacerts"

	5) Before accessing the web service via a Java REST Client, set the following system property (this must be set every time JBOSS restarts): 

		System.setProperty("javax.net.ssl.trustStore", "path/to/cacerts")

	6) In the REST Client, check if some authentication is necessary after opening a HttpsURLConnection. Optionally set request property (e.g., JSON). 

	7) Call the web service and retrieve the data. 


		


## License

This project is licensed under the GNU General Public License, see also the license file at the root of the project. 
