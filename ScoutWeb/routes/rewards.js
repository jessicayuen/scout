var express = require('express');
var router = express.Router();
var Parse = require('parse').Parse;

router.get('/', function(req, res, next) {
  res.render('rewards', { title: 'Scout' , jumbotron: 'Rewards' });
});

router.get('/rewardslist', function(req, res, next) {
  var RewardObj = Parse.Object.extend("Reward");
  var query = new Parse.Query(RewardObj);

  query.find({
    success: function(list) {
      var rewards = [];
      /*
      list.forEach(function (reward) {
        console.log('\n DUMP:\n %j\n', reward);
      });
      */
      res.json(list);
    }
  });
});

router.post('/', function (req, res) {

});

module.exports = router;
