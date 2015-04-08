var Parse = require('parse').Parse;
var moment = require('moment');

var APP_ID = 'DiEded8eK6muPcH8cdHGj8iqYUny65Mva143CpQ3';
var MASTER_KEY = 'haRBk6ltEVIbnNmwsBkMYneYefjS9JSLOWyjxbjb';

var BUSINESS_TABLE = 'Business';
var CUSTOMER_TABLE = 'Customer';
var POINTS_TABLE = 'Points';
var INTERVAL_TABLE = 'Interval';
var INTERVAL_REC_TABLE = 'IntervalRecord';

var NUM_MIN_ARGS = 3;

var CUST_MIN_DUR = 5;
var CUST_MAX_DUR = 25;
var POINTS_PER_5_MIN = 1;
var CHANCE_CUST_IN_STORE = 0.1;
var REWARDS_ON = false;

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

var saveRewardData = function (date, duration, business, customer) {
    var pointsObj = Parse.Object.extend(POINTS_TABLE);
    var points = new pointsObj();

    points.set('business', business);
    points.set('customer', customer);
    points.set('firstVisit', date.toDate());
    points.set('points', Math.floor(duration/5)*POINTS_PER_5_MIN);

    points.save(null, {error: function (obj, error) {console.log('saveRewardData: '+ error.message);}});
};

var saveIntervalRecData = function (intervalObj, startDate, endDate, business, customer) {
    for (var i=startDate.clone(); i<endDate; i.add(5,'minutes')) {
        var intervalRecObj = Parse.Object.extend(INTERVAL_REC_TABLE);
        var intervalRec = new intervalRecObj();

        intervalRec.set('interval', intervalObj);
        intervalRec.set('timestamp', i.toDate());
        intervalRec.set('coordX', getRandomArbitrary(1,10));
        intervalRec.set('coordY', getRandomArbitrary(1,10));

        intervalRec.save(null, {error: function (obj, error) {console.log('saveIntervalRecData: '+ error.message );}});
    }
};

var saveIntervalData = function(business, customer, startDate) {
    var intervalObj = Parse.Object.extend(INTERVAL_TABLE);
    var interval = new intervalObj();
    var durationMin = getRandomInt(CUST_MIN_DUR, CUST_MAX_DUR);
    var endDate = startDate.clone().add(durationMin, 'minutes');

    interval.set('business', business);
    interval.set('customer', customer);
    interval.set('from', startDate.toDate()); // dates are incorrectly inserted for some reason
    interval.set('to', endDate.toDate());

    interval.save(null, {
        success: function (obj) {
            saveIntervalRecData(obj, startDate.clone(), endDate.clone(), business, customer);
            if (REWARDS_ON) {
                saveRewardData(startDate.clone(), durationMin, business, customer);
            }
        },
        error: function (obj, error) {
            console.log('saveIntervalData: '+ error.message);
        }
    });
};

var insertIntervals = function(business, customer, date) {
    //var startDate = new Date(date+'T10:00:00');
    var startDate = new moment(date+'T10:00:00');
    var endDate = new moment(date+'T15:00:00');

    for (var date=startDate.clone(); date < endDate; date.add(5, 'minutes')) {
        var curDate = date.clone();

        if (Math.random() < CHANCE_CUST_IN_STORE) {
            console.log('insert: '+curDate.toString());
            saveIntervalData(business, customer, curDate);
        }
    }
};

var generateData = function (businessName, username, days) {
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
    if (days == null || days < 1) {
        console.log('generateIntervalData: days is < 1');
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

        insertIntervals(business, customer, days);
    });
};

var getArgs = function (argc, argv) {
    var args = argv;
    var argsObj = {};

    args.splice(0,2);

    while(args.length > 0) {
        var temp = null;

        switch (args[0]) {
            case '-b': // business
                temp = args.splice(0,2);
                argsObj.business = temp[1];
                break;
            case '-c': // customer
                temp = args.splice(0,2);
                argsObj.username = temp[1];
                break;
            case '-d': // date
                temp = args.splice(0,2);
                argsObj.date = temp[1];
                break;
            case '-p': // percent chance
                temp = args.splice(0,2);
                argsObj.pchance = temp[1];
                break;
            case '-r': // rewards flag
                temp = args.splice(0,1);
                REWARDS_ON = true;
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
        console.log('USAGE: node index.js [-b business] [-c customer username] [-d date (YYYY-MM-DD) [OPTIONAL: -p percent chance (0.0-1.0)]] [-r add rewards]');
        process.exit(1);
    }

    Parse.initialize(APP_ID, MASTER_KEY);

    var argsObj = getArgs(argc, argv);

    var businessName = argsObj.business;
    var username = argsObj.username;
    var date = argsObj.date;

    if (argsObj.pchance) {
        CHANCE_CUST_IN_STORE = argsObj.pchance;
    }

    generateData(businessName, username, date);
};


main(process.argv.length, process.argv);


