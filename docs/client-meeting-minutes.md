Jan 15, 2015 Client Meeting
---------------------------
* Research available on market BLE (iBeacon) vendors (TI, Qualcomm, etc)
    * Make a choice on a vendor
    * Research on simultaneous device support
    * Research on mobile device support
    * Research on whether multiple BLE devices can communicate with each other
    * Make note of pros and cons
* Make notes of quirks when running into problems in project
*BLE may not be the best option, wifi could be better
    * Smartphones continuously pings for wifi hotspots
        * Each ping may broadcast data of phone such as MAC address which we can keep track of
* Potential problems with mobile application:
    * Battery life
        * Potential solution could be to use GPS to locate device, and if device is within proximity, turn on bluetooth
            * May not be feasible
            * GPS requires data, not every customer has a data plan
                * Businesses are happy with just tracking 30% of their customers, thus not tracking every customer is fine
    * Bluetooth may not be on
* Another feature could be to have additional points for a longer stay
    * Ex. Claim 4 points for entering store, additional 1 point for every additional 10 minutes
* Consider using BLE for triangulation purposes
    * Keep track of user through store
    * Accuracy of location is a concern
* Website dashboard kept as simple as possible
    * Business owner access only
* Mobile application would preferably be cross-platform, however just Android is good enough
* Steps for proceeding in project
    * Step 1
        * Pick hardware
    * Step 2
        * Figure out whether the customer is inside the business or not
        * If inside business:
            * Give promo for first visit
        * If not inside business:
            * If customer has not been to business for a week, send promo to drive incentive

Project Sequence:

1. Setup backend
    * Get BLE beacons communicating
    * Have BLE beacon communicating with server
2. Setup web dashboard
3. Setup mobile application

Derived user stories from discussion:

* As a customer, I&rsquo;d like to walk into a business have my mobile phone detect that I&rsquo;m there and offer me a discount
* As a business owner, I&rsquo;d like to know who, how often, and how long customers are coming into the store
* As a business owner, I&rsquo;d like to drive incentive to customers that have not been in the store for a while
* As a business owner, I&rsquo;d like to know which locations in the stores the customers visiting the most often

Jan. 26, 2015 Client Meeting (Irregular)
----------------------------------------
* Further research available options aside from Bluetooth, such as GPS, and maybe WiFi. Further investigate the technologies FourSquare is using to perform their services.
* Focus on getting a seamless user experience, in that the user should be able to click accept on the agreement terms and have the app take care of the rest. Part of the reason why Bluetooth may not be the top choice is that users don’t typically leave their Bluetooth on.
* Investigate the ability to have the app run in the background and perform these services.
* Emphasized on the importance of triangulation.
    * Example of being in a line, having multiple beacons, and the ability to detect line movement.
* Mentioned the possibility of calibrating signal strength (since that’s how we’re approximating distance of user to Bluetooth Beacon), to help out with possible interference.
* UI, user usability is of little importance. Focus on getting the technology (i.e. triangulation, background services) to work, and the ability to track customer data.
* Kyle will place an order on 3 Estimote Beacons.

Jan. 30, 2015 Client Meeting
-----------------------------
* Went over user requirement plan with kylem specifically user stories.
   * Agreed upon which of the user stories were right to cut out and which one to keep. Cutting out condition being whether or not it was feasible or related to what kyle wanted in the first place, since many new points were brought up during our previous irregular meeting.
	* Extensive points system had its priority lowered in the backlog as it was more of a niche than a necessity, if time arises in the end implementations can be looked into.

* Noted that the user story regarding to approximate location of user within a beacon was one of the main features Kyle would have liked in the working prototype.

* The type of data collected and shown as well as method of collection were discussed extensively in the meeting.
   * whether or not the application is running in the foreground of background when collecting data
	* amount of time spent in the store
	* distance based (approximity - meaning data is collected whenever user comes in range of a BLE beacon)
	* ability to identify specific beacon as well as signal strength to said beacon (this will individualize each stores as users are probably going to be visiting more than one Scout associated merchant
	* association of the data collected with specific user (ability to identify each individual user - male or female, age, name, etc) (ethicality should be considered with this idea)

* Items to be displayed on the dashboard specifically to the merchants were also reviewed in the meeting
	* number of new customers
	* list of registered user who have visited the store
	* ability to display user location data within the store(relativity to a beacon within the store) (this data can also be used to map out the location attracting most users)
	* user heat map (displaying user location concentration within the areas of the store) with respect to time derived from point above
	* beacon specific information (e.g. stores names, location, etc)
	* Average time a user spent in the store
	* number of points given (either per user or average)
	* set rewards for users to claim (ability for merchants to alter the prizes customers can redeem with points)
	* rewards redeemed by the user (data on what the users are redeeming with their points)

* Another topic we went over was the items to be displayed on the android appliation, these are user specific
	* Number of points accumulated
	* Type of rewards that can be redeemed

#####With all the discussion points noted above the following user stories were either added or modified:
   * As a business owner, I’d like to be able to utilize BLE sensors to track customer interaction with my store
   * As a business owner, I want to be able to sign in into an online dashboard
   * As a business owner, I’d like to be able to track a customer’s approximate location within a certain radius of the beacon
   * As a business owner, I’d like to utilize the hardware’s ability to trilaterate the user’s approximate movement within the store
   * As a customer, I’d like to be rewarded points for visiting a business once a day
   * As a business owner, I’d like to be able to see the movement of the customer on my dashboard in the shape of a heat map
   * As a business owner, I want the app to run in the 
background to collect the data
   * As a business owner, I want the average time a customer was in the store to be visible on my dashboard
   * As a customer, I would like to be able to sign in to the mobile application
   * As a customer, I want to be able to redeem my points for rewards
   * As a business owner, I want to know the number of points I have given customers visible on my dashboard

