var express = require('express');
var router = express.Router();
var Parse = require('parse').Parse;
var parseHandler = require('parse-handler');

router.get('/', function(req, res, next) {
    if (Parse.User.current()) {
        res.render('heatmap', { title: 'Scout', banner:'Heat Map', filename: 'heatmap' } );
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

// routing to retrieveIntervalRecordsJSON
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

// routing to retrieveBeaconsJson
router.get('/retrieveBeaconsJSON', function(req, res, next) {
    var successCb = function (beacons) {
        res.json(beacons);
    }
    var errorCb = function (beacons, error) {
        console.log(error);
        res.status(400).send('ERROR: Cannot retrieve beacons');
    }
    parseHandler.retrieveBeaconsJSON(successCb, errorCb, {'user': Parse.User.current()});
});

router.post('/addBeacon', function (req, res) {
  var BeaconObj = Parse.Object.extend('Beacon');
  var BusinessObj = Parse.Object.extend('Business');

  var name = req.body['name'];
  var coordX = req.body['coordX'];
  var coordY = req.body['coordY'];

  console.log('name: '+ name + ', coordX: '+ coordX + ', coordY: '+ coordY);

  var businessQuery = new Parse.Query(BusinessObj);
  businessQuery.equalTo('owner', Parse.User.current());
  businessQuery.first().then( function(business) {
  
    var beacon = new BeaconObj();
    beacon.set("name", name);
    beacon.set("coordX", parseFloat(coordX));
    beacon.set("coordY", parseFloat(coordY));

    beacon.save(null, {
      success: function(beacon) {
        console.log('Beacon has been successfully saved.');

        res.status(200).send();
      },
      error: function(beacon, error) {
        console.log('ERROR: Unable to save beacon.');
        console.log(error.message);

        res.status(400).send('Beacon could not be successfully saved.');
      }
    });
  }, function(error) {
    console.log('ERROR: Could not query business');
    console.log(error.message);
    
    res.status(400).send('Unable to find the current business.');
  });
});

router.put('/editBeacon', function (req, res) {
  var beaconId = req.body['objectId'];
  var name = req.body['name'];
  var coordX = req.body['coordX'];
  var coordY = req.body['coordY'];

  var BeaconObj = Parse.Object.extend('Beacon');
  var query = new Parse.Query(BeaconObj);

  query.get(beaconId, {
    success: function (beacon) {
      beacon.set('name', name);
      beacon.set('coordX', parseFloat(coordX));
      beacon.set('coordY', parseFloat(coordY));

      beacon.save(null , {
        success: function (beacon) {
          console.log('Beacon '+ beaconId + 'has been successfully updated.');
          
          res.status(200).send();
        },
        error: function(beacon, error) {
          console.log('ERROR: Cannot update beacon '+ beaconId);
          console.log(error.message);

          res.status(400).send('Unable to update beacon'+ beaconId + '.')
        }
      });
    },
    error: function (error) {
      console.log('ERROR: Cannot query beacon '+ beaconId);

      res.status(400).send('Unable to find the beacon '+ beaconId + '.');
    }
  });
});

router.post('/removeBeacon', function (req, res) {
  var beaconObjectId = req.body['objectid'];
  var BeaconObj = Parse.Object.extend('Beacon');
  var query = new Parse.Query(BeaconObj);

  query.get(beaconObjectId, {
    success: function (beacon) {
      beacon.destroy();
      console.log('Beacon ' + beaconObjectId + ' has been successfully deleted.');

      res.status(200).send();
    },
    error: function (error) {
      console.log('ERROR: Cannot delete beacon ' + beaconObjectId + ' or is already deleted.');

      res.status(400).send('Unable to delete beacon ' + beaconObjectId + '.');
    }
  });
});

// routing for testing data
router.get('/getTestHeatmap', function(req, res, next) {

    // Data contains max & min values and an array of point objects (TODO: replace with real data)
    // For now generate some random data
    var max = 0;
    var width = 1281;
    var height = 778; //TODO: Change based on image size

    var minLng = -80;
    var maxLng = 80;
    var minLat = (-97.125/2);
    var maxLat = (97.125/2);

    var arrlen = 10;
    var len = 20;
    var intervals = [];

    // heatmap data format
    var data = { 
    };

    for (var i = 0;i<arrlen;i++) {
        var points = [];
        while (len--) {
          var val = Math.floor(Math.random()*100);
          max = Math.max(max, val);
                    var start = new Date();
          var end = start;
          end.setDate(end.getDate() + 5);
           var randomDate = new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime())).toISOString();
            var timestamp = {
                __type: 'Date',
                iso: randomDate
            };
          var point = {
            coordX: Math.floor(Math.random()*(maxLng-minLng))+minLng,
            coordY: Math.floor(Math.random()*(maxLat-minLat))+minLat,
            timestamp: timestamp
          };
          points.push(point);
        }
        len = 20;
            console.log(points);
        data[i] = points;
    }
    console.log(data);
    res.json(data);

});

module.exports = router;
