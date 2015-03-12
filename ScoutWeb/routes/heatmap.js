var express = require('express');
var router = express.Router();
var Parse = require('parse').Parse;
var parseHandler = require('parse-handler');

router.get('/', function(req, res, next) {
    if (Parse.User.current()) {
        res.render('heatmap', { title: 'Scout', filename: 'heatmap' } );
    } else {
        res.redirect('/');
    }
});

// routing to test retrieveIntervalJSON
router.get('/retrieveIntervalJSON', function(req, res, next) {
    var successCb = function (interval) {
        res.json(interval); // TODO: need to populate
    }
    var errorCb = function (interval, error) {
        console.log(error);
        res.status(400).send('ERROR: Cannot retrieve interval');
    }
    parseHandler.retrieveIntervalJSON(successCb, errorCb, {'user': Parse.User.current()});
});

// routing to test retrieveIntervalRecordsJSON
router.get('/retrieveIntervalRecordsJSON', function(req, res, next) {
    var successCb = function (intervalRecords) {
        res.json(intervalRecords); // TODO: need to populate
    }
    var errorCb = function (intervalRecords, error) {
        console.log(error);
        res.status(400).send('ERROR: Cannot retrieve interval');
    }
    parseHandler.retrieveIntervalRecordsJSON(successCb, errorCb, {'user': Parse.User.current()});
});

router.get('/getheatmap', function(req, res, next) {

    // Data contains max & min values and an array of point objects (TODO: replace with real data)
    // For now generate some random data
    var points = [];
    var max = 0;
    var width = 1281;
    var height = 778; //TODO: Change based on image size
    var len = 200;

    while (len--) {
      var val = Math.floor(Math.random()*100);
      max = Math.max(max, val);
      var point = {
        x: Math.floor(Math.random()*width),
        y: Math.floor(Math.random()*height),
        value: val
      };
      points.push(point);
    }
    // heatmap data format
    var data = { 
      max: max, 
      data: points 
    };

    res.json(data);


    // TODO: Used for animated heatmap for we decide to implement
    // setInterval(function () {
    //   // Get a new day's data every 10 intervals
    //   if (intervalCounter == 10){
    //     intervalCounter = 0;
    //     getAnotherDay(); 
    //   } else {
    //     intervalCounter++;
    //   }

    //   // Create new array for the next frame's points, remove old points, add new points, then update and push to map
    //   var newData = [];
    //   for(var j=0; j < data.data.length; j++) {
    //     var point = data.data[j];

    //     if(point.value >= 10) {
    //       point.fresh = false;
    //     }

    //     // Fade in fresh points, fade out unfresh points
    //     if(point.fresh) {
    //       point.value = point.value + .8;
    //     } else {
    //       point.value = point.value - .1;
    //     }
            
    //     if(point.value > 0) {
    //       newData.push(data.data[j]);
    //     }
    //   }
    //   data.data = newData;
          
    //   heatmapLayer.setData(data);
    // }, 100);



});

module.exports = router;