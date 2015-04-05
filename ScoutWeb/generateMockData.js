var Parse = require('parse').Parse;
var moment = require('moment');

var APP_ID = 'DiEded8eK6muPcH8cdHGj8iqYUny65Mva143CpQ3';
var MASTER_KEY = 'haRBk6ltEVIbnNmwsBkMYneYefjS9JSLOWyjxbjb';
var BUSINESS_TABLE = 'Business';
var CUSTOMER_TABLE = 'Customer';
var INTERVAL_TABLE = 'Interval';
var INTERVAL_REC_TABLE = 'IntervalRecord';

var NUM_MIN_ARGS = 1;

var CUST_MIN_DUR = 5;
var CUST_MAX_DUR = 25;

var CHANCE_CUST_IN_STORE = 0.1;


var getBusinessQuery = function (businessName) {
    var businessObj = Parse.Object.extend(BUSINESS_TABLE);

    var query = new Parse.Query(businessObj);
    query.equalTo('name', businessName);

    return query.first();
};

var getAllCustomerQuery = function () {
    var customerObj = Parse.Object.extend(CUSTOMER_TABLE);

    var query = new Parse.Query(customerObj);

    return query.find();
};

var saveInterval = function () {
    var intervalObj = Parse.Object.extend(INTERVAL_TABLE);
    
}

var saveIntervalRecord = function () {
    var intervalRecObj = Parse.Object.extend(INTERVAL_REC_TABLE);

}

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/random
var getRandomInt = function (min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
};

var insertIntervalRecords = function (intervalObj, currentDate) {
    var durationMin = getRandomInt(CUST_MIN_DUR, CUST_MAX_DUR);


};

var insertIntervals = function (business, customersList) {
    var custInStoreQueue = [];
    var previousWeekDate = moment().subtract(7,'days').utc();
    var currentDate = moment().utc();

    for (var date=previousWeekDate; date<currentDate; date.add(5, 'minutes')) {
        if (Math.random() < CHANCE_CUST_IN_STORE && customersList.length) {
            var customer = custNotInStoreQueue.shift();

            // save
        }
    }
};

var generateData = function (businessName) {
    var business = null;
    var customersList = null;

    if (businessName == null) {
        console.log('generateData: business name is null');
        process.exit(1);
    }

    getBusinessQuery(businessName).then(function (businessObj) {
        // get business
        business = businessObj;

        return getAllCustomerQuery();
    })
    .then(function (customersObj) {
        // get customers
        customersList = customersObj;

        return;
    })
    .then(function() {
        // insert intervals
        if (business == null) {
            console.log('generateData: unable to query business');
        }
        if (customersList == null) {
            console.log('generateData: unable to query customers');
        }

        console.log('generateData: found '+ businessName);
        console.log('generateData: queried '+ customersList.length+' customers');

        insertIntervals(business, customersList);
    });
};

var main = function (argc, argv) {
    if (argc-2 < NUM_MIN_ARGS) {
        console.log('USAGE: node index.js [business name]');
        process.exit(1);
    }

    Parse.initialize(APP_ID, MASTER_KEY);

    var businessName = argv[2];

    generateData(businessName);
};

main(process.argv.length, process.argv);
