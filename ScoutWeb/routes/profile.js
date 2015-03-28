var express = require('express');
var router = express.Router();
var Parse = require('parse').Parse;
var parseHandler = require('parse-handler');

router.get('/', function(req, res, next) {
  if (Parse.User.current()) {
    res.render('profile', { title: 'Scout', jumbotron: 'Profile', filename: 'profile' });
  } else {
    res.redirect('/');
  }
});


module.exports = router;
