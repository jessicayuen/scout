var Parse = require('parse').Parse;
var moment = require('moment');

var APP_ID = 'DiEded8eK6muPcH8cdHGj8iqYUny65Mva143CpQ3';
var MASTER_KEY = 'haRBk6ltEVIbnNmwsBkMYneYefjS9JSLOWyjxbjb';

var BUSINESS_TABLE = 'Business';
var CUSTOMER_TABLE = 'Customer';
var INTERVAL_TABLE = 'Interval';
var INTERVAL_REC_TABLE = 'IntervalRecord';

var NUM_MIN_ARGS = 2;

var CUST_MIN_DUR = 5;
var CUST_MAX_DUR = 25;
var CHANCE_CUST_IN_STORE = 0.1;
var HOURS_OF_DATA = 3;

var getBusinessQuery = function (businessName) {
    var businessObj = Parse.Object.extend(BUSINESS_TABLE);

    var query = new Parse.Query(businessObj);
    query.equalTo('name', businessName);

    return query.first();
};

var getAllCustomerQuery = function () {
    var customerObj = Parse.Object.extend(CUSTOMER_TABLE);

    var query = new Parse.Query(customerObj);
    query.include("user")

    return query.find();
};

var saveInterval = function (business, customer, from, to) {
    var intervalObj = Parse.Object.extend(INTERVAL_TABLE);
    var interval = new intervalObj();

    interval.set('business', business);
    interval.set('customer', customer);
    interval.set('from', from.toDate());
    interval.set('to', to.toDate());

    return interval.save(null, {error: function (obj, error) {console.log('saveInterval: '+ error.message )}});
}

var saveIntervalRecords = function (intervalObj, from, to) {
    var intervalRecObj = Parse.Object.extend(INTERVAL_REC_TABLE);

    for (var i=from.utc(); i < to.utc(); i.add(5,'minutes')) {
        var intervalRec = new intervalRecObj();

        intervalRec.set('interval', intervalObj);
        intervalRec.set('timestamp', i.toDate());
        intervalRec.set('coordX', getRandomArbitrary(3,5));
        intervalRec.set('coordY', getRandomArbitrary(3,5));

        intervalRec.save(null, { error: function (obj, error) {console.log('saveIntervalRecords: '+ error.message );} });
    }

    return;
};

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/random
var getRandomInt = function (min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
};

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/random
var getRandomArbitrary = function (min, max) {
  return Math.random() * (max - min) + min;
}

var insertIntervals = function (business, customer) {
    var previousWeekDate = moment().subtract(HOURS_OF_DATA,'hours').utc();
    var currentDate = moment().utc();

    for (var date=previousWeekDate; date<currentDate; date.add(5, 'minutes')) {
        if (Math.random() < CHANCE_CUST_IN_STORE) {
            var durationMin = getRandomInt(CUST_MIN_DUR, CUST_MAX_DUR);

            var fromDate = date;
            var toDate = date.clone().add(durationMin, 'minutes');

            saveInterval(business, customer, fromDate, toDate).then(function(intervalObj) {
                toDate = date.clone().add(durationMin, 'minutes'); // unsure why this is needed

                return saveIntervalRecords(intervalObj, fromDate, toDate);
            }).then(function () {
                date.add(durationMin, 'minutes');

                return;
            });
        }
    }
};

var generateData = function (businessName, username) {
    var business = null;
    var customer = null;

    if (businessName == null) {
        console.log('generateData: business name is null');
        process.exit(1);
    }
    if (username == null) {
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
        customersObj.some(function (obj) {
            var objUsername = obj.get('user').get('username');

            if (username == objUsername) {
                customer = obj;
            }

            return customer;
        });

        return;
    })
    .then(function() {
        // insert intervals
        if (business == null) {
            console.log('generateData: unable to query business');
            process.exit(1);
        }
        if (customer == null) {
            console.log('generateData: unable to query customers');
            process.exit(1);
        }

        console.log('generateData: found '+ businessName);
        console.log('generateData: queried '+ customer.get('user').get('username'));

        insertIntervals(business, customer);
    });
};

var main = function (argc, argv) {
    if (argc-2 < NUM_MIN_ARGS) {
        console.log('USAGE: node index.js [business name] [customer.user username] [OPTIONAL: hours of data (default=3)]');
        process.exit(1);
    }

    Parse.initialize(APP_ID, MASTER_KEY);

    var businessName = argv[2];
    var username = argv[3];

    if (argv[4] != null && typeof argv[4] === 'number') {
        HOURS_OF_DATA = argv[4]
    }

    generateData(businessName, username);
};

main(process.argv.length, process.argv);
