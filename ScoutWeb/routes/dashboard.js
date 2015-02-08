var express = require('express');
var router = express.Router();
var Parse = require('parse').Parse;

router.get('/', function(req, res, next) {
  res.render('dashboard', { title: 'Scout' });
});

module.exports = router;