var express = require('express');
var router = express.Router();
var Parse = require('parse').Parse;

router.get('/', function(req, res, next) {
  res.render('index', { title: 'Scout' });
});

router.post('/', function (req, res) {
  var email = req.body['email'];
  var password = req.body['password'];

  Parse.User.logIn(email, password, {
    success: function(user) {
      res.redirect('/dashboard');
    },
    error: function(user, error) {
      // put up error message
      res.redirect('/');
    }
  });
});

module.exports = router;
