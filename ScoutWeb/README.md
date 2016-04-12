Scout Webapp
===============
The Scout web application is utilized by business owners in which is used to modify their business' rewards program and to analyze their own customer data.

Required Modules
-----------------------
* node.js
* npm

Running the application
-----------------------
To run the application, go to the Scout Webapp root directory and run: `node app.js`

Generating mock data
-----------------------
To generate mock data for a day run: `USAGE: node generateMockData.js [-b business] [-c customer username] [-d date (YYYY-MM-DD) [OPTIONAL: -p percent chance (0.0-1.0)]] [-r add rewards]`

(ie. node generateMockData.js -b Ellantos -c test@test.com -d 2015-09-23)