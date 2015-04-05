var express = require('express');
var router = express.Router();
var Parse = require('parse').Parse;
var moment = require('moment'); 

// Mock data
var data = {
    new: {
        daily: 'N/A',
        monthly: 'N/A'
    },
    visitlength: 'N/A',
    points: {
        earned: 'N/A',
        avg: 'N/A'
    }
};


// Fetch a parse object collection for the current user's business by its string
// and return its json representation.
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
        res.render('dashboard', { title: 'Scout', banner: 'Overview', filename: 'dashboard', data: data});
    } else {
        res.redirect('/');
    }
});


router.get('/index', function(req, res, next) {
    doStuffToMuhObjectJSON('Points', function(json) {
        // new daily customers with points
        data.new.daily = json.filter( function(point) {
            date = new Date(point.firstVisit);
            return date.setDate(date.getDate() + 1) > new Date();
        }).length;
        // new monthly customers with points
        data.new.monthly = json.filter( function(point) {
            date = new Date(point.firstVisit);
            return date.setMonth(date.getMonth() + 1) > new Date();
        }).length;
        // points earned
        data.points.earned = json.reduce( function(a, b) {
            return a + b.points;
        }, 0);
        // average points
        data.points.avg = data.points.earned / json.length;
    }).then( function() {
        doStuffToMuhObjectJSON('Interval', function(json)  {
            // average the durations sum / count
            var visitlength = json.reduce( function(a, b) {
                return a + (new Date(b.to.iso) - new Date(b.from.iso));
            }, 0) / json.length;
            data.visitlength = moment.duration(visitlength).humanize();
        res.json(data);
        });
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
    doStuffToMuhObjectJSON('Interval', function(json) {
        // get dates, and unique visits counts binned by said dates.
        var counts =  {};
        var arr = [];
        json.forEach( function(interval) {
            var d = moment(interval.from.iso);
            d.startOf('day');
            counts[+d] = 1 + (counts[+d] || 0);
        });
        for (key in counts)
            arr.push({x: key, y: counts[key]});
        // sort entries by date.
        customerData[0].values = arr.sort( function(a, b) {
            return a.x - b.x;
        });

    }).then( function() { 
        doStuffToMuhObjectJSON('Points', function(json) {
            // bin dates for points data (where unique business-customer
            // relationships should first be instatiated... eventually)
            var counts = {};
            var arr = [];
            json.forEach( function(point) {
                var d = moment(point.firstVisit.iso);
                d.startOf('day');
                counts[+d] = 1 + (counts[+d] || 0);
            });
            for (key in counts)
                arr.push({x: parseInt(key), y: counts[key]});
        customerData[1].values = arr.sort( function(a, b) {
            return a.x - b.x;
        });
            res.json(customerData);
        });
    });
});

module.exports = router;
