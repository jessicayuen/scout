var Parse = require('parse').Parse;
var moment = require('moment');

var APP_ID = 'DiEded8eK6muPcH8cdHGj8iqYUny65Mva143CpQ3';
var MASTER_KEY = 'haRBk6ltEVIbnNmwsBkMYneYefjS9JSLOWyjxbjb';

var BUSINESS_TABLE = 'Business';
var CUSTOMER_TABLE = 'Customer';
var POINTS_TABLE = 'Points';
var INTERVAL_TABLE = 'Interval';
var INTERVAL_REC_TABLE = 'IntervalRecord';

var NUM_MIN_ARGS = 2;

var CUST_MIN_DUR = 5;
var CUST_MAX_DUR = 25;
var CHANCE_CUST_IN_STORE = 0.1;
var HOURS_OF_DATA = 3;

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/random
var getRandomInt = function (min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
};

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/random
var getRandomArbitrary = function (min, max) {
  return Math.random() * (max - min) + min;
}

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
    interval.set('from', from.toDate()); // dates are incorrectly inserted for some reason
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

var savePoints = function (business, customer, pointCount) {
    var pointsObj = Parse.Object.extend(POINTS_TABLE);
    var points = new pointsObj();

    points.set('business', business);
    points.set('customer', customer);
    points.set('firstVisit', moment().utc().toDate());
    points.set('points', pointCount);

    points.save(null, { error: function (obj, error) {console.log('savePoints: '+ error.message );} });

    return;
}

var insertIntervals = function (business, customer) {
    var previousWeekDate = moment().subtract(HOURS_OF_DATA,'hours').utc();
    var currentDate = moment().utc();

    for (var date=previousWeekDate; date<currentDate; date.add(5, 'minutes')) {
        if (Math.random() < CHANCE_CUST_IN_STORE) {
            var durationMin = getRandomInt(CUST_MIN_DUR, CUST_MAX_DUR);
            var points = getRandomInt(1,4);

            var fromDate = date.clone();
            var toDate = date.clone().add(durationMin, 'minutes');

            saveInterval(business, customer, fromDate, toDate).then(function(intervalObj) {
                toDate = date.clone().add(durationMin, 'minutes'); // unsure why this is needed

                return saveIntervalRecords(intervalObj, fromDate, toDate);
            })
            .then(function () {
                return savePoints(business, customer, points);
            })
            .then(function () {
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
        console.log('generateIntervalData: business name is null');
        process.exit(1);
    }
    if (username == null) {
        console.log('generateIntervalData: business name is null');
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
            console.log('generateIntervalData: unable to query business');
            process.exit(1);
        }
        if (customer == null) {
            console.log('generateIntervalData: unable to query customers');
            process.exit(1);
        }

        console.log('generateIntervalData: found '+ businessName);
        console.log('generateIntervalData: queried '+ customer.get('user').get('username'));

        insertIntervals(business, customer);
    });
};

var generateRewardData = function (businessName, username) {
    var business = null;
    var customer = null;

    if (businessName == null) {
        console.log('generateRewardData: business name is null');
        process.exit(1);
    }
    if (username == null) {
        console.log('generateRewardData: business name is null');
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
    .then(function () {
        if (business == null) {
            console.log('generateRewardData: unable to query business');
            process.exit(1);
        }
        if (customer == null) {
            console.log('generateRewardData: unable to query customers');
            process.exit(1);
        }

        console.log('generateRewardData: found '+ businessName);
        console.log('generateRewardData: queried '+ customer.get('user').get('username'));

    });
};

var getArgs = function (argc, argv) {
    var args = argv;
    var argsObj = {};

    args.splice(0,2);

    while(args.length > 0) {
        var temp = null;

        switch (args[0]) {
            case '-b':
                temp = args.splice(0,2);
                argsObj.business = temp[1];
                break;
            case '-c':
                temp = args.splice(0,2);
                argsObj.username = temp[1];
                break;
            case '-h':
                temp = args.splice(0,2);
                argsObj.hours = temp[1];
                break;
            default:
                console.log('invalid input');
                process.exit(1);
        }
    }

    return argsObj;
};

var main = function (argc, argv) {
    if (argc-2 < NUM_MIN_ARGS) {
        console.log('USAGE: node index.js [-b business] [-c customer username] [OPTIONAL: -h hours]');
        process.exit(1);
    }

    Parse.initialize(APP_ID, MASTER_KEY);

    var argsObj = getArgs(argc, argv);

    var businessName = argsObj.business;
    var username = argsObj.username;

    if (argsObj.hours) {
        HOURS_OF_DATA = argsObj.hours;
    }

    generateData(businessName, username);
};

main(process.argv.length, process.argv);
