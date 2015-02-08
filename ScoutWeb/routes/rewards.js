var express = require('express');
var router = express.Router();
var Parse = require('parse').Parse;

router.get('/', function(req, res, next) {
  if (Parse.User.current()) {
    res.render('rewards', { title: 'Scout' , jumbotron: 'Rewards' });
  } else {
    res.redirect('/');
  }
});

router.get('/getrewards', function(req, res, next) {
  var RewardObj = Parse.Object.extend("Reward");
  var query = new Parse.Query(RewardObj);

  query.find({
    success: function(list) {
      res.json(list);
    }
  });
});

router.post('/removereward', function (req, res) {
  var rewardObjectId = req.body['objectid'];

  var RewardObj = Parse.Object.extend("Reward");
  var query = new Parse.Query(RewardObj);

  query.get(rewardObjectId, {
    success: function (reward) {
      reward.destroy();
      console.log('Reward has been successfully deleted.');

      res.redirect('/rewards');
    },
    error: function (object, error) {
      console.log('ERROR: Cannot delete reward.');

      res.redirect('/rewards');
    }
  });
});

module.exports = router;
