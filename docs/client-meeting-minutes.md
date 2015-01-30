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

