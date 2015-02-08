var express = require('express');
var router = express.Router();
var Parse = require('parse').Parse;

router.get('/', function(req, res, next) {
  res.render('rewards', { title: 'Scout' });
});

router.post('/', function (req, res) {

});

module.exports = router;
