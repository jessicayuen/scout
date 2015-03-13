var express = require('express');
var router = express.Router();
var Parse = require('parse').Parse;


// Mock data
// Hopefully we get more points data in parse soon!
var data = {
    new: {
        daily: 'N/A',
        monthly: 'N/A'
    },
    totcustomers: 'N/A',
    points: {
        earned: 'N/A',
        redeemed: 'N/A'
    }
};


// Please rename this function. I dunno how to describe. I need sleep.
var doStuffToMuhObjectJSON = function(objName, stuff) {
    var businessObj = Parse.Object.extend('Business');
    var businessQuery = new Parse.Query(businessObj);
    var query = new Parse.Query(Parse.Object.extend(objName));
    businessQuery.equalTo('owner', Parse.User.current());

    return businessQuery.first().then( function(business) {
        // queries for this page.
        query.equalTo('business', business);
        return query.collection().fetch();
    }).then( function (collection) {
        // do stuff to muh JSON
        stuff(collection.toJSON());
    });
};


router.get('/', function(req, res, next) {

    if (Parse.User.current()) {
        res.render('dashboard', { title: 'Scout', jumbotron: 'Overview', filename: 'dashboard', data: data});
    } else {
        res.redirect('/');
    }
});


router.get('/index', function(req, res, next) {
    doStuffToMuhObjectJSON('Points', function(json) {
        // new daily customers with points
        data.new.daily = json.filter( function(point) {
            date = new Date(point.createdAt);
            return date.setDate(date.getDate() + 1) > new Date();
        }).length;
        // new monthly customers with points
        data.new.monthly = json.filter( function(point) {
            date = new Date(point.createdAt);
            return date.setMonth(date.getMonth() + 1) > new Date();
        }).length;
        res.json(data);
    });
});


router.get('/points', function(req, res, next) {
    var pointsData = [{
        key : 'Points',
        values : []
    }];
    doStuffToMuhObjectJSON('Points', function(json) {
        // map collection into series, with js timestamps
        var series = json.map( function (point) {
            return {
                x: +new Date(point.createdAt),
                y: point.points,
                size: 50
            };
        });
        // set and display
        pointsData[0].values = series;
        res.json(pointsData);
    });
});


router.get('/customers', function(req, res, next) {
    var customerData = [
        {
            key : 'Visits',
            values : []
        },
        {
            key : 'New Customers',
            values : []
        },
    ];
    doStuffToMuhObjectJSON('NEW_Interval', function(json) {
        // get dates, and unique visits counts binned by said dates.
        var counts =  {};
        json.forEach( function(interval) {
            var d = new Date(interval.from.iso);
            d.setHours(0,0,0,0);
            counts[+d] = 1 + (counts[+d] || 0);
        });
        for (key in counts)
            customerData[0].values.push({x: parseInt(key), y: counts[key]});

    }).then( function() {
        // bin dates for points data (where unique business-customer
        // relationships should first be instatiated... eventually)
        var counts = {};
        doStuffToMuhObjectJSON('Points', function(json) {
            console.log(json);
            json.forEach( function(point) {
                var d = new Date(point.createdAt);
                d.setHours(0,0,0,0);
                counts[+d] = 1 + (counts[+d] || 0);
            });
            console.log(counts);
            for (key in counts)
                customerData[1].values.push({x: parseInt(key), y: counts[key]});
            res.json(customerData);
        });
    });
});

module.exports = router;
