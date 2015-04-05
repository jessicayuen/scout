var Parse = require('parse').Parse;
var moment = require('moment');

var APP_ID = 'DiEded8eK6muPcH8cdHGj8iqYUny65Mva143CpQ3';
var MASTER_KEY = 'haRBk6ltEVIbnNmwsBkMYneYefjS9JSLOWyjxbjb';
var BUSINESS_TABLE = 'Business';
var CUSTOMER_TABLE = 'Customer';
var NUM_MIN_ARGS = 1;

var getBusinessQuery = function (businessName) {
    var businessObj = Parse.Object.extend(BUSINESS_TABLE);

    var query = new Parse.Query(businessObj);
    query.equalTo(businessName);

    return query;
};

var getAllCustomerQuery = function () {
    var customerObj = Parse.Object.extend(BUSINESS_TABLE);

    return new Parse.Query(customerObj);
};

var insertIntervalRecords = function (intervalObj, currentDate) {
    var durationMin = 20; // need to generate random minute range

    
};

var insertIntervals = function (business, customersList) {
    var previousWeekDate = moment().subtract(7,'days').utc();
    var currentDate = moment().utc();

    for (var date=previousWeekDate; date<currentDate; date.add(5, 'minutes')) {
        // need to figure out how to generate random intervals
        //console.log(date.format("MM-DD-YYYY HH:mm:ss"));
    }
};

var generateData = function (businessName) {
    var business = null;
    var customersList = null;

    if (businessName == null) {
        console.log('generateData: business name is null');
        process.exit(1);
    }

    getBusinessQuery(businessName).first().then(function (businessObj) {
        // get business
        business = businessObj;

        return getAllCustomerQuery().find();
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
