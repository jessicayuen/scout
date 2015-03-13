var express = require('express');
var router = express.Router();
var Parse = require('parse').Parse;


// Mock data
// Hopefully we get some points data in parse soon!
var data = {
    new: {
        daily: 9999,
        monthly: 9999
    },
    totcustomers: 9999,
    points: {
        earned: 9999,
        redeemed: 9999
    }
};


router.get('/', function(req, res, next) {

    if (Parse.User.current()) {
        res.render('dashboard', { title: 'Scout', jumbotron: 'Overview', filename: 'dashboard', data: data});
    } else {
        res.redirect('/');
    }
});


router.get('/index', function(req, res, next) {

        // fetch actual points count for a business. 
        var businessObj = Parse.Object.extend('Business');
        var pointsObj = Parse.Object.extend('Points');

        var queries = [];
        var businessQuery = new Parse.Query(businessObj);
        var pointsQuery = new Parse.Query(pointsObj);
        queries.push(pointsQuery);

        businessQuery.equalTo('owner', Parse.User.current());

        businessQuery.first().then( function(business) {
            pointsQuery.equalTo('business', business);
            pointsQuery.count().then( function(count) {
                    data.new.daily = count;
                    res.json(data);
                    console.log(data);
                }, function(error) {
                    console.log(error);
                });
        }, function(error) {
            console.log('ERROR: Could not query business');
            console.log(error.message);
        });

        // Return json when all the queries finish
        // this doesn't work :(
        Parse.Promise.when(queries).then( function() {
            console.log(data);
        });
});


router.get('/gethistoric', function(req, res, next) {
    // this is just using mock data at the moment
    
    var dataList = [
        {'date':'2013-01','value': 53 },
        {'date':'2013-02','value': 165},
        {'date':'2013-03','value': 269},
        {'date':'2013-04','value': 344},
        {'date':'2013-05','value': 376},
        {'date':'2013-06','value': 410},
        {'date':'2013-07','value': 421},
        {'date':'2013-08','value': 405},
        {'date':'2013-09','value': 376},
        {'date':'2013-10','value': 359},
        {'date':'2013-11','value': 392},
        {'date':'2013-12','value': 433},
        {'date':'2014-01','value': 455},
        {'date':'2014-02','value': 478},
    ];

    res.json(dataList);
});

module.exports = router;
